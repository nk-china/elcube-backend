package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.basis.TfmsCustomObjectManager;
import cn.nkpro.tfms.platform.custom.doc.TfmsDocProcessor;
import cn.nkpro.ts5.exception.TfmsIllegalContentException;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.BizDocPreparsed;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.services.TfmsDefDocTypeService;
import cn.nkpro.tfms.platform.services.TfmsDocEngineWithPerm;
import cn.nkpro.tfms.platform.services.TfmsPermService;
import cn.nkpro.ts5.supports.RedisSupport;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class TfmsDocEngineWithPermImpl extends TfmsDocEngineBaseImpl implements TfmsDocEngineWithPerm {

    @Autowired
    private RedisSupport<BizDocBase> redisSupport;
    @Autowired
    private TfmsPermService permService;
    @Autowired
    private TfmsDefDocTypeService defDocTypeService;
    @Autowired
    private TfmsCustomObjectManager customObjectManager;

    @Override
    public BizDocBase getDetailHasDocPermForController(String docId) {
        // 检查权限
        permService.assertHasDocPerm(TfmsPermService.MODE_READ, docId,null);
        return mappingComponents(filterByReadPerm(execGetDocDetail(docId,true,true, true)));
    }

    @Override
    public boolean preUpdateForController(String docType, String docId) {
        return  permService.hasDocPerm(TfmsPermService.MODE_WRITE,docId,docType);
    }

    @Override
    public BizDocBase toCreateForController(String refObjectId, String preDocId, String docType) {
        // 检查权限
        permService.assertHasDocPerm(TfmsPermService.MODE_WRITE, docType);

        log.debug("===> 准备更新单据");
        // 单据配置数据，并过滤权限
        DefDocTypeBO def = defDocTypeService.getDocDefinedRuntime(docType, null);

        // 创建单据
        BizDocBase doc = customObjectManager
                .getCustomObject(def.getRefObjectType(), TfmsDocProcessor.class)
                .create(def, refObjectId, preDocId);
        doc.setWriteable(true);

        return mappingComponents(
                filterByReadPerm(
                        cacheRuntime(doc)
                )
        );
    }

    @Override
    public BizDocBase calculateForController(String docType, String component, String calculate, String data) {
        log.debug("docType = "+docType);

        // 加载运行时的单据数据
        BizDocBase[]        docs            = loadRuntime(data);
        BizDocBase          docRuntime      = docs[0];
        DefDocTypeBO        def             = docRuntime.getDefinedDoc();
        // 获取单据处理程序
        TfmsDocProcessor    docProcessor    = customObjectManager.getCustomObject(def.getRefObjectType(), TfmsDocProcessor.class);

        log.debug("docType.interceptor = " + docProcessor.getClass().getName());

        // 调用单据处理器的计算逻辑
        // 将计算之后的单据数据缓存1小时，在下一次计算或保存单据的时候再加载出来，避免没有权限的卡片数据丢失
        return mappingComponents(
                filterByReadPerm(
                        cacheRuntime(
                                docProcessor.calculate(component, calculate, docRuntime)
                        )
                )
        );
    }
    /**
     * 单据更新逻辑
     * @param docType 单据类型
     * @param data 单据JSON
     * @param source 请求来源
     */
    @Override
    @Transactional
    public BizDocBase doUpdateForController(String docType, String data, String source) {
        log.debug("doc update begin");
        log.debug("docType = "+docType);

        // 加载运行时的单据数据
        BizDocBase[]        docs            = loadRuntime(data);
        BizDocBase          docRuntime      = docs[0];
        BizDocBase          docOriginal     = docs[2]!=null?docs[2]:execGetDocDetail(docRuntime.getDocId(),true,true,false);
        DefDocTypeBO        def             = docRuntime.getDefinedDoc();
        // 获取单据处理程序
        TfmsDocProcessor    docProcessor    = customObjectManager.getCustomObject(def.getRefObjectType(), TfmsDocProcessor.class);

        log.debug("docType.interceptor = " + docProcessor.getClass().getName());

        // 检查单据修改权限
        if(docOriginal==null) {
            permService.assertHasDocPerm(TfmsPermService.MODE_WRITE, docType);
        }else{
            permService.assertHasDocPerm(TfmsPermService.MODE_WRITE, docOriginal.getDocId(), docType);
        }

        // 执行更新程序
        return mappingComponents(
                filterByReadPerm(
                        clearRuntime(
                            execUpdate(docRuntime, docOriginal, def, source, false)
                        )
                )
        );
    }

    @Override
    public Object callForController(String docType, String component, String event, String data) {
        /*
         * 通过单据类型 获取默认单据配置，注意这里没有指定版本号
         */
        log.debug("docType = "+docType);
        DefDocTypeBO def = defDocTypeService.getDocDefined(docType);

        TfmsDocProcessor docProcessor = customObjectManager
                .getCustomObject(def.getRefObjectType(), TfmsDocProcessor.class);
        log.debug("docType.interceptor = " + docProcessor.getClass().getName());

        BizDocBase[] docs = loadRuntime(data);

        return docProcessor.call(component, event, docs[0]);
    }



    /**
     * 根据用户输入的单据数据，进行预处理
     * 将用户权限之外的数据补充完整
     * @param input 用户输入的单据数据
     * @return 预处理后的单据数据
     */
    private BizDocBase[] loadRuntime(String input){

        // 预解析用户提交的单据数据，主要为了得到docId
        BizDocPreparsed docInput   = JSON.parseObject(input, BizDocPreparsed.class);
        log.debug("doc.docId = "+docInput.getDocId());

        DefDocTypeBO def;
        BizDocBase   docLastProcessed = null;
        BizDocBase   docOriginal = null;
        if(StringUtils.isNotBlank(docInput.getRuntimeKey())){
            docLastProcessed = redisSupport.get(docInput.getRuntimeKey());
            if(docLastProcessed==null){
                throw new TfmsIllegalContentException("操作超时，请刷新单据后重试");
            }

            def = docLastProcessed.getDefinedDoc();

        }else{
            docOriginal = execGetDocDetail(docInput.getDocId(),false,true, false);
            if(docOriginal==null){
                // 通过单据类型 获取默认单据配置，注意这里没有指定版本号
                def = defDocTypeService.getDocDefined(docInput.getDocType());
            }else{
                def = docOriginal.getDefinedDoc();
            }
        }

        // 获取单据处理程序
        TfmsDocProcessor    docProcessor    = customObjectManager.getCustomObject(def.getRefObjectType(), TfmsDocProcessor.class);

        // 通过单据处理程序完整解析单据数据
        BizDocBase          doc             = unMappingComponents(JSON.parseObject(input, docProcessor.dataType()));

        // 将用户提交的数据补充完整
        // 1、使用完整的单据配置对象
        // 2、填充完整的数据
        doc.setDefinedDoc(def);
        if(docLastProcessed!=null)
            docLastProcessed.getComponentsData().forEach((k,v)-> doc.getComponentsData().putIfAbsent(k,v));
        else if(docOriginal!=null)
            docOriginal.getComponentsData().forEach((k,v)-> doc.getComponentsData().putIfAbsent(k,v));

        // 返回 完整的doc
        return new BizDocBase[]{doc,docLastProcessed,docOriginal};
    }

    private BizDocBase cacheRuntime(BizDocBase doc){
        String runtimeDataKey = StringUtils.defaultIfBlank(doc.getRuntimeKey(),String.format("RUNTIME:%s", UUID.randomUUID().toString()));
        doc.setRuntimeKey(runtimeDataKey);
        redisSupport.set(runtimeDataKey,doc);
        redisSupport.expire(runtimeDataKey,3600);
        return doc;
    }

    private BizDocBase clearRuntime(BizDocBase doc){
        if(StringUtils.isNotBlank(doc.getRuntimeKey()))
            redisSupport.delete(doc.getRuntimeKey());
        doc.setRuntimeKey(null);
        return doc;
    }



    private BizDocBase filterByReadPerm(BizDocBase output){
        // 处理数据权限
        DefDocTypeBO defFilterByPerm = permService.filterDocCards(TfmsPermService.MODE_READ,output.getDefinedDoc());
        // 设置经过权限控制的单据配置
        output.setDefinedDoc(defFilterByPerm);
        // 清理没有权限的卡片组件
        output.getComponentsData().entrySet()
                .removeIf(entry ->
                        defFilterByPerm.getCustomComponents()
                                .stream()
                                .noneMatch(item -> StringUtils.equals(item.getComponent(),entry.getKey())));
        return output;
    }

    /**
     * 单据数据的映射与反映射
     */
    private BizDocBase mappingComponents(BizDocBase doc){
        if(doc!=null && doc.getDefinedDoc()!=null) {
            doc.getDefinedDoc()
                    .getCustomComponents()
                    .forEach(defDocComponentBO -> {
                        Object remove = doc.getComponentsData().remove(defDocComponentBO.getComponent());
                        if (remove != null) {
                            doc.getComponentsData().put(defDocComponentBO.getComponentMapping(), remove);
                        }
                    });
        }
        return doc;
    }

    /**
     * 单据数据的映射与反映射
     */
    private BizDocBase unMappingComponents(BizDocBase doc){
        if(doc!=null && doc.getDefinedDoc()!=null){
            doc.getDefinedDoc()
                    .getCustomComponents()
                    .forEach(defDocComponentBO -> {
                        Object remove = doc.getComponentsData().remove(defDocComponentBO.getComponentMapping());
                        if(remove!=null){
                            doc.getComponentsData().put(defDocComponentBO.getComponent(),remove);
                        }
                    });
        }
        return doc;
    }
}
