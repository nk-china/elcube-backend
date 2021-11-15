package cn.nkpro.ts5.platform.gen;

import cn.nkpro.ts5.platform.gen.PlatformRegistry;
import cn.nkpro.ts5.platform.gen.PlatformRegistryExample;
import cn.nkpro.ts5.platform.gen.PlatformRegistryKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface PlatformRegistryMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int countByExample(PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int deleteByExample(PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(PlatformRegistryKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int insert(PlatformRegistry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int insertSelective(PlatformRegistry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    List<PlatformRegistry> selectByExampleWithBLOBs(PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    List<PlatformRegistry> selectByExample(PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    PlatformRegistry selectByPrimaryKey(PlatformRegistryKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") PlatformRegistry record, @Param("example") PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int updateByExampleWithBLOBs(@Param("record") PlatformRegistry record, @Param("example") PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") PlatformRegistry record, @Param("example") PlatformRegistryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(PlatformRegistry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(PlatformRegistry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(PlatformRegistry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    List<PlatformRegistry> selectByExample(PlatformRegistryExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_platform_registry
     *
     * @mbggenerated
     */
    List<PlatformRegistry> selectByExampleWithBLOBs(PlatformRegistryExample example, RowBounds rowBounds);
}