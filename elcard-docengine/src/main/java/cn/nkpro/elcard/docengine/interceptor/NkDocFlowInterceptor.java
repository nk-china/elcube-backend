/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.docengine.interceptor;

import cn.nkpro.elcard.co.NkCustomScriptObject;
import cn.nkpro.elcard.docengine.model.DocHV;
import lombok.Getter;

/**
 * <h3>业务流拦截器
 *
 * <p>这个拦截器用于自定义处理业务流可见性
 *
 * @see #apply(DocHV)
 *
 * @author bean 2021-12-03
 */
@SuppressWarnings("unused")
public interface NkDocFlowInterceptor extends NkCustomScriptObject {

    /**
     * 返回一个业务流描述
     *
     * <p>根据当前活动单据或前序单据 docHV 的值返回一个 FlowDescribe 对象
     *
     * <p>当visible为false，那么该业务节点将不可用
     * <p>visibleDesc为不可用的原因描述
     *
     * <p>使用场景主要有2个：
     * <p>1、在活动单据显示时 根据自身单据 判断后续单据是否可见
     * <p>2、在活动单据创建时 根据前序单据 判断自身单据是否允许创建
     *
     * @param docHV 活动单据或前序单据
     * @return 业务流描述
     */
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

        /**
         * 返回一个可见的结果
         */
        public static FlowDescribe visible(){
            return new FlowDescribe(true,null);
        }

        /**
         *
         * 返回一个不可见的结果
         * @param visibleDesc 不可见原因
         */
        public static FlowDescribe invisible(String visibleDesc){
            return new FlowDescribe(false,visibleDesc);
        }
    }
}
