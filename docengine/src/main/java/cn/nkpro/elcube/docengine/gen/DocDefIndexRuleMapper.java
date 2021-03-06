package cn.nkpro.elcube.docengine.gen;

import cn.nkpro.elcube.docengine.gen.DocDefIndexRule;
import cn.nkpro.elcube.docengine.gen.DocDefIndexRuleExample;
import cn.nkpro.elcube.docengine.gen.DocDefIndexRuleKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface DocDefIndexRuleMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_rule
     *
     * @mbggenerated
     */
    int countByExample(DocDefIndexRuleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_rule
     *
     * @mbggenerated
     */
    int deleteByExample(DocDefIndexRuleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_rule
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(DocDefIndexRuleKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_rule
     *
     * @mbggenerated
     */
    int insert(DocDefIndexRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_rule
     *
     * @mbggenerated
     */
    int insertSelective(DocDefIndexRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_rule
     *
     * @mbggenerated
     */
    List<DocDefIndexRule> selectByExample(DocDefIndexRuleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_rule
     *
     * @mbggenerated
     */
    DocDefIndexRule selectByPrimaryKey(DocDefIndexRuleKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_rule
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") DocDefIndexRule record, @Param("example") DocDefIndexRuleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_rule
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") DocDefIndexRule record, @Param("example") DocDefIndexRuleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_rule
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(DocDefIndexRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_rule
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(DocDefIndexRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_rule
     *
     * @mbggenerated
     */
    List<DocDefIndexRule> selectByExample(DocDefIndexRuleExample example, RowBounds rowBounds);
}