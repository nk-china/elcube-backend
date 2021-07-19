package cn.nkpro.tfms.platform.custom;

import cn.nkpro.tfms.platform.model.index.IndexDocItem;
import lombok.Data;

import java.util.List;

@Data
public class TfmsCardIndexItemExec {
    private List<IndexDocItem> update;
    private String removeItemType;

    public TfmsCardIndexItemExec(List<IndexDocItem> update, String removeItemType) {
        this.update = update;
        this.removeItemType = removeItemType;
    }
}