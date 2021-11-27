package cn.nkpro.easis.security.gen;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface AuthGroupRefMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int countByExample(AuthGroupRefExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int deleteByExample(AuthGroupRefExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(AuthGroupRefKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int insert(AuthGroupRefKey record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int insertSelective(AuthGroupRefKey record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    List<AuthGroupRefKey> selectByExample(AuthGroupRefExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") AuthGroupRefKey record, @Param("example") AuthGroupRefExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") AuthGroupRefKey record, @Param("example") AuthGroupRefExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_auth_group_ref
     *
     * @mbggenerated
     */
    List<AuthGroupRefKey> selectByExample(AuthGroupRefExample example, RowBounds rowBounds);
}