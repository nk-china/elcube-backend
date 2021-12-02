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
package cn.nkpro.easis.platform.gen;

import cn.nkpro.easis.platform.gen.PlatformRegistry;
import cn.nkpro.easis.platform.gen.PlatformRegistryExample;
import cn.nkpro.easis.platform.gen.PlatformRegistryKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface PlatformRegistryMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int countByExample(PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int deleteByExample(PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(PlatformRegistryKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int insert(PlatformRegistry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int insertSelective(PlatformRegistry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    List<PlatformRegistry> selectByExampleWithBLOBs(PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    List<PlatformRegistry> selectByExample(PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    PlatformRegistry selectByPrimaryKey(PlatformRegistryKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") PlatformRegistry record, @Param("example") PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int updateByExampleWithBLOBs(@Param("record") PlatformRegistry record, @Param("example") PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") PlatformRegistry record, @Param("example") PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(PlatformRegistry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(PlatformRegistry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(PlatformRegistry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    List<PlatformRegistry> selectByExample(PlatformRegistryExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    List<PlatformRegistry> selectByExampleWithBLOBs(PlatformRegistryExample example, RowBounds rowBounds);
}