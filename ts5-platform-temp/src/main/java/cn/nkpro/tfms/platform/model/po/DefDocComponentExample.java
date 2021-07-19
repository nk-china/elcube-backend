package cn.nkpro.tfms.platform.model.po;

import java.util.ArrayList;
import java.util.List;

public class DefDocComponentExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table def_doc_component
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table def_doc_component
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table def_doc_component
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_component
     *
     * @mbggenerated
     */
    public DefDocComponentExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_component
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_component
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_component
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_component
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_component
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_component
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_component
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
     * This method corresponds to the database table def_doc_component
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
     * This method corresponds to the database table def_doc_component
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_doc_component
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
     * This class corresponds to the database table def_doc_component
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

        public Criteria andComponentIsNull() {
            addCriterion("COMPONENT is null");
            return (Criteria) this;
        }

        public Criteria andComponentIsNotNull() {
            addCriterion("COMPONENT is not null");
            return (Criteria) this;
        }

        public Criteria andComponentEqualTo(String value) {
            addCriterion("COMPONENT =", value, "component");
            return (Criteria) this;
        }

        public Criteria andComponentNotEqualTo(String value) {
            addCriterion("COMPONENT <>", value, "component");
            return (Criteria) this;
        }

        public Criteria andComponentGreaterThan(String value) {
            addCriterion("COMPONENT >", value, "component");
            return (Criteria) this;
        }

        public Criteria andComponentGreaterThanOrEqualTo(String value) {
            addCriterion("COMPONENT >=", value, "component");
            return (Criteria) this;
        }

        public Criteria andComponentLessThan(String value) {
            addCriterion("COMPONENT <", value, "component");
            return (Criteria) this;
        }

        public Criteria andComponentLessThanOrEqualTo(String value) {
            addCriterion("COMPONENT <=", value, "component");
            return (Criteria) this;
        }

        public Criteria andComponentLike(String value) {
            addCriterion("COMPONENT like", value, "component");
            return (Criteria) this;
        }

        public Criteria andComponentNotLike(String value) {
            addCriterion("COMPONENT not like", value, "component");
            return (Criteria) this;
        }

        public Criteria andComponentIn(List<String> values) {
            addCriterion("COMPONENT in", values, "component");
            return (Criteria) this;
        }

        public Criteria andComponentNotIn(List<String> values) {
            addCriterion("COMPONENT not in", values, "component");
            return (Criteria) this;
        }

        public Criteria andComponentBetween(String value1, String value2) {
            addCriterion("COMPONENT between", value1, value2, "component");
            return (Criteria) this;
        }

        public Criteria andComponentNotBetween(String value1, String value2) {
            addCriterion("COMPONENT not between", value1, value2, "component");
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

        public Criteria andVersionEqualTo(Integer value) {
            addCriterion("VERSION =", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotEqualTo(Integer value) {
            addCriterion("VERSION <>", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThan(Integer value) {
            addCriterion("VERSION >", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionGreaterThanOrEqualTo(Integer value) {
            addCriterion("VERSION >=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThan(Integer value) {
            addCriterion("VERSION <", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionLessThanOrEqualTo(Integer value) {
            addCriterion("VERSION <=", value, "version");
            return (Criteria) this;
        }

        public Criteria andVersionIn(List<Integer> values) {
            addCriterion("VERSION in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotIn(List<Integer> values) {
            addCriterion("VERSION not in", values, "version");
            return (Criteria) this;
        }

        public Criteria andVersionBetween(Integer value1, Integer value2) {
            addCriterion("VERSION between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andVersionNotBetween(Integer value1, Integer value2) {
            addCriterion("VERSION not between", value1, value2, "version");
            return (Criteria) this;
        }

        public Criteria andComponentMappingIsNull() {
            addCriterion("COMPONENT_MAPPING is null");
            return (Criteria) this;
        }

        public Criteria andComponentMappingIsNotNull() {
            addCriterion("COMPONENT_MAPPING is not null");
            return (Criteria) this;
        }

        public Criteria andComponentMappingEqualTo(String value) {
            addCriterion("COMPONENT_MAPPING =", value, "componentMapping");
            return (Criteria) this;
        }

        public Criteria andComponentMappingNotEqualTo(String value) {
            addCriterion("COMPONENT_MAPPING <>", value, "componentMapping");
            return (Criteria) this;
        }

        public Criteria andComponentMappingGreaterThan(String value) {
            addCriterion("COMPONENT_MAPPING >", value, "componentMapping");
            return (Criteria) this;
        }

        public Criteria andComponentMappingGreaterThanOrEqualTo(String value) {
            addCriterion("COMPONENT_MAPPING >=", value, "componentMapping");
            return (Criteria) this;
        }

        public Criteria andComponentMappingLessThan(String value) {
            addCriterion("COMPONENT_MAPPING <", value, "componentMapping");
            return (Criteria) this;
        }

        public Criteria andComponentMappingLessThanOrEqualTo(String value) {
            addCriterion("COMPONENT_MAPPING <=", value, "componentMapping");
            return (Criteria) this;
        }

        public Criteria andComponentMappingLike(String value) {
            addCriterion("COMPONENT_MAPPING like", value, "componentMapping");
            return (Criteria) this;
        }

        public Criteria andComponentMappingNotLike(String value) {
            addCriterion("COMPONENT_MAPPING not like", value, "componentMapping");
            return (Criteria) this;
        }

        public Criteria andComponentMappingIn(List<String> values) {
            addCriterion("COMPONENT_MAPPING in", values, "componentMapping");
            return (Criteria) this;
        }

        public Criteria andComponentMappingNotIn(List<String> values) {
            addCriterion("COMPONENT_MAPPING not in", values, "componentMapping");
            return (Criteria) this;
        }

        public Criteria andComponentMappingBetween(String value1, String value2) {
            addCriterion("COMPONENT_MAPPING between", value1, value2, "componentMapping");
            return (Criteria) this;
        }

        public Criteria andComponentMappingNotBetween(String value1, String value2) {
            addCriterion("COMPONENT_MAPPING not between", value1, value2, "componentMapping");
            return (Criteria) this;
        }

        public Criteria andComponentNameIsNull() {
            addCriterion("COMPONENT_NAME is null");
            return (Criteria) this;
        }

        public Criteria andComponentNameIsNotNull() {
            addCriterion("COMPONENT_NAME is not null");
            return (Criteria) this;
        }

        public Criteria andComponentNameEqualTo(String value) {
            addCriterion("COMPONENT_NAME =", value, "componentName");
            return (Criteria) this;
        }

        public Criteria andComponentNameNotEqualTo(String value) {
            addCriterion("COMPONENT_NAME <>", value, "componentName");
            return (Criteria) this;
        }

        public Criteria andComponentNameGreaterThan(String value) {
            addCriterion("COMPONENT_NAME >", value, "componentName");
            return (Criteria) this;
        }

        public Criteria andComponentNameGreaterThanOrEqualTo(String value) {
            addCriterion("COMPONENT_NAME >=", value, "componentName");
            return (Criteria) this;
        }

        public Criteria andComponentNameLessThan(String value) {
            addCriterion("COMPONENT_NAME <", value, "componentName");
            return (Criteria) this;
        }

        public Criteria andComponentNameLessThanOrEqualTo(String value) {
            addCriterion("COMPONENT_NAME <=", value, "componentName");
            return (Criteria) this;
        }

        public Criteria andComponentNameLike(String value) {
            addCriterion("COMPONENT_NAME like", value, "componentName");
            return (Criteria) this;
        }

        public Criteria andComponentNameNotLike(String value) {
            addCriterion("COMPONENT_NAME not like", value, "componentName");
            return (Criteria) this;
        }

        public Criteria andComponentNameIn(List<String> values) {
            addCriterion("COMPONENT_NAME in", values, "componentName");
            return (Criteria) this;
        }

        public Criteria andComponentNameNotIn(List<String> values) {
            addCriterion("COMPONENT_NAME not in", values, "componentName");
            return (Criteria) this;
        }

        public Criteria andComponentNameBetween(String value1, String value2) {
            addCriterion("COMPONENT_NAME between", value1, value2, "componentName");
            return (Criteria) this;
        }

        public Criteria andComponentNameNotBetween(String value1, String value2) {
            addCriterion("COMPONENT_NAME not between", value1, value2, "componentName");
            return (Criteria) this;
        }

        public Criteria andOrderbyIsNull() {
            addCriterion("ORDERBY is null");
            return (Criteria) this;
        }

        public Criteria andOrderbyIsNotNull() {
            addCriterion("ORDERBY is not null");
            return (Criteria) this;
        }

        public Criteria andOrderbyEqualTo(Integer value) {
            addCriterion("ORDERBY =", value, "orderby");
            return (Criteria) this;
        }

        public Criteria andOrderbyNotEqualTo(Integer value) {
            addCriterion("ORDERBY <>", value, "orderby");
            return (Criteria) this;
        }

        public Criteria andOrderbyGreaterThan(Integer value) {
            addCriterion("ORDERBY >", value, "orderby");
            return (Criteria) this;
        }

        public Criteria andOrderbyGreaterThanOrEqualTo(Integer value) {
            addCriterion("ORDERBY >=", value, "orderby");
            return (Criteria) this;
        }

        public Criteria andOrderbyLessThan(Integer value) {
            addCriterion("ORDERBY <", value, "orderby");
            return (Criteria) this;
        }

        public Criteria andOrderbyLessThanOrEqualTo(Integer value) {
            addCriterion("ORDERBY <=", value, "orderby");
            return (Criteria) this;
        }

        public Criteria andOrderbyIn(List<Integer> values) {
            addCriterion("ORDERBY in", values, "orderby");
            return (Criteria) this;
        }

        public Criteria andOrderbyNotIn(List<Integer> values) {
            addCriterion("ORDERBY not in", values, "orderby");
            return (Criteria) this;
        }

        public Criteria andOrderbyBetween(Integer value1, Integer value2) {
            addCriterion("ORDERBY between", value1, value2, "orderby");
            return (Criteria) this;
        }

        public Criteria andOrderbyNotBetween(Integer value1, Integer value2) {
            addCriterion("ORDERBY not between", value1, value2, "orderby");
            return (Criteria) this;
        }

        public Criteria andOrderCalcIsNull() {
            addCriterion("ORDER_CALC is null");
            return (Criteria) this;
        }

        public Criteria andOrderCalcIsNotNull() {
            addCriterion("ORDER_CALC is not null");
            return (Criteria) this;
        }

        public Criteria andOrderCalcEqualTo(Integer value) {
            addCriterion("ORDER_CALC =", value, "orderCalc");
            return (Criteria) this;
        }

        public Criteria andOrderCalcNotEqualTo(Integer value) {
            addCriterion("ORDER_CALC <>", value, "orderCalc");
            return (Criteria) this;
        }

        public Criteria andOrderCalcGreaterThan(Integer value) {
            addCriterion("ORDER_CALC >", value, "orderCalc");
            return (Criteria) this;
        }

        public Criteria andOrderCalcGreaterThanOrEqualTo(Integer value) {
            addCriterion("ORDER_CALC >=", value, "orderCalc");
            return (Criteria) this;
        }

        public Criteria andOrderCalcLessThan(Integer value) {
            addCriterion("ORDER_CALC <", value, "orderCalc");
            return (Criteria) this;
        }

        public Criteria andOrderCalcLessThanOrEqualTo(Integer value) {
            addCriterion("ORDER_CALC <=", value, "orderCalc");
            return (Criteria) this;
        }

        public Criteria andOrderCalcIn(List<Integer> values) {
            addCriterion("ORDER_CALC in", values, "orderCalc");
            return (Criteria) this;
        }

        public Criteria andOrderCalcNotIn(List<Integer> values) {
            addCriterion("ORDER_CALC not in", values, "orderCalc");
            return (Criteria) this;
        }

        public Criteria andOrderCalcBetween(Integer value1, Integer value2) {
            addCriterion("ORDER_CALC between", value1, value2, "orderCalc");
            return (Criteria) this;
        }

        public Criteria andOrderCalcNotBetween(Integer value1, Integer value2) {
            addCriterion("ORDER_CALC not between", value1, value2, "orderCalc");
            return (Criteria) this;
        }

        public Criteria andTimesCalcIsNull() {
            addCriterion("TIMES_CALC is null");
            return (Criteria) this;
        }

        public Criteria andTimesCalcIsNotNull() {
            addCriterion("TIMES_CALC is not null");
            return (Criteria) this;
        }

        public Criteria andTimesCalcEqualTo(Integer value) {
            addCriterion("TIMES_CALC =", value, "timesCalc");
            return (Criteria) this;
        }

        public Criteria andTimesCalcNotEqualTo(Integer value) {
            addCriterion("TIMES_CALC <>", value, "timesCalc");
            return (Criteria) this;
        }

        public Criteria andTimesCalcGreaterThan(Integer value) {
            addCriterion("TIMES_CALC >", value, "timesCalc");
            return (Criteria) this;
        }

        public Criteria andTimesCalcGreaterThanOrEqualTo(Integer value) {
            addCriterion("TIMES_CALC >=", value, "timesCalc");
            return (Criteria) this;
        }

        public Criteria andTimesCalcLessThan(Integer value) {
            addCriterion("TIMES_CALC <", value, "timesCalc");
            return (Criteria) this;
        }

        public Criteria andTimesCalcLessThanOrEqualTo(Integer value) {
            addCriterion("TIMES_CALC <=", value, "timesCalc");
            return (Criteria) this;
        }

        public Criteria andTimesCalcIn(List<Integer> values) {
            addCriterion("TIMES_CALC in", values, "timesCalc");
            return (Criteria) this;
        }

        public Criteria andTimesCalcNotIn(List<Integer> values) {
            addCriterion("TIMES_CALC not in", values, "timesCalc");
            return (Criteria) this;
        }

        public Criteria andTimesCalcBetween(Integer value1, Integer value2) {
            addCriterion("TIMES_CALC between", value1, value2, "timesCalc");
            return (Criteria) this;
        }

        public Criteria andTimesCalcNotBetween(Integer value1, Integer value2) {
            addCriterion("TIMES_CALC not between", value1, value2, "timesCalc");
            return (Criteria) this;
        }

        public Criteria andEditableSpELIsNull() {
            addCriterion("EDITABLE_SP_E_L is null");
            return (Criteria) this;
        }

        public Criteria andEditableSpELIsNotNull() {
            addCriterion("EDITABLE_SP_E_L is not null");
            return (Criteria) this;
        }

        public Criteria andEditableSpELEqualTo(String value) {
            addCriterion("EDITABLE_SP_E_L =", value, "editableSpEL");
            return (Criteria) this;
        }

        public Criteria andEditableSpELNotEqualTo(String value) {
            addCriterion("EDITABLE_SP_E_L <>", value, "editableSpEL");
            return (Criteria) this;
        }

        public Criteria andEditableSpELGreaterThan(String value) {
            addCriterion("EDITABLE_SP_E_L >", value, "editableSpEL");
            return (Criteria) this;
        }

        public Criteria andEditableSpELGreaterThanOrEqualTo(String value) {
            addCriterion("EDITABLE_SP_E_L >=", value, "editableSpEL");
            return (Criteria) this;
        }

        public Criteria andEditableSpELLessThan(String value) {
            addCriterion("EDITABLE_SP_E_L <", value, "editableSpEL");
            return (Criteria) this;
        }

        public Criteria andEditableSpELLessThanOrEqualTo(String value) {
            addCriterion("EDITABLE_SP_E_L <=", value, "editableSpEL");
            return (Criteria) this;
        }

        public Criteria andEditableSpELLike(String value) {
            addCriterion("EDITABLE_SP_E_L like", value, "editableSpEL");
            return (Criteria) this;
        }

        public Criteria andEditableSpELNotLike(String value) {
            addCriterion("EDITABLE_SP_E_L not like", value, "editableSpEL");
            return (Criteria) this;
        }

        public Criteria andEditableSpELIn(List<String> values) {
            addCriterion("EDITABLE_SP_E_L in", values, "editableSpEL");
            return (Criteria) this;
        }

        public Criteria andEditableSpELNotIn(List<String> values) {
            addCriterion("EDITABLE_SP_E_L not in", values, "editableSpEL");
            return (Criteria) this;
        }

        public Criteria andEditableSpELBetween(String value1, String value2) {
            addCriterion("EDITABLE_SP_E_L between", value1, value2, "editableSpEL");
            return (Criteria) this;
        }

        public Criteria andEditableSpELNotBetween(String value1, String value2) {
            addCriterion("EDITABLE_SP_E_L not between", value1, value2, "editableSpEL");
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

        public Criteria andMarkdownFlagIsNull() {
            addCriterion("MARKDOWN_FLAG is null");
            return (Criteria) this;
        }

        public Criteria andMarkdownFlagIsNotNull() {
            addCriterion("MARKDOWN_FLAG is not null");
            return (Criteria) this;
        }

        public Criteria andMarkdownFlagEqualTo(Integer value) {
            addCriterion("MARKDOWN_FLAG =", value, "markdownFlag");
            return (Criteria) this;
        }

        public Criteria andMarkdownFlagNotEqualTo(Integer value) {
            addCriterion("MARKDOWN_FLAG <>", value, "markdownFlag");
            return (Criteria) this;
        }

        public Criteria andMarkdownFlagGreaterThan(Integer value) {
            addCriterion("MARKDOWN_FLAG >", value, "markdownFlag");
            return (Criteria) this;
        }

        public Criteria andMarkdownFlagGreaterThanOrEqualTo(Integer value) {
            addCriterion("MARKDOWN_FLAG >=", value, "markdownFlag");
            return (Criteria) this;
        }

        public Criteria andMarkdownFlagLessThan(Integer value) {
            addCriterion("MARKDOWN_FLAG <", value, "markdownFlag");
            return (Criteria) this;
        }

        public Criteria andMarkdownFlagLessThanOrEqualTo(Integer value) {
            addCriterion("MARKDOWN_FLAG <=", value, "markdownFlag");
            return (Criteria) this;
        }

        public Criteria andMarkdownFlagIn(List<Integer> values) {
            addCriterion("MARKDOWN_FLAG in", values, "markdownFlag");
            return (Criteria) this;
        }

        public Criteria andMarkdownFlagNotIn(List<Integer> values) {
            addCriterion("MARKDOWN_FLAG not in", values, "markdownFlag");
            return (Criteria) this;
        }

        public Criteria andMarkdownFlagBetween(Integer value1, Integer value2) {
            addCriterion("MARKDOWN_FLAG between", value1, value2, "markdownFlag");
            return (Criteria) this;
        }

        public Criteria andMarkdownFlagNotBetween(Integer value1, Integer value2) {
            addCriterion("MARKDOWN_FLAG not between", value1, value2, "markdownFlag");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table def_doc_component
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
     * This class corresponds to the database table def_doc_component
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