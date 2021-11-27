package cn.nkpro.easis.platform.service;

import cn.nkpro.easis.platform.gen.UserSavedQuery;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by bean on 2020/7/22.
 */
public interface UserQueryService {
    List<UserSavedQuery> getList(String source);

    @Transactional
    void create(UserSavedQuery query);

    void delete(String queryId);
}
