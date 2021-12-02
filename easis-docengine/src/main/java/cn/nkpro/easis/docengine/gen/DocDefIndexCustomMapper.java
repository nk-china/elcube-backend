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
package cn.nkpro.easis.docengine.gen;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface DocDefIndexCustomMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    int countByExample(DocDefIndexCustomExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    int deleteByExample(DocDefIndexCustomExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(DocDefIndexCustomKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    int insert(DocDefIndexCustom record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    int insertSelective(DocDefIndexCustom record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    List<DocDefIndexCustom> selectByExample(DocDefIndexCustomExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    DocDefIndexCustom selectByPrimaryKey(DocDefIndexCustomKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") DocDefIndexCustom record, @Param("example") DocDefIndexCustomExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") DocDefIndexCustom record, @Param("example") DocDefIndexCustomExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(DocDefIndexCustom record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(DocDefIndexCustom record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    List<DocDefIndexCustom> selectByExample(DocDefIndexCustomExample example, RowBounds rowBounds);
}