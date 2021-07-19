package cn.nkpro.tfms.platform.services.impl;

import cn.nkpro.tfms.platform.basis.Constants;
import cn.nkpro.tfms.platform.basis.TfmsCustomObjectManager;
import cn.nkpro.tfms.platform.custom.EnumDocClassify;
import cn.nkpro.tfms.platform.custom.doc.TfmsDocProcessor;
import cn.nkpro.ts5.exception.TfmsOperatNotAllowedCaution;
import cn.nkpro.tfms.platform.mappers.gen.BizDocMapper;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.DefDocStatusExt;
import cn.nkpro.tfms.platform.model.DefDocTypeBO;
import cn.nkpro.tfms.platform.model.po.BizDocExample;
import cn.nkpro.tfms.platform.services.TfmsDefDocTypeService;
import cn.nkpro.tfms.platform.services.TfmsPermService;
import cn.nkpro.tfms.platform.services.ThreadLocalContextHolder;
import cn.nkpro.ts5.supports.RedisSupport;
import cn.nkpro.ts5.supports.SequenceSupport;
import cn.nkpro.ts5.utils.BeanUtilz;
import cn.nkpro.ts5.utils.DateTimeUtilz;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
public class TfmsDocEngineBaseImpl {

    @Autowired
    private BizDocMapper bizDocMapper;
    @Autowired
    private SequenceSupport sequenceUtils;
    @Autowired
    private TfmsPermService permService;
    @Autowired
    private TfmsCustomObjectManager customObjectManager;
    @Autowired
    private RedisSupport<BizDocBase> redisSupport;
    @Autowired
    private TfmsDefDocTypeService defDocTypeService;

    BizDocBase execUpdate(BizDocBase doc, BizDocBase original, DefDocTypeBO def, String source, boolean ignoreStateValid){

        Assert.hasText(doc.getDocId(),"单据ID不能为空");
        Assert.hasText(doc.getDocType(),"单据类型不能为空");

        /*
         * 如果：
         *      1、原始数据不为空，即单据为存量单据，修改模式
         *      2、原始单据的identification不为空，为了兼容部分老数据修改标示为null的情况
         *      3、原始单据的identification与用户提交的数据不一致
         * 那么：
         *      表示当前用户操作的单据已经被其他用户进行过修改，为了保证数据一致性，本次修改不允许
         *      抛出异常
         */
        if(original!=null
                && StringUtils.isNotBlank(original.getIdentification())
                && !StringUtils.equals(original.getIdentification(),doc.getIdentification())){
            throw new TfmsOperatNotAllowedCaution("单据信息已被其他用户或程序修改，请刷新后重试");
        }

        // 根据单据类型获的单据处理程序
        String docType = doc.getDocType();
        TfmsDocProcessor docProcessor = customObjectManager
                .getCustomObject(def.getRefObjectType(), TfmsDocProcessor.class);
        log.debug("docType.interceptor = " + docProcessor.getClass().getName());

        /*
         * 创建新单据
         */
        if(original==null){
            log.debug("当前单据为新建单据，准备保存单据基本信息");
            doc.setUpdatedTime(DateTimeUtilz.nowSeconds());
            // 如果原始单据信息为空，那么表示单据为新建
            if(StringUtils.isBlank(doc.getDocNumber()))
                doc.setDocNumber(sequenceUtils.next(EnumDocClassify.valueOf(doc.getClassify()), doc.getDocType()));
            bizDocMapper.insert(doc);
        }else{

            BizDocExample example = new BizDocExample();

            if(doc.getIdentification()==null){
                example.createCriteria()
                        .andIdentificationIsNull()
                        .andDocIdEqualTo(doc.getDocId());
            }else{
                example.createCriteria()
                        .andIdentificationEqualTo(doc.getIdentification())
                        .andDocIdEqualTo(doc.getDocId());
            }

            // 设置新的识别标识
            doc.setIdentification(UUID.randomUUID().toString());
            doc.setUpdatedTime(DateTimeUtilz.nowSeconds());

            int i = bizDocMapper.updateByExample(doc, example);
            if(i==0){
                throw new TfmsOperatNotAllowedCaution("单据信息已被其他用户或程序修改，请刷新后重试");
            }
            log.debug("更新数据库完成");
        }

        /*
         * 这段代码注释掉
         * 不论用户是否具有卡片的权限，更新程序都会执行，只是数据会被替换成原始数据，并进过计算程序的计算
         *
         * 再次获取单据配置
         * 并过滤权限
         */
        //if(!ignorePerm){
        //    def = permService.filterDocCards(TfmsPermService.MODE_WRITE,def);
        //}

        log.debug("doc.defVersion = "+doc.getDefVersion());

        doc.setDefinedDoc(def);

        // 检查单据状态是否合法，即符合单据配置中的 状态流
        if(!ignoreStateValid){
            String docState = doc.getDocState();
            Assert.isTrue(doc.getDefinedDoc().getStatus()
                            .stream()
                            .filter(DefDocStatusExt::getAvailable)
                            .anyMatch(defDocStatus -> StringUtils.equals(defDocStatus.getDocState(), docState)),
                    "单据状态不合法");
        }

        // 执行单据处理器的更新逻辑

        doc = docProcessor.update(doc,original,source);

        // 触发单据处理器事件
        docProcessor.updateBeforeCommit(doc);

        bizDocMapper.updateByPrimaryKey(doc);

        log.debug("doc updated");

        // 执行索引规则并更新索引
        index(doc);

        // 缓存清理工作
        redisSupport.delete(String.format("%s%s", Constants.CACHE_DOC , doc.getDocId()));
        if(StringUtils.isNotBlank(doc.getRefObjectId()))
            redisSupport.delete(Constants.CACHE_DOC_HISTORY,doc.getRefObjectId());

        // 单据更新完毕后，清理本地线程中的对应的单据数据，避免出现数据不一致
        ThreadLocalContextHolder.clearDoc(doc.getDocId());
        log.debug("clear doc cache");

        return doc;
    }

