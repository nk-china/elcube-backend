package cn.nkpro.ts5.platform.service;

import cn.nkpro.ts5.platform.mybatis.gen.SysUserSavedQuery;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by bean on 2020/7/22.
 */
public interface UserQueryService {
    List<SysUserSavedQuery> getList(String source);

    @Transactional
    void create(SysUserSavedQuery query);

    void delete(String queryId);
}
