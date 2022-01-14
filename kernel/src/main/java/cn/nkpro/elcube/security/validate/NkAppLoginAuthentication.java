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

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class NkAppLoginAuthentication extends AbstractAuthenticationToken {
    @Getter
    private String nkAppSource;
    @Getter
    private String phone;
    @Getter
    private String verCode;
    @Getter
    private String openId;
    @Getter
    private String appleId;

    public NkAppLoginAuthentication(String nkAppSource, String phone, String verCode, String openId, String appleId) {
        super(null);
        this.nkAppSource = nkAppSource;
        this.phone = phone;
        this.verCode = verCode;
        this.openId = openId;
        this.appleId = appleId;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
