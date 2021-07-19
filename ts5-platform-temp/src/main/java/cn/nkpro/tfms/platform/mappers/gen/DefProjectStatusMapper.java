package cn.nkpro.tfms.platform.mappers.gen;

import cn.nkpro.tfms.platform.model.po.DefProjectStatus;
import cn.nkpro.tfms.platform.model.po.DefProjectStatusExample;
import cn.nkpro.tfms.platform.model.po.DefProjectStatusKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface DefProjectStatusMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_status
     *
     * @mbggenerated
     */
    int countByExample(DefProjectStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_status
     *
     * @mbggenerated
     */
    int deleteByExample(DefProjectStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_status
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(DefProjectStatusKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_status
     *
     * @mbggenerated
     */
    int insert(DefProjectStatus record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_status
     *
     * @mbggenerated
     */
    int insertSelective(DefProjectStatus record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_status
     *
     * @mbggenerated
     */
    List<DefProjectStatus> selectByExample(DefProjectStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_status
     *
     * @mbggenerated
     */
    DefProjectStatus selectByPrimaryKey(DefProjectStatusKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_status
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") DefProjectStatus record, @Param("example") DefProjectStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_status
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") DefProjectStatus record, @Param("example") DefProjectStatusExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_status
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(DefProjectStatus record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_status
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(DefProjectStatus record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_status
     *
     * @mbggenerated
     */
    List<DefProjectStatus> selectByExample(DefProjectStatusExample example, RowBounds rowBounds);
}