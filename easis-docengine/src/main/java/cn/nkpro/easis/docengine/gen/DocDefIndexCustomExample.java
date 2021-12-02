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

public class DocDefIndexCustomExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    public DocDefIndexCustomExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
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
     * This method corresponds to the database table nk_doc_def_index_custom
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
     * This method corresponds to the database table nk_doc_def_index_custom
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_def_index_custom
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
     * This class corresponds to the database table nk_doc_def_index_custom
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

        public Criteria andCustomTypeIsNull() {
            addCriterion("CUSTOM_TYPE is null");
            return (Criteria) this;
        }

        public Criteria andCustomTypeIsNotNull() {
            addCriterion("CUSTOM_TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andCustomTypeEqualTo(String value) {
            addCriterion("CUSTOM_TYPE =", value, "customType");
            return (Criteria) this;
        }

        public Criteria andCustomTypeNotEqualTo(String value) {
            addCriterion("CUSTOM_TYPE <>", value, "customType");
            return (Criteria) this;
        }

        public Criteria andCustomTypeGreaterThan(String value) {
            addCriterion("CUSTOM_TYPE >", value, "customType");
            return (Criteria) this;
        }

        public Criteria andCustomTypeGreaterThanOrEqualTo(String value) {
            addCriterion("CUSTOM_TYPE >=", value, "customType");
            return (Criteria) this;
        }

        public Criteria andCustomTypeLessThan(String value) {
            addCriterion("CUSTOM_TYPE <", value, "customType");
            return (Criteria) this;
        }

        public Criteria andCustomTypeLessThanOrEqualTo(String value) {
            addCriterion("CUSTOM_TYPE <=", value, "customType");
            return (Criteria) this;
        }

        public Criteria andCustomTypeLike(String value) {
            addCriterion("CUSTOM_TYPE like", value, "customType");
            return (Criteria) this;
        }

        public Criteria andCustomTypeNotLike(String value) {
            addCriterion("CUSTOM_TYPE not like", value, "customType");
            return (Criteria) this;
        }

        public Criteria andCustomTypeIn(List<String> values) {
            addCriterion("CUSTOM_TYPE in", values, "customType");
            return (Criteria) this;
        }

        public Criteria andCustomTypeNotIn(List<String> values) {
            addCriterion("CUSTOM_TYPE not in", values, "customType");
            return (Criteria) this;
        }

        public Criteria andCustomTypeBetween(String value1, String value2) {
            addCriterion("CUSTOM_TYPE between", value1, value2, "customType");
            return (Criteria) this;
        }

        public Criteria andCustomTypeNotBetween(String value1, String value2) {
            addCriterion("CUSTOM_TYPE not between", value1, value2, "customType");
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

        public Criteria andConditionSpELIsNull() {
            addCriterion("CONDITION_SP_E_L is null");
            return (Criteria) this;
        }

        public Criteria andConditionSpELIsNotNull() {
            addCriterion("CONDITION_SP_E_L is not null");
            return (Criteria) this;
        }

        public Criteria andConditionSpELEqualTo(String value) {
            addCriterion("CONDITION_SP_E_L =", value, "conditionSpEL");
            return (Criteria) this;
        }

        public Criteria andConditionSpELNotEqualTo(String value) {
            addCriterion("CONDITION_SP_E_L <>", value, "conditionSpEL");
            return (Criteria) this;
        }

        public Criteria andConditionSpELGreaterThan(String value) {
            addCriterion("CONDITION_SP_E_L >", value, "conditionSpEL");
            return (Criteria) this;
        }

        public Criteria andConditionSpELGreaterThanOrEqualTo(String value) {
            addCriterion("CONDITION_SP_E_L >=", value, "conditionSpEL");
            return (Criteria) this;
        }

        public Criteria andConditionSpELLessThan(String value) {
            addCriterion("CONDITION_SP_E_L <", value, "conditionSpEL");
            return (Criteria) this;
        }

        public Criteria andConditionSpELLessThanOrEqualTo(String value) {
            addCriterion("CONDITION_SP_E_L <=", value, "conditionSpEL");
            return (Criteria) this;
        }

        public Criteria andConditionSpELLike(String value) {
            addCriterion("CONDITION_SP_E_L like", value, "conditionSpEL");
            return (Criteria) this;
        }

        public Criteria andConditionSpELNotLike(String value) {
            addCriterion("CONDITION_SP_E_L not like", value, "conditionSpEL");
            return (Criteria) this;
        }

        public Criteria andConditionSpELIn(List<String> values) {
            addCriterion("CONDITION_SP_E_L in", values, "conditionSpEL");
            return (Criteria) this;
        }

        public Criteria andConditionSpELNotIn(List<String> values) {
            addCriterion("CONDITION_SP_E_L not in", values, "conditionSpEL");
            return (Criteria) this;
        }

        public Criteria andConditionSpELBetween(String value1, String value2) {
            addCriterion("CONDITION_SP_E_L between", value1, value2, "conditionSpEL");
            return (Criteria) this;
        }

        public Criteria andConditionSpELNotBetween(String value1, String value2) {
            addCriterion("CONDITION_SP_E_L not between", value1, value2, "conditionSpEL");
            return (Criteria) this;
        }

        public Criteria andDataSpELIsNull() {
            addCriterion("DATA_SP_E_L is null");
            return (Criteria) this;
        }

        public Criteria andDataSpELIsNotNull() {
            addCriterion("DATA_SP_E_L is not null");
            return (Criteria) this;
        }

        public Criteria andDataSpELEqualTo(String value) {
            addCriterion("DATA_SP_E_L =", value, "dataSpEL");
            return (Criteria) this;
        }

        public Criteria andDataSpELNotEqualTo(String value) {
            addCriterion("DATA_SP_E_L <>", value, "dataSpEL");
            return (Criteria) this;
        }

        public Criteria andDataSpELGreaterThan(String value) {
            addCriterion("DATA_SP_E_L >", value, "dataSpEL");
            return (Criteria) this;
        }

        public Criteria andDataSpELGreaterThanOrEqualTo(String value) {
            addCriterion("DATA_SP_E_L >=", value, "dataSpEL");
            return (Criteria) this;
        }

        public Criteria andDataSpELLessThan(String value) {
            addCriterion("DATA_SP_E_L <", value, "dataSpEL");
            return (Criteria) this;
        }

        public Criteria andDataSpELLessThanOrEqualTo(String value) {
            addCriterion("DATA_SP_E_L <=", value, "dataSpEL");
            return (Criteria) this;
        }

        public Criteria andDataSpELLike(String value) {
            addCriterion("DATA_SP_E_L like", value, "dataSpEL");
            return (Criteria) this;
        }

        public Criteria andDataSpELNotLike(String value) {
            addCriterion("DATA_SP_E_L not like", value, "dataSpEL");
            return (Criteria) this;
        }

        public Criteria andDataSpELIn(List<String> values) {
            addCriterion("DATA_SP_E_L in", values, "dataSpEL");
            return (Criteria) this;
        }

        public Criteria andDataSpELNotIn(List<String> values) {
            addCriterion("DATA_SP_E_L not in", values, "dataSpEL");
            return (Criteria) this;
        }

        public Criteria andDataSpELBetween(String value1, String value2) {
            addCriterion("DATA_SP_E_L between", value1, value2, "dataSpEL");
            return (Criteria) this;
        }

        public Criteria andDataSpELNotBetween(String value1, String value2) {
            addCriterion("DATA_SP_E_L not between", value1, value2, "dataSpEL");
            return (Criteria) this;
        }

        public Criteria andKeySpELIsNull() {
            addCriterion("KEY_SP_E_L is null");
            return (Criteria) this;
        }

        public Criteria andKeySpELIsNotNull() {
            addCriterion("KEY_SP_E_L is not null");
            return (Criteria) this;
        }

        public Criteria andKeySpELEqualTo(String value) {
            addCriterion("KEY_SP_E_L =", value, "keySpEL");
            return (Criteria) this;
        }

        public Criteria andKeySpELNotEqualTo(String value) {
            addCriterion("KEY_SP_E_L <>", value, "keySpEL");
            return (Criteria) this;
        }

        public Criteria andKeySpELGreaterThan(String value) {
            addCriterion("KEY_SP_E_L >", value, "keySpEL");
            return (Criteria) this;
        }

        public Criteria andKeySpELGreaterThanOrEqualTo(String value) {
            addCriterion("KEY_SP_E_L >=", value, "keySpEL");
            return (Criteria) this;
        }

        public Criteria andKeySpELLessThan(String value) {
            addCriterion("KEY_SP_E_L <", value, "keySpEL");
            return (Criteria) this;
        }

        public Criteria andKeySpELLessThanOrEqualTo(String value) {
            addCriterion("KEY_SP_E_L <=", value, "keySpEL");
            return (Criteria) this;
        }

        public Criteria andKeySpELLike(String value) {
            addCriterion("KEY_SP_E_L like", value, "keySpEL");
            return (Criteria) this;
        }

        public Criteria andKeySpELNotLike(String value) {
            addCriterion("KEY_SP_E_L not like", value, "keySpEL");
            return (Criteria) this;
        }

        public Criteria andKeySpELIn(List<String> values) {
            addCriterion("KEY_SP_E_L in", values, "keySpEL");
            return (Criteria) this;
        }

        public Criteria andKeySpELNotIn(List<String> values) {
            addCriterion("KEY_SP_E_L not in", values, "keySpEL");
            return (Criteria) this;
        }

        public Criteria andKeySpELBetween(String value1, String value2) {
            addCriterion("KEY_SP_E_L between", value1, value2, "keySpEL");
            return (Criteria) this;
        }

        public Criteria andKeySpELNotBetween(String value1, String value2) {
            addCriterion("KEY_SP_E_L not between", value1, value2, "keySpEL");
            return (Criteria) this;
        }

        public Criteria andMappingSpELIsNull() {
            addCriterion("MAPPING_SP_E_L is null");
            return (Criteria) this;
        }

        public Criteria andMappingSpELIsNotNull() {
            addCriterion("MAPPING_SP_E_L is not null");
            return (Criteria) this;
        }

        public Criteria andMappingSpELEqualTo(String value) {
            addCriterion("MAPPING_SP_E_L =", value, "mappingSpEL");
            return (Criteria) this;
        }

        public Criteria andMappingSpELNotEqualTo(String value) {
            addCriterion("MAPPING_SP_E_L <>", value, "mappingSpEL");
            return (Criteria) this;
        }

        public Criteria andMappingSpELGreaterThan(String value) {
            addCriterion("MAPPING_SP_E_L >", value, "mappingSpEL");
            return (Criteria) this;
        }

        public Criteria andMappingSpELGreaterThanOrEqualTo(String value) {
            addCriterion("MAPPING_SP_E_L >=", value, "mappingSpEL");
            return (Criteria) this;
        }

        public Criteria andMappingSpELLessThan(String value) {
            addCriterion("MAPPING_SP_E_L <", value, "mappingSpEL");
            return (Criteria) this;
        }

        public Criteria andMappingSpELLessThanOrEqualTo(String value) {
            addCriterion("MAPPING_SP_E_L <=", value, "mappingSpEL");
            return (Criteria) this;
        }

        public Criteria andMappingSpELLike(String value) {
            addCriterion("MAPPING_SP_E_L like", value, "mappingSpEL");
            return (Criteria) this;
        }

        public Criteria andMappingSpELNotLike(String value) {
            addCriterion("MAPPING_SP_E_L not like", value, "mappingSpEL");
            return (Criteria) this;
        }

        public Criteria andMappingSpELIn(List<String> values) {
            addCriterion("MAPPING_SP_E_L in", values, "mappingSpEL");
            return (Criteria) this;
        }

        public Criteria andMappingSpELNotIn(List<String> values) {
            addCriterion("MAPPING_SP_E_L not in", values, "mappingSpEL");
            return (Criteria) this;
        }

        public Criteria andMappingSpELBetween(String value1, String value2) {
            addCriterion("MAPPING_SP_E_L between", value1, value2, "mappingSpEL");
            return (Criteria) this;
        }

        public Criteria andMappingSpELNotBetween(String value1, String value2) {
            addCriterion("MAPPING_SP_E_L not between", value1, value2, "mappingSpEL");
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
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table nk_doc_def_index_custom
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
     * This class corresponds to the database table nk_doc_def_index_custom
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