package cn.nkpro.easis.security.bo;

import lombok.Data;

import java.util.List;

@Data
public class GrantedAuthoritySub {
    // 卡片
    private List<String> includes;
    private List<String> excludes;

    // 下一个状态
    private List<String> status;

    // 自定义
    private List<String> customs;
}