package cn.nkpro.easis.security;

import lombok.Getter;

/**
 * Created by bean on 2020/8/3.
 */
public enum NkDefaultPermissions implements NkPermissions {

    view("查看"),
    create("创建"),
    update("修改");

    @Getter
    private String desc;

    NkDefaultPermissions(String desc){
        this.desc = desc;
    }
}
