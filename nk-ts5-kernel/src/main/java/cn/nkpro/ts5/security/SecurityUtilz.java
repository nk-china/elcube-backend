package cn.nkpro.ts5.security;

import cn.nkpro.ts5.exception.TfmsAccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by bean on 2020/7/24.
 */
public class SecurityUtilz {

    public static TfmsUserDetails getUser(){
        return (TfmsUserDetails) Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getDetails)
                .orElseThrow(()->new TfmsAccessDeniedException("未登陆"));
    }

    public static Collection<? extends GrantedAuthority> getAuthorities(){
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .orElse(Collections.emptyList());
    }

    public static boolean hasAnyAuthority(String... targetAuthoritys){

        /**
         * *:*
         * RES:*
         * RES:OPT
         * @*:*
         * @DOT:*
         * @DOC:OPT
         */

        return targetAuthoritys.length==0 || getAuthorities().stream()
                .anyMatch(authority->{
                    Pattern pattern = Pattern.compile(
                            String.format("^%s$",
                                    authority.getAuthority()
                                            .replaceAll("[*]","@?\\\\w+"))
                    );
                    return Arrays.stream(targetAuthoritys)
                                .anyMatch(targetAuthority->pattern.matcher(targetAuthority).matches());
                });
    }
}
