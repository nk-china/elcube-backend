package cn.nkpro.ts5.co.gen;

import cn.nkpro.ts5.co.gen.ScriptDefH;
import cn.nkpro.ts5.co.gen.ScriptDefHExample;
import cn.nkpro.ts5.co.gen.ScriptDefHKey;
import cn.nkpro.ts5.co.gen.ScriptDefHWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface ScriptDefHMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    int countByExample(ScriptDefHExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(ScriptDefHKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    int insert(ScriptDefHWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    int insertSelective(ScriptDefHWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    List<ScriptDefHWithBLOBs> selectByExampleWithBLOBs(ScriptDefHExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    List<ScriptDefH> selectByExample(ScriptDefHExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    ScriptDefHWithBLOBs selectByPrimaryKey(ScriptDefHKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") ScriptDefHWithBLOBs record, @Param("example") ScriptDefHExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    int updateByExampleWithBLOBs(@Param("record") ScriptDefHWithBLOBs record, @Param("example") ScriptDefHExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") ScriptDefH record, @Param("example") ScriptDefHExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(ScriptDefHWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(ScriptDefHWithBLOBs record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(ScriptDefH record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    List<ScriptDefH> selectByExample(ScriptDefHExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_script_def_h
     *
     * @mbggenerated
     */
    List<ScriptDefHWithBLOBs> selectByExampleWithBLOBs(ScriptDefHExample example, RowBounds rowBounds);
}