package cn.nkpro.easis.security.gen;

import cn.nkpro.easis.security.gen.AuthLimit;
import cn.nkpro.easis.security.gen.AuthLimitExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AuthLimitMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    int countByExample(AuthLimitExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(String limitId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    int insert(AuthLimit record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    int insertSelective(AuthLimit record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    List<AuthLimit> selectByExampleWithBLOBs(AuthLimitExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    List<AuthLimit> selectByExample(AuthLimitExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    AuthLimit selectByPrimaryKey(String limitId);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") AuthLimit record, @Param("example") AuthLimitExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    int updateByExampleWithBLOBs(@Param("record") AuthLimit record, @Param("example") AuthLimitExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") AuthLimit record, @Param("example") AuthLimitExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(AuthLimit record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(AuthLimit record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(AuthLimit record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    List<AuthLimit> selectByExample(AuthLimitExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_limit
     *
     * @mbggenerated
     */
    List<AuthLimit> selectByExampleWithBLOBs(AuthLimitExample example, RowBounds rowBounds);
}