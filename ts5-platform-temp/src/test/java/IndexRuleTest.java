import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.po.BizDoc;
import cn.nkpro.tfms.platform.model.po.BizDocPartner;
import com.alibaba.fastjson.TypeReference;
import org.junit.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bean on 2020/7/29.
 */
public class IndexRuleTest {
    private ScriptEngineManager manager = new ScriptEngineManager();
    private ScriptEngine engine = manager.getEngineByName("groovy");

    @Test
    public void test() throws ScriptException {

        System.out.println(new TypeReference<List<BizDoc>>(){}.getType());

//        Bindings bindings = engine.createBindings();
        BizDocBase doc = new BizDocBase();
        doc.setDocType("ZR01");
        doc.setDocName("谢炳盛");
//
        ArrayList list = new ArrayList(){{
            add(doc);
        }};

        doc.getComponentsData().put("nk-card-doc-partner",new ArrayList(){{
            BizDocPartner partner = new BizDocPartner();
            partner.setType("LESSEE");
            partner.setPartnerName("谢炳盛");
            add(partner);
        }});
//
//
//        bindings.put("list",list);
//        System.out.println(engine.eval("return list.find{it.name == '1'}.name",bindings));
//        System.out.println(engine.eval("return list.find{it.name == '2'}.name",bindings));


        ExpressionParser parser = new SpelExpressionParser();

//        Expression exp = parser.parseExpression("#list.^[docType eq 'ZR01']?.docName");
        Expression exp = parser.parseExpression("componentsData['nk-card-doc-partner'].^[type eq 'LESSEE']?.partnerName");

        StandardEvaluationContext ctx = new StandardEvaluationContext();
        ctx.setRootObject(doc);
        //ctx.setVariable("list", list);

        Object value = exp.getValue(ctx);
        System.out.println(value);
    }
}
