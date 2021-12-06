/*
 * This file is part of ELCard.
 *
 * ELCard is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCard.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcard.security;

import cn.nkpro.elcard.security.bo.GrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class NkSecurityRunner {

    @Qualifier("NkSysAccountService")
    @Autowired@SuppressWarnings("all")
    private UserDetailsService userDetailsService;

    public void runAsUser(String username,Function function){
        UserDetails details = userDetailsService.loadUserByUsername(username);

        GrantedAuthority authority = new GrantedAuthority();
        authority.setAuthority("*:*");

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(authority)
        );
        authentication.setDetails(details);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try{
            function.apply();
        }finally {
            SecurityContextHolder.clearContext();
        }
    }

    @FunctionalInterface
    public interface Function{
        void apply();
    }
}