    BizDocBase execGetDocDetail(String docId,boolean fromLocal,boolean includeData,boolean force){

        BizDocBase doc = fromLocal ? ThreadLocalContextHolder.getBizDoc(docId) : null;

        if(doc==null){
            String key = String.format("%s%s",Constants.CACHE_DOC , docId);
            doc = redisSupport.getIfAbsent(
                    key,
                    StringUtils.EMPTY,
                    ()-> BeanUtilz.copyFromObject(bizDocMapper.selectByPrimaryKey(docId), BizDocBase.class)
            );

            if(doc!=null){
                try{

                    DefDocTypeBO docDefinedRuntime = defDocTypeService.getDocDefinedRuntime(doc.getDocType(), doc);
                    doc.setDefinedDoc(docDefinedRuntime);

                    // 获取编辑权限
                    doc.setWriteable(permService.hasDocPerm(TfmsPermService.MODE_WRITE,docId,doc.getDocType()));

                    if(includeData){
                        doc = customObjectManager
                                .getCustomObject(doc.getDefinedDoc().getRefObjectType(), TfmsDocProcessor.class)
                                .detail(doc);
                    }

                    if(includeData)
                        ThreadLocalContextHolder.setBizDoc(doc);

                }catch (Exception e){
                    if(force){
                        doc.setException(buildExceptionMessage(e));
                        log.error(e.getMessage(),e);
                    }else{
                        throw e;
                    }
                }
            }

        }

        return doc;
    }

    public void index(BizDocBase doc) {
        customObjectManager
                .getCustomObject(doc.getDefinedDoc().getRefObjectType(), TfmsDocProcessor.class)
                .index(doc);
    }

    /**
     * 格式化异常信息
     * @param e Exception
     * @return 格式化的异常信息
     */
    private List<String> buildExceptionMessage(Throwable e){

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
