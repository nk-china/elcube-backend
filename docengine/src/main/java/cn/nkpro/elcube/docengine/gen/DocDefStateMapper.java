package cn.nkpro.elcube.docengine.gen;

import cn.nkpro.elcube.docengine.gen.DocDefState;
import cn.nkpro.elcube.docengine.gen.DocDefStateExample;
import cn.nkpro.elcube.docengine.gen.DocDefStateKey;
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