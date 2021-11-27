package cn.nkpro.easis.bigdata.controller;

import cn.nkpro.easis.annotation.NkNote;
import cn.nkpro.easis.bigdata.gen.DataView;
import cn.nkpro.easis.bigdata.gen.DataViewWithBLOBs;
import cn.nkpro.easis.bigdata.service.DataViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by bean on 2019/12/18.
 */
@PreAuthorize("authenticated")
@NkNote("4.数据可视化")
@RequestMapping("/dataView")
@RestController
public class DataViewController {

    @Autowired
    private DataViewService dataViewService;

    @NkNote("1、加载数据视图列表")
    @RequestMapping("/all")
    public List<? extends DataView> all(){
        return dataViewService.getAll();
    }

    @NkNote("2、加载数据视图")
    @ResponseBody
    @RequestMapping("/mload")
    public List<? extends DataView> load(@RequestBody List<String> ids){
        return dataViewService.getDetail(ids);
    }

    @NkNote("2、加载数据视图")
    @ResponseBody
    @RequestMapping("/load")
    public DataView load(String id){
        return dataViewService.getDetail(id);
    }

    @NkNote("3、保存数据视图")
    @ResponseBody
    @RequestMapping("/update")
    public DataView update(@RequestBody DataViewWithBLOBs dataView){
        dataViewService.doUpdate(dataView);
        return dataView;
    }

    @NkNote("3、保存数据视图")
    @ResponseBody
    @RequestMapping("/update/order")
    public void updateOrder(@RequestBody List<String> ids){
        dataViewService.doUpdate(ids);
    }

    @NkNote("4、删除数据视图")
    @ResponseBody
    @RequestMapping("/del")
    public void del(String id){
        dataViewService.doDel(id);
    }
}
