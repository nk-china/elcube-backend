package cn.nkpro.elcube.platform.gen;

import cn.nkpro.elcube.platform.gen.UserAccountExtend;
import cn.nkpro.elcube.platform.gen.UserAccountExtendExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface UserAccountExtendMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_extend
     *
     * @mbggenerated
     */
    int countByExample(UserAccountExtendExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_extend
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_extend
     *
     * @mbggenerated
     */
    int insert(UserAccountExtend record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_extend
     *
     * @mbggenerated
     */
    int insertSelective(UserAccountExtend record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_extend
     *
     * @mbggenerated
     */
    List<UserAccountExtend> selectByExample(UserAccountExtendExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_extend
     *
     * @mbggenerated
     */
    UserAccountExtend selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_extend
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") UserAccountExtend record, @Param("example") UserAccountExtendExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_extend
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") UserAccountExtend record, @Param("example") UserAccountExtendExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_extend
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(UserAccountExtend record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_extend
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(UserAccountExtend record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_extend
     *
     * @mbggenerated
     */
    List<UserAccountExtend> selectByExample(UserAccountExtendExample example, RowBounds rowBounds);
}