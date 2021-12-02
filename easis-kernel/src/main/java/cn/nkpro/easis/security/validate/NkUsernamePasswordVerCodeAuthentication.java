/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.security.validate;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class NkUsernamePasswordVerCodeAuthentication extends AbstractAuthenticationToken {

    @Getter
    private String username;
    @Getter
    private String password;
    @Getter
    private String verCode;
    @Getter
    private String verKey;

    public NkUsernamePasswordVerCodeAuthentication(String username, String password, String verKey, String verCode) {
        super(null);
        this.username = username;
        this.password = password;
        this.verKey = verKey;
        this.verCode = verCode;
    }

    @Override
    public String getCredentials() {
        return password;
    }

    @Override
    public String getPrincipal() {
        return username;
    }
}