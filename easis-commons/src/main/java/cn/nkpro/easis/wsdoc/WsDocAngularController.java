package cn.nkpro.easis.wsdoc;

import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.wsdoc.model.Ctrl;
import cn.nkpro.easis.wsdoc.model.Fun;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/wsdoc")
public class WsDocAngularController implements ApplicationContextAware {

	private ApplicationContext applicationContext;
	private Class<Object> ctrlType = Object.class;


	@Value("${spring.application.name}")
	private String applicationName;

	@ResponseBody
	@RequestMapping(value = "",produces="text/html;charset=UTF-8")
	public String _default() {
		return "<script>location.href='wsdoc/index.html';</script>";
	}

	/**
	 * 获取所有接口类
	 * @return 接口类
	 */
	@ResponseBody
	@RequestMapping(value = "/controllers")
	public List<Ctrl> controllers(){

		Map<String, Object> beans = new HashMap<>();
		beans.putAll(applicationContext.getBeansWithAnnotation(Controller.class));
		beans.putAll(applicationContext.getBeansWithAnnotation(RestController.class));

		return beans
				.entrySet()
				.stream()
				.filter(entry -> ClassUtils.getUserClass(entry.getValue()).isAnnotationPresent(NkNote.class))
				.map(entry -> {

					Class<?> controllerType = ClassUtils.getUserClass(entry.getValue());
					NkNote nkController = controllerType.getAnnotation(NkNote.class);

					Ctrl ctrlConfig = new Ctrl();
					ctrlConfig.setBeanName(entry.getKey());
					ctrlConfig.setCtrlName(nkController.value());
					ctrlConfig.setCtrlDesc(nkController.desc());
					ctrlConfig.setFuncs(methods(controllerType));

					return ctrlConfig;

				})
				.sorted()
				.collect(Collectors.toList());
	}

	/**
	 * 获取接口类详情
	 * @param beanName beanName
	 * @return 详情
	 */
	@ResponseBody
	@RequestMapping("/controller/{beanName}")
	public Ctrl controller(@PathVariable("beanName")String beanName){

		Object ctrl = applicationContext.getBean(beanName, ctrlType);

		Class<?> controllerType = ClassUtils.getUserClass(ctrl);
		NkNote ctrlNote = controllerType.getAnnotation(NkNote.class);

		Ctrl ctrlConfig = new Ctrl();
		ctrlConfig.setBeanName(beanName);
		ctrlConfig.setCtrlName(ctrlNote != null ? ctrlNote.value() : controllerType.getName());
		ctrlConfig.setCtrlDesc(ctrlNote != null ? ctrlNote.desc():"");
		ctrlConfig.setFuncs(methods(controllerType));

		return ctrlConfig;
	}

	/**
	 * 获取指定类的接口列表
	 * @param controllerType controllerType
	 * @return 获取指定类的接口列表
	 */
	private List<Fun> methods(Class<?> controllerType){

		RequestMapping ctrlRequestMapping = controllerType.getAnnotation(RequestMapping.class);
		String ctrlMappingURL = ctrlRequestMapping != null ? ctrlRequestMapping.value()[0] : "";

		return Arrays
				.stream(controllerType.getMethods())
				.filter(method-> method.getAnnotation(RequestMapping.class)!=null||
							method.getAnnotation(GetMapping.class)!=null||
						method.getAnnotation(PostMapping.class)!=null)
				.map(method->{
					Fun methodConfig = new Fun();
					String mapping = GetMethodMapping(method);
					NkNote methodNote = method.getAnnotation(NkNote.class);
					// NOTES
					methodConfig.setFuncName(methodNote!=null?methodNote.value():mapping);
					methodConfig.setFuncMapping(method.getName()+"@"+mapping.replaceAll("[/.]", "!"));
					methodConfig.setFuncUrl(ctrlMappingURL + mapping);

					return methodConfig;
				})
				.sorted()
				.collect(Collectors.toList());
	}

	private String GetMethodMapping(Method method){

		String[] mappings =  GetMethodMappings(method);
		return  mappings!=null&&mappings.length>0?mappings[0]:"";
	}

