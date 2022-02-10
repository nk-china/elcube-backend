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
package cn.nkpro.elcube.security.validate;

import cn.nkpro.elcube.security.NkCodeUserDetailsService;
import cn.nkpro.elcube.security.bo.UserDetails;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;

import java.util.List;

public class NkCodeAuthenticationProvider implements AuthenticationProvider {

    private List<NkCodeUserDetailsService> userDetailsServices;

    @Override
    public boolean supports(Class<?> authentication) {
        return (NkCodeAuthentication.class.isAssignableFrom(authentication));
    }

    public NkCodeAuthenticationProvider(List<NkCodeUserDetailsService> userDetailsServices){
        this.userDetailsServices = userDetailsServices;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        NkCodeAuthentication nkAuthentication = (NkCodeAuthentication) authentication;

        NkCodeUserDetailsService detailsService = userDetailsServices.stream()
                .filter(service -> service.supports(nkAuthentication.getType()))
                .findFirst()
                .orElseThrow(()->new ProviderNotFoundException("不支持的登陆方式"));

        UserDetails details = detailsService.loadUser(nkAuthentication.getCode(),nkAuthentication.getSecret());

        if(null==details){
            throw new PreAuthenticatedCredentialsNotFoundException("账号未绑定");
        }
        if(null!=details.getLocked() && details.getLocked()==1){
            throw new BadCredentialsException("账号已禁用");
        }

        AbstractAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                nkAuthentication.getPrincipal(),
                nkAuthentication.getCredentials(),
                details.getAuthorities());
        auth.setDetails(details);
        return auth;
    }

}
