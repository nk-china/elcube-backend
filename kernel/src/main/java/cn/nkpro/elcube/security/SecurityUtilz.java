/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.security;

import cn.nkpro.elcube.exception.NkAccessDeniedException;
import cn.nkpro.elcube.security.bo.UserDetails;
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

    public static UserDetails getUser(){
        return (UserDetails) Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getDetails)
                .orElseThrow(()->new NkAccessDeniedException("未登陆"));
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(){
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .orElse(Collections.emptyList());
    }

    public static boolean hasAnyAuthority(String... targetAuthoritys){

        /*
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
                                            .replaceAll("[*]","[@#]?[A-Za-z0-9_-]+"))
                    );
                    return Arrays.stream(targetAuthoritys)
                                .anyMatch(targetAuthority->pattern.matcher(targetAuthority.toUpperCase()).matches());
                });
    }
}
