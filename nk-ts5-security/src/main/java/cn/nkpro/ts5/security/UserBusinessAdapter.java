package cn.nkpro.ts5.security;

import java.util.Collections;

public interface UserBusinessAdapter {
    default Object getUser(String key){return Collections.emptyMap();}
}
