/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.wsdoc;


import cn.nkpro.elcube.annotation.CodeFieldListNotes;
import cn.nkpro.elcube.annotation.CodeFieldNotes;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

public class ReturnTypeAnalyser {

	public static String analyse(Class<?> clazz) {
		return execAnalyse(new HashSet<>(), clazz, 0, null);
	}
	public static String analyse(Class<?> clazz, int level, String desc) {
		return execAnalyse(new HashSet<>(), clazz, level, desc);
	}

	private static String execAnalyse(Set<Class> hash, Class<?> clazz, int level, String desc) {


		StringBuilder buf = new StringBuilder();

		if (clazz.getName().startsWith("com.alibaba.fastjson")) {
			buf.append("<").append(clazz.getName()).append(">");
			return buf.toString();
		}

		if (clazz.getName().startsWith("org.springframework")) {
			buf.append("<").append(clazz.getName()).append(">");

			return buf.toString();
		}

		if (clazz==void.class || clazz==Void.class) {
			buf.append("<Void>");
			if(desc!=null)buf.append(desc);

			return buf.toString();
		}
		if (clazz == Class.class || clazz == Object.class) {
			if(desc!=null)buf.append(desc);

			return buf.toString();
		}
		if (isBasicType(clazz)) {
			buf.append(makeLevel(level));
			buf.append("(").append(clazz.getName()).append(") ");

			return buf.toString();
		}
		if (isImplements(clazz,Collection.class)){
			
			Type type = clazz.getGenericSuperclass();
			if(type==null){
				buf.append(makeLevel(level));
				buf.append("[ <List> ]");
				return buf.toString();
			}

			buf.append("[");
			buf.append('\n');
			buf.append(execAnalyse(hash, type.getClass(), level + 2, desc));
			buf.append('\n');
			buf.append("] ");
			return buf.toString();
		}
		if (isImplements(clazz,Map.class)){
			Type type = clazz.getGenericSuperclass();
			if(type==null){
				buf.append(makeLevel(level));
				buf.append("{ <Map> }");
				return buf.toString();
			}

			Type[] types = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();

			buf.append(makeLevel(level));
			buf.append("{");
			buf.append('\n');
			buf.append(makeLevel(level+1));
			buf.append("<KEY> :");
			buf.append(execAnalyse(hash, (Class<?>) types[0], level + 2, desc));
			buf.append('\n');
			buf.append(makeLevel(level+1));
			buf.append("<VALUE> :");
			buf.append(execAnalyse(hash, (Class<?>) types[1], level + 2, desc));
			buf.append('\n');
			buf.append(makeLevel(level));
			buf.append("} ");
			return buf.toString();

		}
		if (clazz.isArray()){
			buf.append("[");
			buf.append('\n');
			buf.append(execAnalyse(hash, clazz.getComponentType(), level + 2, desc));
			buf.append('\n');
			buf.append("] ");
			return buf.toString();
		}

		if (desc != null) {
			buf.append(makeLevel(level));
			buf.append("//").append(desc);
			buf.append('\n');
		}

		buf.append(makeLevel(level));
		buf.append('{');
		buf.append('\n');


		if(hash.contains(clazz)){
			hash.add(clazz);
			return buf.toString();
		}
		hash.add(clazz);

		Method[] methods = clazz.getMethods();

		for (Method method : methods) {
			if (method.getName().startsWith("get") && method.getName().length() > 3) {

				Class<?> returnType = method.getReturnType();

				if (returnType == Class.class) {
					continue;
				}

				if (isBasicType(returnType)) {
					buf.append(makeBuf(level + 1, method, makeFieldDesc(method)));
					continue;
				}

				if (isImplements(returnType, Map.class)) {
					buf.append(makeBuf(level + 1, method, makeFieldDesc(method)));
					continue;
				}

				if (isImplements(returnType, Collection.class)) {

					CodeFieldListNotes bgn = method.getAnnotation(CodeFieldListNotes.class);
					if (bgn != null) {

						String bufSub = "[" +
								'\n' +
								execAnalyse(hash, bgn.componentType(), level + 2, bgn.value()) +
								'\n' +
								makeLevel(level + 1) +
								']';
						buf.append(makeBuf(level + 1, method, bufSub));
						continue;
					}

					buf.append(makeBuf(level + 1, method, makeFieldDesc(method)));
					continue;
				}

				if (isArray(returnType)) {
					String d = null;
					CodeFieldNotes cfn = method.getAnnotation(CodeFieldNotes.class);
					CodeFieldListNotes cfln = method.getAnnotation(CodeFieldListNotes.class);
					if (cfn != null) {
						d = cfn.value();
					} else if (cfln != null) {
						d = cfln.value();
					}

					String bufSub = "[" +
							'\n' +
							execAnalyse(hash, returnType.getComponentType(), level + 2, d) +
							'\n' +
							makeLevel(level + 1) +
							']';
					buf.append(makeBuf(level + 1, method, bufSub));

					continue;
				}

				CodeFieldNotes cfn = method.getAnnotation(CodeFieldNotes.class);

				String bufSub = '\n' +
						execAnalyse(hash, returnType, level + 2, cfn != null ? cfn.value() : "");
				buf.append(makeBuf(level + 1, method, bufSub));
			}
		}

		buf.append(makeLevel(level));
		buf.append('}');

		return buf.toString();
	}

	private static String makeFieldDesc(Method method) {
		CodeFieldNotes cfn = method.getAnnotation(CodeFieldNotes.class);
		return method.getReturnType().getSimpleName() + " " + (cfn != null ? cfn.value() : "");
	}

	private static String makeBuf(int level, Method method, String desc) {
		return makeLevel(level) +
				'\"' + method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4) + "\" : " + desc +
				'\n';
	}

	private static String makeLevel(int level) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < level; i++) {
			buf.append("  ");
		}
		return buf.toString();
	}

	private static boolean isArray(Class<?> res) {
		return res.isArray();
	}

	private static boolean isImplements(Class<?> res, Class<?> interfaceClass) {

		if (res == interfaceClass)
			return true;

		Class<?>[] interfaces = res.getInterfaces();

		for (Class<?> i : interfaces) {
			if (i == interfaceClass)
				return true;
		}

		if (res.getSuperclass() != null)
			return isImplements(res.getSuperclass(), interfaceClass);

		return false;
	}

	private static boolean isBasicType(Class<?> res) {

		if (res.getPackage() == Integer.class.getPackage())
			return true;

		List<Class<?>> valueTypes = new ArrayList<>();
		valueTypes.add(BigDecimal.class);

		return valueTypes.contains(res);

	}
}
