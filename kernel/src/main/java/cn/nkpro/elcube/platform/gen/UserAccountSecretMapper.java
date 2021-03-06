package cn.nkpro.elcube.platform.gen;

import cn.nkpro.elcube.platform.gen.UserAccountSecret;
import cn.nkpro.elcube.platform.gen.UserAccountSecretExample;
import cn.nkpro.elcube.platform.gen.UserAccountSecretKey;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface UserAccountSecretMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_secret
     *
     * @mbggenerated
     */
    int countByExample(UserAccountSecretExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_secret
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(UserAccountSecretKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_secret
     *
     * @mbggenerated
     */
    int insert(UserAccountSecret record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_secret
     *
     * @mbggenerated
     */
    int insertSelective(UserAccountSecret record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_secret
     *
     * @mbggenerated
     */
    List<UserAccountSecret> selectByExample(UserAccountSecretExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_secret
     *
     * @mbggenerated
     */
    UserAccountSecret selectByPrimaryKey(UserAccountSecretKey key);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_secret
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") UserAccountSecret record, @Param("example") UserAccountSecretExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_secret
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") UserAccountSecret record, @Param("example") UserAccountSecretExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_secret
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(UserAccountSecret record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_secret
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(UserAccountSecret record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_account_secret
     *
     * @mbggenerated
     */
    List<UserAccountSecret> selectByExample(UserAccountSecretExample example, RowBounds rowBounds);
}