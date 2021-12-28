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
package cn.nkpro.elcube.components.defaults.services

import cn.nkpro.elcube.docengine.NkDocEngine
import cn.nkpro.elcube.docengine.gen.DocDefFlow
import cn.nkpro.elcube.docengine.interceptor.abstracts.NkAbstractDocFlowInterceptor
import cn.nkpro.elcube.docengine.model.DocHV
import com.alibaba.fastjson.JSON
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.util.Assert

@SuppressWarnings("unused")
@Component("NkFollowUpDocFlowInterceptor")
class NkFollowUpDocFlowInterceptor
        extends NkAbstractDocFlowInterceptor{

    @Autowired
    NkDocEngine docEngine

    @Override
    FlowDescribe apply(DocHV docHV, DocDefFlow flow) {
        if(StringUtils.isBlank(flow.getRefObjectParams())){
            return FlowDescribe.invisible("参数配置错误")
        }

        Param param = JSON.parseObject(flow.getRefObjectParams(),Param.class)

        Assert.notEmpty(param.docType,"单据类型不能为空")
        Assert.notEmpty(param.docState,"单据状态不能为空")

        if(docEngine.find(param.docType)
                .prevIdEquals(docHV.docId)
                .docStateIn(param.docState)
                .countResult() == 0){
            return FlowDescribe.visible()
        }

        return FlowDescribe.invisible("已存在后续单据")
    }

    static class Param{
        String[] docType
        String[] docState
    }
}
