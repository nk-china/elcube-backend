package cn.nkpro.ts5.docengine.cards;

import cn.nkpro.ts5.annotation.NkNote;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

// todo 待开发卡片 相当于融资方案、金融方案
@Order(10000)
@NkNote("计划参数")
@Component("NkCardPlanningParams")
public class NkCardPlanningParams extends NkDynamicBase<Map<String,Object>, NkDynamicFormDef> {

    @Override
    public String getDataComponentName() {
        return "NkDynamicForm";
    }

    @Override
    protected String[] getDefComponentNames() {
        return new String[]{"NkDynamicFormDef"};
    }
}
