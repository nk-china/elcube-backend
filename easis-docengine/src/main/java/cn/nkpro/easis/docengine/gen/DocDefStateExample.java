/*
 * This file is part of EAsis.
 *
 * EAsis is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EAsis is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with EAsis.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.easis.docengine.gen;

import java.util.ArrayList;
import java.util.List;

public class DocDefStateExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    public DocDefStateExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andDocStateIsNull() {
            addCriterion("DOC_STATE is null");
            return (Criteria) this;
        }

        public Criteria andDocStateIsNotNull() {
            addCriterion("DOC_STATE is not null");
            return (Criteria) this;
        }

        public Criteria andDocStateEqualTo(String value) {
            addCriterion("DOC_STATE =", value, "docState");
            return (Criteria) this;
        }

        public Criteria andDocStateNotEqualTo(String value) {
            addCriterion("DOC_STATE <>", value, "docState");
            return (Criteria) this;
        }

        public Criteria andDocStateGreaterThan(String value) {
            addCriterion("DOC_STATE >", value, "docState");
            return (Criteria) this;
        }

        public Criteria andDocStateGreaterThanOrEqualTo(String value) {
            addCriterion("DOC_STATE >=", value, "docState");
            return (Criteria) this;
        }

        public Criteria andDocStateLessThan(String value) {
            addCriterion("DOC_STATE <", value, "docState");
            return (Criteria) this;
        }

        public Criteria andDocStateLessThanOrEqualTo(String value) {
            addCriterion("DOC_STATE <=", value, "docState");
            return (Criteria) this;
        }

        public Criteria andDocStateLike(String value) {
            addCriterion("DOC_STATE like", value, "docState");
            return (Criteria) this;
        }

        public Criteria andDocStateNotLike(String value) {
            addCriterion("DOC_STATE not like", value, "docState");
            return (Criteria) this;
        }

        public Criteria andDocStateIn(List<String> values) {
            addCriterion("DOC_STATE in", values, "docState");
            return (Criteria) this;
        }

        public Criteria andDocStateNotIn(List<String> values) {
            addCriterion("DOC_STATE not in", values, "docState");
            return (Criteria) this;
        }

        public Criteria andDocStateBetween(String value1, String value2) {
            addCriterion("DOC_STATE between", value1, value2, "docState");
            return (Criteria) this;
        }

        public Criteria andDocStateNotBetween(String value1, String value2) {
            addCriterion("DOC_STATE not between", value1, value2, "docState");
            return (Criteria) this;
        }

        public Criteria andDocTypeIsNull() {
            addCriterion("DOC_TYPE is null");
            return (Criteria) this;
        }

        public Criteria andDocTypeIsNotNull() {
            addCriterion("DOC_TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andDocTypeEqualTo(String value) {
            addCriterion("DOC_TYPE =", value, "docType");
            return (Criteria) this;
        }

        public Criteria andDocTypeNotEqualTo(String value) {
            addCriterion("DOC_TYPE <>", value, "docType");
            return (Criteria) this;
        }

        public Criteria andDocTypeGreaterThan(String value) {
            addCriterion("DOC_TYPE >", value, "docType");
            return (Criteria) this;
        }

        public Criteria andDocTypeGreaterThanOrEqualTo(String value) {
            addCriterion("DOC_TYPE >=", value, "docType");
            return (Criteria) this;
        }

        public Criteria andDocTypeLessThan(String value) {
            addCriterion("DOC_TYPE <", value, "docType");
            return (Criteria) this;
        }

        public Criteria andDocTypeLessThanOrEqualTo(String value) {
            addCriterion("DOC_TYPE <=", value, "docType");
            return (Criteria) this;
        }

        public Criteria andDocTypeLike(String value) {
            addCriterion("DOC_TYPE like", value, "docType");
            return (Criteria) this;
        }

        public Criteria andDocTypeNotLike(String value) {
            addCriterion("DOC_TYPE not like", value, "docType");
            return (Criteria) this;
        }

        public Criteria andDocTypeIn(List<String> values) {
            addCriterion("DOC_TYPE in", values, "docType");
            return (Criteria) this;
        }

        public Criteria andDocTypeNotIn(List<String> values) {
            addCriterion("DOC_TYPE not in", values, "docType");
            return (Criteria) this;
        }

        public Criteria andDocTypeBetween(String value1, String value2) {
            addCriterion("DOC_TYPE between", value1, value2, "docType");
            return (Criteria) this;
        }

        public Criteria andDocTypeNotBetween(String value1, String value2) {
            addCriterion("DOC_TYPE not between", value1, value2, "docType");
            return (Criteria) this;
        }

        public Criteria andPreDocStateIsNull() {
            addCriterion("PRE_DOC_STATE is null");
            return (Criteria) this;
        }

        public Criteria andPreDocStateIsNotNull() {
            addCriterion("PRE_DOC_STATE is not null");
            return (Criteria) this;
        }

        public Criteria andPreDocStateEqualTo(String value) {
            addCriterion("PRE_DOC_STATE =", value, "preDocState");
            return (Criteria) this;
        }

        public Criteria andPreDocStateNotEqualTo(String value) {
            addCriterion("PRE_DOC_STATE <>", value, "preDocState");
            return (Criteria) this;
        }

        public Criteria andPreDocStateGreaterThan(String value) {
            addCriterion("PRE_DOC_STATE >", value, "preDocState");
            return (Criteria) this;
        }

        public Criteria andPreDocStateGreaterThanOrEqualTo(String value) {
            addCriterion("PRE_DOC_STATE >=", value, "preDocState");
            return (Criteria) this;
        }

        public Criteria andPreDocStateLessThan(String value) {
            addCriterion("PRE_DOC_STATE <", value, "preDocState");
            return (Criteria) this;
        }

        public Criteria andPreDocStateLessThanOrEqualTo(String value) {
            addCriterion("PRE_DOC_STATE <=", value, "preDocState");
            return (Criteria) this;
        }

        public Criteria andPreDocStateLike(String value) {
            addCriterion("PRE_DOC_STATE like", value, "preDocState");
            return (Criteria) this;
        }

        public Criteria andPreDocStateNotLike(String value) {
            addCriterion("PRE_DOC_STATE not like", value, "preDocState");
            return (Criteria) this;
        }

        public Criteria andPreDocStateIn(List<String> values) {
            addCriterion("PRE_DOC_STATE in", values, "preDocState");
            return (Criteria) this;
        }

        public Criteria andPreDocStateNotIn(List<String> values) {
            addCriterion("PRE_DOC_STATE not in", values, "preDocState");
            return (Criteria) this;
        }

        public Criteria andPreDocStateBetween(String value1, String value2) {
            addCriterion("PRE_DOC_STATE between", value1, value2, "preDocState");
            return (Criteria) this;
        }

        public Criteria andPreDocStateNotBetween(String value1, String value2) {
            addCriterion("PRE_DOC_STATE not between", value1, value2, "preDocState");
            return (Criteria) this;
        }

        public Criteria andVersionIsNull() {
            addCriterion("VERSION is null");
            return (Criteria) this;
        }

        public Criteria andVersionIsNotNull() {
            addCriterion("VERSION is not null");
            return (Criteria) this;
        }

        public Criteria andVersionEqualTo(String value) {
            addCriterion("VERSION =", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotEqualTo(String value) {
            addCriterion("VERSION <>", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThan(String value) {
            addCriterion("VERSION >", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThanOrEqualTo(String value) {
            addCriterion("VERSION >=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThan(String value) {
            addCriterion("VERSION <", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThanOrEqualTo(String value) {
            addCriterion("VERSION <=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLike(String value) {
            addCriterion("VERSION like", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotLike(String value) {
            addCriterion("VERSION not like", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionIn(List<String> values) {
            addCriterion("VERSION in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotIn(List<String> values) {
            addCriterion("VERSION not in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionBetween(String value1, String value2) {
            addCriterion("VERSION between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotBetween(String value1, String value2) {
            addCriterion("VERSION not between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andDocStateDescIsNull() {
            addCriterion("DOC_STATE_DESC is null");
            return (Criteria) this;
        }

        public Criteria andDocStateDescIsNotNull() {
            addCriterion("DOC_STATE_DESC is not null");
            return (Criteria) this;
        }

        public Criteria andDocStateDescEqualTo(String value) {
            addCriterion("DOC_STATE_DESC =", value, "docStateDesc");
            return (Criteria) this;
        }

        public Criteria andDocStateDescNotEqualTo(String value) {
            addCriterion("DOC_STATE_DESC <>", value, "docStateDesc");
            return (Criteria) this;
        }

        public Criteria andDocStateDescGreaterThan(String value) {
            addCriterion("DOC_STATE_DESC >", value, "docStateDesc");
            return (Criteria) this;
        }

        public Criteria andDocStateDescGreaterThanOrEqualTo(String value) {
            addCriterion("DOC_STATE_DESC >=", value, "docStateDesc");
            return (Criteria) this;
        }

        public Criteria andDocStateDescLessThan(String value) {
            addCriterion("DOC_STATE_DESC <", value, "docStateDesc");
            return (Criteria) this;
        }

        public Criteria andDocStateDescLessThanOrEqualTo(String value) {
            addCriterion("DOC_STATE_DESC <=", value, "docStateDesc");
            return (Criteria) this;
        }

        public Criteria andDocStateDescLike(String value) {
            addCriterion("DOC_STATE_DESC like", value, "docStateDesc");
            return (Criteria) this;
        }

        public Criteria andDocStateDescNotLike(String value) {
            addCriterion("DOC_STATE_DESC not like", value, "docStateDesc");
            return (Criteria) this;
        }

        public Criteria andDocStateDescIn(List<String> values) {
            addCriterion("DOC_STATE_DESC in", values, "docStateDesc");
            return (Criteria) this;
        }

        public Criteria andDocStateDescNotIn(List<String> values) {
            addCriterion("DOC_STATE_DESC not in", values, "docStateDesc");
            return (Criteria) this;
        }

        public Criteria andDocStateDescBetween(String value1, String value2) {
            addCriterion("DOC_STATE_DESC between", value1, value2, "docStateDesc");
            return (Criteria) this;
        }

        public Criteria andDocStateDescNotBetween(String value1, String value2) {
            addCriterion("DOC_STATE_DESC not between", value1, value2, "docStateDesc");
            return (Criteria) this;
        }

        public Criteria andSysStateIsNull() {
            addCriterion("SYS_STATE is null");
            return (Criteria) this;
        }

        public Criteria andSysStateIsNotNull() {
            addCriterion("SYS_STATE is not null");
            return (Criteria) this;
        }

        public Criteria andSysStateEqualTo(String value) {
            addCriterion("SYS_STATE =", value, "sysState");
            return (Criteria) this;
        }

        public Criteria andSysStateNotEqualTo(String value) {
            addCriterion("SYS_STATE <>", value, "sysState");
            return (Criteria) this;
        }

        public Criteria andSysStateGreaterThan(String value) {
            addCriterion("SYS_STATE >", value, "sysState");
            return (Criteria) this;
        }

        public Criteria andSysStateGreaterThanOrEqualTo(String value) {
            addCriterion("SYS_STATE >=", value, "sysState");
            return (Criteria) this;
        }

        public Criteria andSysStateLessThan(String value) {
            addCriterion("SYS_STATE <", value, "sysState");
            return (Criteria) this;
        }

        public Criteria andSysStateLessThanOrEqualTo(String value) {
            addCriterion("SYS_STATE <=", value, "sysState");
            return (Criteria) this;
        }

        public Criteria andSysStateLike(String value) {
            addCriterion("SYS_STATE like", value, "sysState");
            return (Criteria) this;
        }

        public Criteria andSysStateNotLike(String value) {
            addCriterion("SYS_STATE not like", value, "sysState");
            return (Criteria) this;
        }

        public Criteria andSysStateIn(List<String> values) {
            addCriterion("SYS_STATE in", values, "sysState");
            return (Criteria) this;
        }

        public Criteria andSysStateNotIn(List<String> values) {
            addCriterion("SYS_STATE not in", values, "sysState");
            return (Criteria) this;
        }

        public Criteria andSysStateBetween(String value1, String value2) {
            addCriterion("SYS_STATE between", value1, value2, "sysState");
            return (Criteria) this;
        }

        public Criteria andSysStateNotBetween(String value1, String value2) {
            addCriterion("SYS_STATE not between", value1, value2, "sysState");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeIsNull() {
            addCriterion("REF_OBJECT_TYPE is null");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeIsNotNull() {
            addCriterion("REF_OBJECT_TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeEqualTo(String value) {
            addCriterion("REF_OBJECT_TYPE =", value, "refObjectType");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeNotEqualTo(String value) {
            addCriterion("REF_OBJECT_TYPE <>", value, "refObjectType");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeGreaterThan(String value) {
            addCriterion("REF_OBJECT_TYPE >", value, "refObjectType");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeGreaterThanOrEqualTo(String value) {
            addCriterion("REF_OBJECT_TYPE >=", value, "refObjectType");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeLessThan(String value) {
            addCriterion("REF_OBJECT_TYPE <", value, "refObjectType");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeLessThanOrEqualTo(String value) {
            addCriterion("REF_OBJECT_TYPE <=", value, "refObjectType");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeLike(String value) {
            addCriterion("REF_OBJECT_TYPE like", value, "refObjectType");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeNotLike(String value) {
            addCriterion("REF_OBJECT_TYPE not like", value, "refObjectType");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeIn(List<String> values) {
            addCriterion("REF_OBJECT_TYPE in", values, "refObjectType");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeNotIn(List<String> values) {
            addCriterion("REF_OBJECT_TYPE not in", values, "refObjectType");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeBetween(String value1, String value2) {
            addCriterion("REF_OBJECT_TYPE between", value1, value2, "refObjectType");
            return (Criteria) this;
        }

        public Criteria andRefObjectTypeNotBetween(String value1, String value2) {
            addCriterion("REF_OBJECT_TYPE not between", value1, value2, "refObjectType");
            return (Criteria) this;
        }

        public Criteria andEditPermIsNull() {
            addCriterion("EDIT_PERM is null");
            return (Criteria) this;
        }

        public Criteria andEditPermIsNotNull() {
            addCriterion("EDIT_PERM is not null");
            return (Criteria) this;
        }

        public Criteria andEditPermEqualTo(Integer value) {
            addCriterion("EDIT_PERM =", value, "editPerm");
            return (Criteria) this;
        }

        public Criteria andEditPermNotEqualTo(Integer value) {
            addCriterion("EDIT_PERM <>", value, "editPerm");
            return (Criteria) this;
        }

        public Criteria andEditPermGreaterThan(Integer value) {
            addCriterion("EDIT_PERM >", value, "editPerm");
            return (Criteria) this;
        }

        public Criteria andEditPermGreaterThanOrEqualTo(Integer value) {
            addCriterion("EDIT_PERM >=", value, "editPerm");
            return (Criteria) this;
        }

        public Criteria andEditPermLessThan(Integer value) {
            addCriterion("EDIT_PERM <", value, "editPerm");
            return (Criteria) this;
        }

        public Criteria andEditPermLessThanOrEqualTo(Integer value) {
            addCriterion("EDIT_PERM <=", value, "editPerm");
            return (Criteria) this;
        }

        public Criteria andEditPermIn(List<Integer> values) {
            addCriterion("EDIT_PERM in", values, "editPerm");
            return (Criteria) this;
        }

        public Criteria andEditPermNotIn(List<Integer> values) {
            addCriterion("EDIT_PERM not in", values, "editPerm");
            return (Criteria) this;
        }

        public Criteria andEditPermBetween(Integer value1, Integer value2) {
            addCriterion("EDIT_PERM between", value1, value2, "editPerm");
            return (Criteria) this;
        }

        public Criteria andEditPermNotBetween(Integer value1, Integer value2) {
            addCriterion("EDIT_PERM not between", value1, value2, "editPerm");
            return (Criteria) this;
        }

        public Criteria andDisplayPrimaryIsNull() {
            addCriterion("DISPLAY_PRIMARY is null");
            return (Criteria) this;
        }

        public Criteria andDisplayPrimaryIsNotNull() {
            addCriterion("DISPLAY_PRIMARY is not null");
            return (Criteria) this;
        }

        public Criteria andDisplayPrimaryEqualTo(Integer value) {
            addCriterion("DISPLAY_PRIMARY =", value, "displayPrimary");
            return (Criteria) this;
        }

        public Criteria andDisplayPrimaryNotEqualTo(Integer value) {
            addCriterion("DISPLAY_PRIMARY <>", value, "displayPrimary");
            return (Criteria) this;
        }

        public Criteria andDisplayPrimaryGreaterThan(Integer value) {
            addCriterion("DISPLAY_PRIMARY >", value, "displayPrimary");
            return (Criteria) this;
        }

        public Criteria andDisplayPrimaryGreaterThanOrEqualTo(Integer value) {
            addCriterion("DISPLAY_PRIMARY >=", value, "displayPrimary");
            return (Criteria) this;
        }

        public Criteria andDisplayPrimaryLessThan(Integer value) {
            addCriterion("DISPLAY_PRIMARY <", value, "displayPrimary");
            return (Criteria) this;
        }

        public Criteria andDisplayPrimaryLessThanOrEqualTo(Integer value) {
            addCriterion("DISPLAY_PRIMARY <=", value, "displayPrimary");
            return (Criteria) this;
        }

        public Criteria andDisplayPrimaryIn(List<Integer> values) {
            addCriterion("DISPLAY_PRIMARY in", values, "displayPrimary");
            return (Criteria) this;
        }

        public Criteria andDisplayPrimaryNotIn(List<Integer> values) {
            addCriterion("DISPLAY_PRIMARY not in", values, "displayPrimary");
            return (Criteria) this;
        }

        public Criteria andDisplayPrimaryBetween(Integer value1, Integer value2) {
            addCriterion("DISPLAY_PRIMARY between", value1, value2, "displayPrimary");
            return (Criteria) this;
        }

        public Criteria andDisplayPrimaryNotBetween(Integer value1, Integer value2) {
            addCriterion("DISPLAY_PRIMARY not between", value1, value2, "displayPrimary");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeIsNull() {
            addCriterion("UPDATED_TIME is null");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeIsNotNull() {
            addCriterion("UPDATED_TIME is not null");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeEqualTo(Long value) {
            addCriterion("UPDATED_TIME =", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeNotEqualTo(Long value) {
            addCriterion("UPDATED_TIME <>", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeGreaterThan(Long value) {
            addCriterion("UPDATED_TIME >", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("UPDATED_TIME >=", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeLessThan(Long value) {
            addCriterion("UPDATED_TIME <", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeLessThanOrEqualTo(Long value) {
            addCriterion("UPDATED_TIME <=", value, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeIn(List<Long> values) {
            addCriterion("UPDATED_TIME in", values, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeNotIn(List<Long> values) {
            addCriterion("UPDATED_TIME not in", values, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeBetween(Long value1, Long value2) {
            addCriterion("UPDATED_TIME between", value1, value2, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andUpdatedTimeNotBetween(Long value1, Long value2) {
            addCriterion("UPDATED_TIME not between", value1, value2, "updatedTime");
            return (Criteria) this;
        }

        public Criteria andActionIsNull() {
            addCriterion("ACTION is null");
            return (Criteria) this;
        }

        public Criteria andActionIsNotNull() {
            addCriterion("ACTION is not null");
            return (Criteria) this;
        }

        public Criteria andActionEqualTo(String value) {
            addCriterion("ACTION =", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionNotEqualTo(String value) {
            addCriterion("ACTION <>", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionGreaterThan(String value) {
            addCriterion("ACTION >", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionGreaterThanOrEqualTo(String value) {
            addCriterion("ACTION >=", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionLessThan(String value) {
            addCriterion("ACTION <", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionLessThanOrEqualTo(String value) {
            addCriterion("ACTION <=", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionLike(String value) {
            addCriterion("ACTION like", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionNotLike(String value) {
            addCriterion("ACTION not like", value, "action");
            return (Criteria) this;
        }

        public Criteria andActionIn(List<String> values) {
            addCriterion("ACTION in", values, "action");
            return (Criteria) this;
        }

        public Criteria andActionNotIn(List<String> values) {
            addCriterion("ACTION not in", values, "action");
            return (Criteria) this;
        }

        public Criteria andActionBetween(String value1, String value2) {
            addCriterion("ACTION between", value1, value2, "action");
            return (Criteria) this;
        }

        public Criteria andActionNotBetween(String value1, String value2) {
            addCriterion("ACTION not between", value1, value2, "action");
            return (Criteria) this;
        }

        public Criteria andOrderByIsNull() {
            addCriterion("ORDER_BY is null");
            return (Criteria) this;
        }

        public Criteria andOrderByIsNotNull() {
            addCriterion("ORDER_BY is not null");
            return (Criteria) this;
        }

        public Criteria andOrderByEqualTo(Integer value) {
            addCriterion("ORDER_BY =", value, "orderBy");
            return (Criteria) this;
        }

        public Criteria andOrderByNotEqualTo(Integer value) {
            addCriterion("ORDER_BY <>", value, "orderBy");
            return (Criteria) this;
        }

        public Criteria andOrderByGreaterThan(Integer value) {
            addCriterion("ORDER_BY >", value, "orderBy");
            return (Criteria) this;
        }

        public Criteria andOrderByGreaterThanOrEqualTo(Integer value) {
            addCriterion("ORDER_BY >=", value, "orderBy");
            return (Criteria) this;
        }

        public Criteria andOrderByLessThan(Integer value) {
            addCriterion("ORDER_BY <", value, "orderBy");
            return (Criteria) this;
        }

        public Criteria andOrderByLessThanOrEqualTo(Integer value) {
            addCriterion("ORDER_BY <=", value, "orderBy");
            return (Criteria) this;
        }

        public Criteria andOrderByIn(List<Integer> values) {
            addCriterion("ORDER_BY in", values, "orderBy");
            return (Criteria) this;
        }

        public Criteria andOrderByNotIn(List<Integer> values) {
            addCriterion("ORDER_BY not in", values, "orderBy");
            return (Criteria) this;
        }

        public Criteria andOrderByBetween(Integer value1, Integer value2) {
            addCriterion("ORDER_BY between", value1, value2, "orderBy");
            return (Criteria) this;
        }

        public Criteria andOrderByNotBetween(Integer value1, Integer value2) {
            addCriterion("ORDER_BY not between", value1, value2, "orderBy");
            return (Criteria) this;
        }

        public Criteria andOperatorNameIsNull() {
            addCriterion("OPERATOR_NAME is null");
            return (Criteria) this;
        }

        public Criteria andOperatorNameIsNotNull() {
            addCriterion("OPERATOR_NAME is not null");
            return (Criteria) this;
        }

        public Criteria andOperatorNameEqualTo(String value) {
            addCriterion("OPERATOR_NAME =", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameNotEqualTo(String value) {
            addCriterion("OPERATOR_NAME <>", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameGreaterThan(String value) {
            addCriterion("OPERATOR_NAME >", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameGreaterThanOrEqualTo(String value) {
            addCriterion("OPERATOR_NAME >=", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameLessThan(String value) {
            addCriterion("OPERATOR_NAME <", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameLessThanOrEqualTo(String value) {
            addCriterion("OPERATOR_NAME <=", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameLike(String value) {
            addCriterion("OPERATOR_NAME like", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameNotLike(String value) {
            addCriterion("OPERATOR_NAME not like", value, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameIn(List<String> values) {
            addCriterion("OPERATOR_NAME in", values, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameNotIn(List<String> values) {
            addCriterion("OPERATOR_NAME not in", values, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameBetween(String value1, String value2) {
            addCriterion("OPERATOR_NAME between", value1, value2, "operatorName");
            return (Criteria) this;
        }

        public Criteria andOperatorNameNotBetween(String value1, String value2) {
            addCriterion("OPERATOR_NAME not between", value1, value2, "operatorName");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated do_not_delete_during_merge
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table nk_doc_def_state
     *
     * @mbggenerated
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}