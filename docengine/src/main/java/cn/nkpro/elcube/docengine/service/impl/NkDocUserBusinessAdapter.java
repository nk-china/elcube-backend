package cn.nkpro.elcube.docengine.service.impl;

import cn.nkpro.elcube.docengine.NkDocEngine;
import cn.nkpro.elcube.platform.gen.UserAccount;
import cn.nkpro.elcube.security.UserBusinessAdapter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component("NkDocUserBusinessAdapter")
public class NkDocUserBusinessAdapter implements UserBusinessAdapter {

    @SuppressWarnings("all")
    @Autowired
    private NkDocEngine docEngine;

    @Override
    public Object getUser(UserAccount account) {

        if(StringUtils.isNotBlank(account.getObjectId())){
            return docEngine.detail(account.getObjectId());
        }

        return Collections.emptyMap();
    }
}
