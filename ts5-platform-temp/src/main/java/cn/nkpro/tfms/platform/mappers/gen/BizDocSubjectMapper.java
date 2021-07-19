package cn.nkpro.tfms.platform.mappers.gen;

import cn.nkpro.tfms.platform.model.po.BizDocSubject;
import cn.nkpro.tfms.platform.model.po.BizDocSubjectExample;
import cn.nkpro.tfms.platform.model.po.BizDocSubjectKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface BizDocSubjectMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_doc_subject
     *
     * @mbggenerated
     */
    int countByExample(BizDocSubjectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_doc_subject
     *
     * @mbggenerated
     */
    int deleteByExample(BizDocSubjectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_doc_subject
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(BizDocSubjectKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_doc_subject
     *
     * @mbggenerated
     */
    int insert(BizDocSubject record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_doc_subject
     *
     * @mbggenerated
     */
    int insertSelective(BizDocSubject record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_doc_subject
     *
     * @mbggenerated
     */
    List<BizDocSubject> selectByExample(BizDocSubjectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_doc_subject
     *
     * @mbggenerated
     */
    BizDocSubject selectByPrimaryKey(BizDocSubjectKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_doc_subject
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") BizDocSubject record, @Param("example") BizDocSubjectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_doc_subject
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") BizDocSubject record, @Param("example") BizDocSubjectExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_doc_subject
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(BizDocSubject record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_doc_subject
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(BizDocSubject record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_doc_subject
     *
     * @mbggenerated
     */
    List<BizDocSubject> selectByExample(BizDocSubjectExample example, RowBounds rowBounds);
}