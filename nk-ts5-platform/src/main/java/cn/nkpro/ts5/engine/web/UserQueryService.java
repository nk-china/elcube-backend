package cn.nkpro.ts5.engine.web;

import cn.nkpro.ts5.orm.mb.gen.SysUserSavedQuery;
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
