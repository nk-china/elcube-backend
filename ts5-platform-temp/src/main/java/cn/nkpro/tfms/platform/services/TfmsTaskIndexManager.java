package cn.nkpro.tfms.platform.services;

import cn.nkpro.tfms.platform.elasticearch.SearchEngine;
import cn.nkpro.tfms.platform.model.BizDocBase;
import cn.nkpro.tfms.platform.model.index.IndexDoc;
import cn.nkpro.tfms.platform.model.index.IndexDocItem;
import cn.nkpro.ts5.utils.BeanUtilz;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TfmsTaskIndexManager {

    @Autowired
    private SearchEngine searchEngine;
    @Autowired
    private TfmsDocService docService;

    public void indexCancel(String processInstanceId) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .postFilter(
                    QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery("dynamics.processInstanceId_keyword",processInstanceId))
                        .must(QueryBuilders.termQuery("itemState","CREATED"))
                )
                .size(10000);
        searchEngine.searchPage(IndexDocItem.class,sourceBuilder)
            .getList()
            .forEach(indexDocItem -> {
                indexDocItem.setItemState("CANCELED");
                indexDocItem.setItemStateDesc("已取消");
                searchEngine.indexAfterCommit(indexDocItem);
            });
    }

    public void index(TaskInfo task, List<String> candidates, boolean isHistoric, String docId){

        BizDocBase doc = docService.getDocDetail(docId);
        IndexDoc docIndex = docService.getDocIndex(doc);
        IndexDocItem taskIndex = BeanUtilz.copyFromObject(docIndex, IndexDocItem.class);
        taskIndex.setItemId(task.getId());
        taskIndex.setItemType("BPM:TASK");
        taskIndex.setItemName(task.getName());

        taskIndex.setItemState(     isHistoric?"COMPLETED":"CREATED");
        taskIndex.setItemStateDesc( isHistoric?"已提交":"待处理");

        taskIndex.setCreatedTime(task.getCreateTime().getTime());
        taskIndex.addDynamicField("processInstanceId_keyword",task.getProcessInstanceId());

        if(task instanceof HistoricTaskInstance){
            Date endTime = ((HistoricTaskInstance) task).getEndTime();
            if(endTime==null && isHistoric){
                endTime = new Date();
            }
            if(endTime!=null) {
                taskIndex.addDynamicField("endTime_long", endTime.getTime());
            }
        }

        if(candidates!=null){
            taskIndex.addDynamicField("assignee_keyword",candidates);
        }else if(StringUtils.isNotBlank(task.getAssignee())){
            taskIndex.addDynamicField("assignee_keyword",
                    Collections.singletonList(task.getAssignee())
            );
        }else if(task instanceof TaskEntity){
            taskIndex.addDynamicField("assignee_keyword",
                    ((TaskEntity)task).getCandidates()
                            .stream()
                            .map(IdentityLinkInfo::getUserId)
                            .collect(Collectors.toList())
            );
        }

        searchEngine.indexAfterCommit(taskIndex);
    }
}
