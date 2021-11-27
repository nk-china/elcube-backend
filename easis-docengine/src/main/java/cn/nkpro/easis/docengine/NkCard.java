package cn.nkpro.easis.docengine;

import cn.nkpro.easis.annotation.NkScriptType;
import cn.nkpro.easis.co.NkScriptCard;
import cn.nkpro.easis.docengine.model.DocDefHV;
import cn.nkpro.easis.docengine.model.DocDefIV;
import cn.nkpro.easis.docengine.model.DocHV;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@NkScriptType("Card")
public interface NkCard<DT,DDT> extends NkScriptCard {

    String POSITION_DEFAULT = "default";
    String POSITION_HEADER  = "header";
    String POSITION_SIDEBAR = "sidebar";

    default String getName(){
        return getCardName();
    }

    /**
     * 获取组件名称
     * @return string
     */
    String getCardName();

    String getPosition();

    String getDataComponentName();

    String[] getAutoDefComponentNames();

    Map<String,String> getVueTemplate();

    // 配置方法
    DDT deserializeDef(Object defContent);

    DDT afterGetDef(DocDefHV defHV, DocDefIV defIV, DDT def);

    Object callDef(DDT def, Object options);

    // 解析数据
    DT deserialize(Object data);

    // 创建方法
    DT afterCreate(DocHV doc, DocHV preDoc, DT data, DocDefIV defIV, DDT def);

    // 查询方法
    DT afterGetData(DocHV doc, DT data, DocDefIV defIV, DDT def);

    // 计算方法
    DT calculate(DocHV doc, DT data, DocDefIV defIV, DDT def, boolean isTrigger, Object options);

    // 调用方法
    Object call(DocHV doc, DT data, DocDefIV defIV, DDT def, Object options);

    /**
     * 更新前数据处理
     * 如果返回null，那么数据将不尽兴存储，但是用户录入的数据不会丢失
     * 在这个阶段，自定义卡片可自行处理数据
     */
    DT beforeUpdate(DocHV doc, DT data, DT original, DocDefIV defIV, DDT def);
    // 更新之后调用
    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    DT afterUpdated(DocHV doc, DT data, DT original, DocDefIV defIV, DDT def);

    void stateChanged(DocHV doc, DocHV original, DT data, DocDefIV defIV, DDT def);

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    default void updateCommitted(DocHV doc, DT data, DocDefIV defIV, DDT def){}

    boolean isDebug();

    default boolean enableDataDiff(){return true;}

    DT random(DocHV docHV, DocDefIV defIV, DDT def);
}
