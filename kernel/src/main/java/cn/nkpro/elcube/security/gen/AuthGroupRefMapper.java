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
package cn.nkpro.elcube.security.gen;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AuthGroupRefMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int countByExample(AuthGroupRefExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int deleteByExample(AuthGroupRefExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(AuthGroupRefKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int insert(AuthGroupRefKey record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int insertSelective(AuthGroupRefKey record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    List<AuthGroupRefKey> selectByExample(AuthGroupRefExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") AuthGroupRefKey record, @Param("example") AuthGroupRefExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") AuthGroupRefKey record, @Param("example") AuthGroupRefExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    List<AuthGroupRefKey> selectByExample(AuthGroupRefExample example, RowBounds rowBounds);
}