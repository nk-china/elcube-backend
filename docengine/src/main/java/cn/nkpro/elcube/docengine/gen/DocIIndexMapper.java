package cn.nkpro.elcube.docengine.gen;

import cn.nkpro.elcube.docengine.gen.DocIIndex;
import cn.nkpro.elcube.docengine.gen.DocIIndexExample;
import cn.nkpro.elcube.docengine.gen.DocIIndexKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface DocIIndexMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_index
     *
     * @mbggenerated
     */
    int countByExample(DocIIndexExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_index
     *
     * @mbggenerated
     */
    int deleteByExample(DocIIndexExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_index
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(DocIIndexKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_index
     *
     * @mbggenerated
     */
    int insert(DocIIndex record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_index
     *
     * @mbggenerated
     */
    int insertSelective(DocIIndex record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_index
     *
     * @mbggenerated
     */
    List<DocIIndex> selectByExample(DocIIndexExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_index
     *
     * @mbggenerated
     */
    DocIIndex selectByPrimaryKey(DocIIndexKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_index
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") DocIIndex record, @Param("example") DocIIndexExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_index
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") DocIIndex record, @Param("example") DocIIndexExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_index
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(DocIIndex record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_index
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(DocIIndex record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_index
     *
     * @mbggenerated
     */
    List<DocIIndex> selectByExample(DocIIndexExample example, RowBounds rowBounds);
}