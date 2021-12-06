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
package cn.nkpro.elcube.docengine.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ExceptionUtils {

    /**
     * 格式化异常信息
     * @param e Exception
     * @return 格式化的异常信息
     */
    default List<String> buildExceptionMessage(Throwable e){

        List<String> messages = new ArrayList<>();
        messages.add(String.format("<span class='t'>Caused by: %s: %s</span>",
                e.getClass().getName(),
                e.getLocalizedMessage()
        ));
        Arrays.stream(e.getStackTrace())
                .forEach(element-> messages.add(String.format("<span>at %s.%s(<span class='highlight'>%s:%d</span>)</span>",
                        element.getClassName(),
                        element.getMethodName(),
                        element.getFileName(),
                        element.getLineNumber()
                )));

        if(e.getCause()!=null){
            messages.addAll(buildExceptionMessage(e.getCause()));
        }

        return messages;
    }
}
