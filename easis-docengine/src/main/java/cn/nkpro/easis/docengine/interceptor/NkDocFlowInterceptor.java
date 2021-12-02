/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.docengine.interceptor;

import cn.nkpro.easis.co.NkCustomScriptObject;
import cn.nkpro.easis.docengine.model.DocHV;
import lombok.Getter;

@SuppressWarnings("unused")
public interface NkDocFlowInterceptor extends NkCustomScriptObject {

    default FlowDescribe apply(DocHV docHV){return FlowDescribe.visible();}

    class FlowDescribe{

        @Getter
        private boolean visible;

        @Getter
        private String visibleDesc;

        private FlowDescribe(boolean visible,String visibleDesc){
            this.visible = visible;
            this.visibleDesc = visibleDesc;
        }

        @SuppressWarnings("all")
        public static FlowDescribe visible(){
            return new FlowDescribe(true,null);
        }
        public static FlowDescribe invisible(String visibleDesc){
            return new FlowDescribe(false,visibleDesc);
        }
    }
}
