package cn.nkpro.tfms.platform.mappers.gen;

import cn.nkpro.tfms.platform.model.po.DefServiceStatusManager;
import cn.nkpro.tfms.platform.model.po.DefServiceStatusManagerExample;
import cn.nkpro.tfms.platform.model.po.DefServiceStatusManagerKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface DefServiceStatusManagerMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_service_status_manager
     *
     * @mbggenerated
     */
    int countByExample(DefServiceStatusManagerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_service_status_manager
     *
     * @mbggenerated
     */
    int deleteByExample(DefServiceStatusManagerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_service_status_manager
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(DefServiceStatusManagerKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_service_status_manager
     *
     * @mbggenerated
     */
    int insert(DefServiceStatusManager record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_service_status_manager
     *
     * @mbggenerated
     */
    int insertSelective(DefServiceStatusManager record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_service_status_manager
     *
     * @mbggenerated
     */
    List<DefServiceStatusManager> selectByExample(DefServiceStatusManagerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_service_status_manager
     *
     * @mbggenerated
     */
    DefServiceStatusManager selectByPrimaryKey(DefServiceStatusManagerKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_service_status_manager
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") DefServiceStatusManager record, @Param("example") DefServiceStatusManagerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_service_status_manager
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") DefServiceStatusManager record, @Param("example") DefServiceStatusManagerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_service_status_manager
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(DefServiceStatusManager record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_service_status_manager
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(DefServiceStatusManager record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_service_status_manager
     *
     * @mbggenerated
     */
    List<DefServiceStatusManager> selectByExample(DefServiceStatusManagerExample example, RowBounds rowBounds);
}