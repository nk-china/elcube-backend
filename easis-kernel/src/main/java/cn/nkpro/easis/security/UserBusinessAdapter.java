package cn.nkpro.easis.security;

import java.util.Collections;

public interface UserBusinessAdapter {
    default Object getUser(String key){return Collections.emptyMap();}
}
