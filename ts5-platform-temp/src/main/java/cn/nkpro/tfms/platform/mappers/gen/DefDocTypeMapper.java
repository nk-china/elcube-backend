package cn.nkpro.tfms.platform.mappers.gen;

import cn.nkpro.tfms.platform.model.po.DefDocType;
import cn.nkpro.tfms.platform.model.po.DefDocTypeExample;
import cn.nkpro.tfms.platform.model.po.DefDocTypeKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface DefDocTypeMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    int countByExample(DefDocTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(DefDocTypeKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    int insert(DefDocType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    int insertSelective(DefDocType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    List<DefDocType> selectByExampleWithBLOBs(DefDocTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    List<DefDocType> selectByExample(DefDocTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    DefDocType selectByPrimaryKey(DefDocTypeKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") DefDocType record, @Param("example") DefDocTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    int updateByExampleWithBLOBs(@Param("record") DefDocType record, @Param("example") DefDocTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") DefDocType record, @Param("example") DefDocTypeExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(DefDocType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(DefDocType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(DefDocType record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    List<DefDocType> selectByExample(DefDocTypeExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_type
     *
     * @mbggenerated
     */
    List<DefDocType> selectByExampleWithBLOBs(DefDocTypeExample example, RowBounds rowBounds);
}