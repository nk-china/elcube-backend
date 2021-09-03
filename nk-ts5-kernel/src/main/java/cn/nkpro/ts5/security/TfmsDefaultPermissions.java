package cn.nkpro.ts5.security;

import lombok.Getter;

/**
 * Created by bean on 2020/8/3.
 */
public enum TfmsDefaultPermissions implements TfmsPermissions{

    view("查看"),
    create("创建"),
    update("修改");

    @Getter
    private String desc;

    TfmsDefaultPermissions(String desc){
        this.desc = desc;
    }
}
