package cn.nkpro.elcube.docengine.gen;

import cn.nkpro.elcube.docengine.gen.DocDefBpm;
import cn.nkpro.elcube.docengine.gen.DocDefBpmExample;
import cn.nkpro.elcube.docengine.gen.DocDefBpmKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface DocDefBpmMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_bpm
     *
     * @mbggenerated
     */
    int countByExample(DocDefBpmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_bpm
     *
     * @mbggenerated
     */
    int deleteByExample(DocDefBpmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_bpm
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(DocDefBpmKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_bpm
     *
     * @mbggenerated
     */
    int insert(DocDefBpm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_bpm
     *
     * @mbggenerated
     */
    int insertSelective(DocDefBpm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_bpm
     *
     * @mbggenerated
     */
    List<DocDefBpm> selectByExample(DocDefBpmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_bpm
     *
     * @mbggenerated
     */
    DocDefBpm selectByPrimaryKey(DocDefBpmKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_bpm
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") DocDefBpm record, @Param("example") DocDefBpmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_bpm
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") DocDefBpm record, @Param("example") DocDefBpmExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_bpm
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(DocDefBpm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_bpm
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(DocDefBpm record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_bpm
     *
     * @mbggenerated
     */
    List<DocDefBpm> selectByExample(DocDefBpmExample example, RowBounds rowBounds);
}