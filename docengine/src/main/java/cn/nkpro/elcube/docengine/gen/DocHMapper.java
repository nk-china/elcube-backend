package cn.nkpro.elcube.docengine.gen;

import cn.nkpro.elcube.docengine.gen.DocH;
import cn.nkpro.elcube.docengine.gen.DocHExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface DocHMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_h
     *
     * @mbggenerated
     */
    int countByExample(DocHExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_h
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(String docId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_h
     *
     * @mbggenerated
     */
    int insert(DocH record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_h
     *
     * @mbggenerated
     */
    int insertSelective(DocH record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_h
     *
     * @mbggenerated
     */
    List<DocH> selectByExample(DocHExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_h
     *
     * @mbggenerated
     */
    DocH selectByPrimaryKey(String docId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_h
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") DocH record, @Param("example") DocHExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_h
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") DocH record, @Param("example") DocHExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_h
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(DocH record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_h
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(DocH record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_h
     *
     * @mbggenerated
     */
    List<DocH> selectByExample(DocHExample example, RowBounds rowBounds);
}