	private String[] GetMethodMappings(Method method){

		RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
		if(methodRequestMapping!=null)
			return  methodRequestMapping.value();

		GetMapping methodGetMapping = method.getAnnotation(GetMapping.class);
		if(methodGetMapping!= null)
			return  methodGetMapping.value();

		PostMapping methodPostMapping = method.getAnnotation(PostMapping.class);
		if(methodPostMapping!= null)
			return  methodPostMapping.value();

		PutMapping methodPutMapping = method.getAnnotation(PutMapping.class);
		if(methodPutMapping!= null)
			return  methodPutMapping.value();

		DeleteMapping methodDeleteMapping = method.getAnnotation(DeleteMapping.class);
		if(methodDeleteMapping!= null)
			return  methodDeleteMapping.value();org.springframework.web.bind.annotation.

		PatchMapping methodPatchMapping = method.getAnnotation(PatchMapping.class);
		if(methodPatchMapping!= null)
			return  methodPatchMapping.value();

		return  null;
	}



	/**
	 * 获取接口详情
	 * @param beanName beanName
	 * @param funcMapping funcMapping
	 * @return 获取接口详情
	 */
	@ResponseBody
	@RequestMapping("/controller/{beanName}/{funcMapping}")
	public Map<String, Object> method(
			@PathVariable("beanName")String beanName,
			@PathVariable("funcMapping")String funcMapping){

		String[] funcMappings = funcMapping.split("[@]");
		String funcMappings0 = funcMappings[0];
		String funcMappings1 = funcMappings.length>1?funcMappings[1]: "";

		Object ctrl = applicationContext.getBean(beanName, ctrlType);
		Method method = Arrays.stream(ClassUtils.getUserClass(ctrl).getMethods())
			.filter(item->{

				if(!item.getName().equals(funcMappings0)){
					return false;
				}

				String[] mappings = GetMethodMappings(item);
				return mappings!=null && Arrays.stream(mappings)
					.anyMatch(m->m.replaceAll("[/.]", "!").equals(funcMappings1));
			})
			.findFirst()
			.orElse(null);
		
		if(method==null)return null;

		// crtl...
		Class<?> controllerType = ClassUtils.getUserClass(ctrl);
		RequestMapping ctrlRequestMapping = controllerType.getAnnotation(RequestMapping.class);
		String ctrlMappingURL = ctrlRequestMapping != null ? ctrlRequestMapping.value()[0] : "";

		// method...
		Map<String, Object> methodConfig = new HashMap<>();

		String methodUrl;
		RequestMapping methodRequestMapping = method.getAnnotation(RequestMapping.class);
		if(methodRequestMapping!= null){
			methodUrl = methodRequestMapping.value()[0];

			if (methodRequestMapping.method().length == 0) {
				methodConfig.put("method", "GET|POST");
			} else {
				StringBuilder builder = new StringBuilder();
				for (RequestMethod m : methodRequestMapping.method()) {
					builder.append('|');
					builder.append(m.name());
				}
				if (builder.length() > 0)
					builder.deleteCharAt(0);
				methodConfig.put("method", builder);
			}

		}else{
			GetMapping methodGetMapping = method.getAnnotation(GetMapping.class);
			if(methodGetMapping!=null){
				methodUrl = methodGetMapping.value()[0];
				methodConfig.put("method", "GET");
			}else{
				PostMapping methodPostMapping = method.getAnnotation(PostMapping.class);
				methodUrl = methodPostMapping.value()[0];
				methodConfig.put("method", "POST");
			}
		}

		NkNote methodNote = method.getAnnotation(NkNote.class);

		// URL
		methodConfig.put("url", ctrlMappingURL + methodUrl);



		if(methodNote!=null) {
			// NOTES
			methodConfig.put("notes", methodNote.value());

			// DESC
			methodConfig.put("desc", methodNote.desc());

			// WARN
			methodConfig.put("warn", methodNote.warn());

			// DEPENDENTENUMS
			methodConfig.put("dependentEnums", Arrays.stream(methodNote.dependentEnums())
					.map(clazz -> {
						NkNote codeNotes = clazz.getAnnotation(NkNote.class);
						Map<String, Object> m = new HashMap<>();
						m.put("name", codeNotes == null ? clazz.getSimpleName() : codeNotes.value());
						m.put("type", clazz.getSimpleName());
						m.put("items", Arrays.stream(clazz.getEnumConstants()).map(item -> (WsConstant<?, ?>) item)
								.collect(Collectors.toMap(
										WsConstant::getCode, WsConstant::getDesc,
										(a, b) -> b,
										LinkedHashMap::new)));
						return m;
					})
					.collect(Collectors.toList()));


			// RETUR
			methodConfig.put("retur", methodNote.retur());
			methodConfig.put("returContent", ReturnTypeAnalyser.analyse(method.getReturnType(), 1, null).trim());
		}

		// DEPRECATED
		methodConfig.put("deprecated", method.getAnnotation(Deprecated.class));

		// ISRESPONSEBODY
		methodConfig.put("isResponseBody", method.getAnnotation(ResponseBody.class) != null);

		// EXCEPTION
		Class<?>[] exceptions = method.getExceptionTypes();
		if (exceptions.length > 0) {
			List<Map<String, Object>> exceptionConfigs = new ArrayList<>();
			for (Class<?> exception : exceptions) {
				NkNote eNotes = exception.getAnnotation(NkNote.class);
				if (eNotes != null) {
					Map<String, Object> exceptionConfig = new HashMap<>();
					exceptionConfig.put("code", eNotes.value());
					exceptionConfig.put("desc", eNotes.desc());
					exceptionConfigs.add(exceptionConfig);
				}
			}
			if (!exceptionConfigs.isEmpty())
				methodConfig.put("exceptions", exceptionConfigs);
		}
		// PARAMS
		List<Map<String, Object>> methodParams = new ArrayList<>();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		Class<?>[] types = method.getParameterTypes();

		Map<String, Object> paramConfig = null;
		for (int i = 0; i < types.length; i++) {

			Annotation[] annos = parameterAnnotations[i];
			for (Annotation anno : annos) {
				if (anno.annotationType() == RequestParam.class) {
					paramConfig = new HashMap<>();
					RequestParam rp = (RequestParam) anno;
					paramConfig.put("paramType", "RequestParam");
					paramConfig.put("name", rp.value());
					Class<?> type = types[i];
					if (type.isArray()) {
						paramConfig.put("type", type.getComponentType().getName() + "[]");
					} else {
						paramConfig.put("type", type.getName());
					}
					paramConfig.put("required", rp.required());
				}
				if (anno.annotationType() == PathVariable.class) {
					paramConfig = new HashMap<>();
					PathVariable pv = (PathVariable) anno;
					paramConfig.put("paramType", "PathVariable");
					paramConfig.put("type", String.class.getName());
					paramConfig.put("name", "{" + pv.value() + "}");
					paramConfig.put("required", true);
				}
				if(anno.annotationType() == RequestBody.class){
					paramConfig = new HashMap<>();
					paramConfig.put("name", "@RequestBody");
					paramConfig.put("paramType", "RequestBody");
					Class<?> type = types[i];
					if (type.isArray()) {
						paramConfig.put("type", type.getComponentType().getName() + "[]");
					} else {
						paramConfig.put("type", type.getName());
					}
					paramConfig.put("required", true);
					paramConfig.put("bodyFormat", ReturnTypeAnalyser.analyse(type));
				}
			}
			for (Annotation anno : annos) {
				if (anno.annotationType() == NkNote.class) {
					if (paramConfig == null) {
						paramConfig = new HashMap<>();
						paramConfig.put("paramType", "Undefined");
						Class<?> type = types[i];
						if (type.isArray()) {
							paramConfig.put("type", type.getComponentType().getName() + "[]");
						} else {
							paramConfig.put("type", type.getName());
						}
						paramConfig.put("name", "{ Unknow }");
						paramConfig.put("required", false);
					}
					NkNote c = (NkNote) anno;
					paramConfig.put("notes", c.value());
				}
			}
			if (paramConfig != null) {
				methodParams.add(paramConfig);
			}
			// clear paramConfig
			paramConfig = null;
		}
		methodConfig.put("params", methodParams);
	
		return methodConfig;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
