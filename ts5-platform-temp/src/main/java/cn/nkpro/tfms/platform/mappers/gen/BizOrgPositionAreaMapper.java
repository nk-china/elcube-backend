package cn.nkpro.tfms.platform.mappers.gen;

import cn.nkpro.tfms.platform.model.po.BizOrgPositionArea;
import cn.nkpro.tfms.platform.model.po.BizOrgPositionAreaExample;
import cn.nkpro.tfms.platform.model.po.BizOrgPositionAreaKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface BizOrgPositionAreaMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_org_position_area
     *
     * @mbggenerated
     */
    int countByExample(BizOrgPositionAreaExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_org_position_area
     *
     * @mbggenerated
     */
    int deleteByExample(BizOrgPositionAreaExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_org_position_area
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(BizOrgPositionAreaKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_org_position_area
     *
     * @mbggenerated
     */
    int insert(BizOrgPositionArea record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_org_position_area
     *
     * @mbggenerated
     */
    int insertSelective(BizOrgPositionArea record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_org_position_area
     *
     * @mbggenerated
     */
    List<BizOrgPositionArea> selectByExample(BizOrgPositionAreaExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_org_position_area
     *
     * @mbggenerated
     */
    BizOrgPositionArea selectByPrimaryKey(BizOrgPositionAreaKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_org_position_area
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") BizOrgPositionArea record, @Param("example") BizOrgPositionAreaExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_org_position_area
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") BizOrgPositionArea record, @Param("example") BizOrgPositionAreaExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_org_position_area
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(BizOrgPositionArea record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_org_position_area
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(BizOrgPositionArea record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table biz_org_position_area
     *
     * @mbggenerated
     */
    List<BizOrgPositionArea> selectByExample(BizOrgPositionAreaExample example, RowBounds rowBounds);
}