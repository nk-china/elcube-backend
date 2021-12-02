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

public interface DocDefStateMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    int countByExample(DocDefStateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    int deleteByExample(DocDefStateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(DocDefStateKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    int insert(DocDefState record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    int insertSelective(DocDefState record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    List<DocDefState> selectByExample(DocDefStateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    DocDefState selectByPrimaryKey(DocDefStateKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") DocDefState record, @Param("example") DocDefStateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") DocDefState record, @Param("example") DocDefStateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(DocDefState record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(DocDefState record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    List<DocDefState> selectByExample(DocDefStateExample example, RowBounds rowBounds);
}