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
package cn.nkpro.elcard.security.bo;

import cn.nkpro.elcard.utils.DateTimeUtilz;

import java.util.List;

/**
 * Created by bean on 2019/12/30.
 */
public class UserDetails extends UserAccountBO implements org.springframework.security.core.userdetails.UserDetails {

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return super.getAuthorities();
    }
    @Override
    public boolean isEnabled() {
        return getLocked()==null || getLocked()==0;
    }
    @Override
    public boolean isAccountNonLocked() {
        return getLocked()!=null && getLocked()==0;
    }
    @Override
    public boolean isAccountNonExpired() {
        return getValidTo()!=null && DateTimeUtilz.todayShortString().compareTo(getValidTo())>=0;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return getValidFrom()!=null && DateTimeUtilz.todayShortString().compareTo(getValidFrom())<=0;
    }

}
