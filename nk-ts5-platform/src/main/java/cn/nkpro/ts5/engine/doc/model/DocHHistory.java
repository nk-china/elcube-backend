package cn.nkpro.ts5.engine.doc.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 单据数据对象，与前端交互的数据格式
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
public class DocHHistory extends DocHBasis {

    private Integer historyVersion;
    private String historyUserRealName;
    private String historyUserId;
    private Long historyCreateTime;
    private List<Object> historyChangedCards;

    public DocHHistory() {
        super();
    }
}
