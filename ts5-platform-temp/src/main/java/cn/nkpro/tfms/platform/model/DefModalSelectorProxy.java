package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.DefModalSelector;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DefModalSelectorProxy {

    private  DefModalSelector target;

    public DefModalSelector toModalSelector(){
        return target;
    }

    public DefModalSelectorProxy() {
        target = new DefModalSelector();
    }

    public DefModalSelectorProxy(DefModalSelector target) {
        this.target = target == null?new DefModalSelector():target;
    }

    public JSONArray getSearchItems() {
        if(StringUtils.isBlank(target.getSearchItemsJson())){
            return null;
        }
        return JSON.parseArray(target.getSearchItemsJson());
    }

    public void setSearchItems(List searchItems) {
        if(searchItems!=null)
            target.setSearchItemsJson(JSON.toJSONString(searchItems));
    }

    public JSONArray getColumns() {
        if(StringUtils.isBlank(target.getColumnsJson())){
            return null;
        }
        return JSON.parseArray(target.getColumnsJson());
    }

    public void setColumns(List columns) {
        if(columns!=null)
            target.setColumnsJson(JSON.toJSONString(columns));
    }

    public JSONArray getMappings() {
        if(StringUtils.isBlank(target.getMappingsJson())){
            return null;
        }
        return JSON.parseArray(target.getMappingsJson());
    }

    public void setMappings(List mappings) {
        if(mappings!=null)
            target.setMappingsJson(JSON.toJSONString(mappings));
    }


    //


    public String getComponent() {
        return target.getComponent();
    }

    public void setComponent(String component) {
        target.setComponent(component);
    }

    public String getDocType() {
        return target.getDocType();
    }

    public void setDocType(String docType) {
        target.setDocType(docType);
    }

    public String getTitle() {
        return target.getTitle();
    }

    public void setTitle(String title) {
        target.setTitle(title);
    }

    public String getWidth() {
        return target.getWidth();
    }

    public void setWidth(String width) {
        target.setWidth(width);
    }

    public String getSource() {
        return target.getSource();
    }

    public void setSource(String source) {
        target.setSource(source);
    }

    public String getPreConditions() {
        return target.getPreConditions();
    }

    public void setPreConditions(String preConditions) {
        target.setPreConditions( preConditions);
    }

    public Long getUpdatedTime() {
        return target.getUpdatedTime();
    }

    public void setUpdatedTime(Long updatedTime) {
        target.setUpdatedTime(updatedTime);
    }
}
