package cn.nkpro.easis.platform.gen;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface UserSavedQueryMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    int countByExample(UserSavedQueryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    int insert(UserSavedQuery record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    int insertSelective(UserSavedQuery record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    List<UserSavedQuery> selectByExampleWithBLOBs(UserSavedQueryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    List<UserSavedQuery> selectByExample(UserSavedQueryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    UserSavedQuery selectByPrimaryKey(String id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") UserSavedQuery record, @Param("example") UserSavedQueryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    int updateByExampleWithBLOBs(@Param("record") UserSavedQuery record, @Param("example") UserSavedQueryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") UserSavedQuery record, @Param("example") UserSavedQueryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(UserSavedQuery record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    int updateByPrimaryKeyWithBLOBs(UserSavedQuery record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(UserSavedQuery record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    List<UserSavedQuery> selectByExample(UserSavedQueryExample example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_user_saved_query
     *
     * @mbggenerated
     */
    List<UserSavedQuery> selectByExampleWithBLOBs(UserSavedQueryExample example, RowBounds rowBounds);
}