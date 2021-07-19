package cn.nkpro.tfms.platform.model.po;

import java.util.ArrayList;
import java.util.List;

public class DefProjectTypeExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table def_project_type
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table def_project_type
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table def_project_type
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_type
     *
     * @mbggenerated
     */
    public DefProjectTypeExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_type
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_type
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_type
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_type
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_type
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_type
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_type
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
     * This method corresponds to the database table def_project_type
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
     * This method corresponds to the database table def_project_type
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table def_project_type
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
     * This class corresponds to the database table def_project_type
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

        public Criteria andProjectTypeIsNull() {
            addCriterion("PROJECT_TYPE is null");
            return (Criteria) this;
        }

        public Criteria andProjectTypeIsNotNull() {
            addCriterion("PROJECT_TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andProjectTypeEqualTo(String value) {
            addCriterion("PROJECT_TYPE =", value, "projectType");
            return (Criteria) this;
        }

        public Criteria andProjectTypeNotEqualTo(String value) {
            addCriterion("PROJECT_TYPE <>", value, "projectType");
            return (Criteria) this;
        }

        public Criteria andProjectTypeGreaterThan(String value) {
            addCriterion("PROJECT_TYPE >", value, "projectType");
            return (Criteria) this;
        }

        public Criteria andProjectTypeGreaterThanOrEqualTo(String value) {
            addCriterion("PROJECT_TYPE >=", value, "projectType");
            return (Criteria) this;
        }

        public Criteria andProjectTypeLessThan(String value) {
            addCriterion("PROJECT_TYPE <", value, "projectType");
            return (Criteria) this;
        }

        public Criteria andProjectTypeLessThanOrEqualTo(String value) {
            addCriterion("PROJECT_TYPE <=", value, "projectType");
            return (Criteria) this;
        }

        public Criteria andProjectTypeLike(String value) {
            addCriterion("PROJECT_TYPE like", value, "projectType");
            return (Criteria) this;
        }

        public Criteria andProjectTypeNotLike(String value) {
            addCriterion("PROJECT_TYPE not like", value, "projectType");
            return (Criteria) this;
        }

        public Criteria andProjectTypeIn(List<String> values) {
            addCriterion("PROJECT_TYPE in", values, "projectType");
            return (Criteria) this;
        }

        public Criteria andProjectTypeNotIn(List<String> values) {
            addCriterion("PROJECT_TYPE not in", values, "projectType");
            return (Criteria) this;
        }

        public Criteria andProjectTypeBetween(String value1, String value2) {
            addCriterion("PROJECT_TYPE between", value1, value2, "projectType");
            return (Criteria) this;
        }

        public Criteria andProjectTypeNotBetween(String value1, String value2) {
            addCriterion("PROJECT_TYPE not between", value1, value2, "projectType");
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

        public Criteria andProjectTypeDescIsNull() {
            addCriterion("PROJECT_TYPE_DESC is null");
            return (Criteria) this;
        }

        public Criteria andProjectTypeDescIsNotNull() {
            addCriterion("PROJECT_TYPE_DESC is not null");
            return (Criteria) this;
        }

        public Criteria andProjectTypeDescEqualTo(String value) {
            addCriterion("PROJECT_TYPE_DESC =", value, "projectTypeDesc");
            return (Criteria) this;
        }

        public Criteria andProjectTypeDescNotEqualTo(String value) {
            addCriterion("PROJECT_TYPE_DESC <>", value, "projectTypeDesc");
            return (Criteria) this;
        }

        public Criteria andProjectTypeDescGreaterThan(String value) {
            addCriterion("PROJECT_TYPE_DESC >", value, "projectTypeDesc");
            return (Criteria) this;
        }

        public Criteria andProjectTypeDescGreaterThanOrEqualTo(String value) {
            addCriterion("PROJECT_TYPE_DESC >=", value, "projectTypeDesc");
            return (Criteria) this;
        }

        public Criteria andProjectTypeDescLessThan(String value) {
            addCriterion("PROJECT_TYPE_DESC <", value, "projectTypeDesc");
            return (Criteria) this;
        }

        public Criteria andProjectTypeDescLessThanOrEqualTo(String value) {
            addCriterion("PROJECT_TYPE_DESC <=", value, "projectTypeDesc");
            return (Criteria) this;
        }

        public Criteria andProjectTypeDescLike(String value) {
            addCriterion("PROJECT_TYPE_DESC like", value, "projectTypeDesc");
            return (Criteria) this;
        }

        public Criteria andProjectTypeDescNotLike(String value) {
            addCriterion("PROJECT_TYPE_DESC not like", value, "projectTypeDesc");
            return (Criteria) this;
        }

        public Criteria andProjectTypeDescIn(List<String> values) {
            addCriterion("PROJECT_TYPE_DESC in", values, "projectTypeDesc");
            return (Criteria) this;
        }

        public Criteria andProjectTypeDescNotIn(List<String> values) {
            addCriterion("PROJECT_TYPE_DESC not in", values, "projectTypeDesc");
            return (Criteria) this;
        }

        public Criteria andProjectTypeDescBetween(String value1, String value2) {
            addCriterion("PROJECT_TYPE_DESC between", value1, value2, "projectTypeDesc");
            return (Criteria) this;
        }

        public Criteria andProjectTypeDescNotBetween(String value1, String value2) {
            addCriterion("PROJECT_TYPE_DESC not between", value1, value2, "projectTypeDesc");
            return (Criteria) this;
        }

        public Criteria andValidFromIsNull() {
            addCriterion("VALID_FROM is null");
            return (Criteria) this;
        }

        public Criteria andValidFromIsNotNull() {
            addCriterion("VALID_FROM is not null");
            return (Criteria) this;
        }

        public Criteria andValidFromEqualTo(String value) {
            addCriterion("VALID_FROM =", value, "validFrom");
            return (Criteria) this;
        }

        public Criteria andValidFromNotEqualTo(String value) {
            addCriterion("VALID_FROM <>", value, "validFrom");
            return (Criteria) this;
        }

        public Criteria andValidFromGreaterThan(String value) {
            addCriterion("VALID_FROM >", value, "validFrom");
            return (Criteria) this;
        }

        public Criteria andValidFromGreaterThanOrEqualTo(String value) {
            addCriterion("VALID_FROM >=", value, "validFrom");
            return (Criteria) this;
        }

        public Criteria andValidFromLessThan(String value) {
            addCriterion("VALID_FROM <", value, "validFrom");
            return (Criteria) this;
        }

        public Criteria andValidFromLessThanOrEqualTo(String value) {
            addCriterion("VALID_FROM <=", value, "validFrom");
            return (Criteria) this;
        }

        public Criteria andValidFromLike(String value) {
            addCriterion("VALID_FROM like", value, "validFrom");
            return (Criteria) this;
        }

        public Criteria andValidFromNotLike(String value) {
            addCriterion("VALID_FROM not like", value, "validFrom");
            return (Criteria) this;
        }

        public Criteria andValidFromIn(List<String> values) {
            addCriterion("VALID_FROM in", values, "validFrom");
            return (Criteria) this;
        }

        public Criteria andValidFromNotIn(List<String> values) {
            addCriterion("VALID_FROM not in", values, "validFrom");
            return (Criteria) this;
        }

        public Criteria andValidFromBetween(String value1, String value2) {
            addCriterion("VALID_FROM between", value1, value2, "validFrom");
            return (Criteria) this;
        }

        public Criteria andValidFromNotBetween(String value1, String value2) {
            addCriterion("VALID_FROM not between", value1, value2, "validFrom");
            return (Criteria) this;
        }

        public Criteria andValidToIsNull() {
            addCriterion("VALID_TO is null");
            return (Criteria) this;
        }

        public Criteria andValidToIsNotNull() {
            addCriterion("VALID_TO is not null");
            return (Criteria) this;
        }

        public Criteria andValidToEqualTo(String value) {
            addCriterion("VALID_TO =", value, "validTo");
            return (Criteria) this;
        }

        public Criteria andValidToNotEqualTo(String value) {
            addCriterion("VALID_TO <>", value, "validTo");
            return (Criteria) this;
        }

        public Criteria andValidToGreaterThan(String value) {
            addCriterion("VALID_TO >", value, "validTo");
            return (Criteria) this;
        }

        public Criteria andValidToGreaterThanOrEqualTo(String value) {
            addCriterion("VALID_TO >=", value, "validTo");
            return (Criteria) this;
        }

        public Criteria andValidToLessThan(String value) {
            addCriterion("VALID_TO <", value, "validTo");
            return (Criteria) this;
        }

        public Criteria andValidToLessThanOrEqualTo(String value) {
            addCriterion("VALID_TO <=", value, "validTo");
            return (Criteria) this;
        }

        public Criteria andValidToLike(String value) {
            addCriterion("VALID_TO like", value, "validTo");
            return (Criteria) this;
        }

        public Criteria andValidToNotLike(String value) {
            addCriterion("VALID_TO not like", value, "validTo");
            return (Criteria) this;
        }

        public Criteria andValidToIn(List<String> values) {
            addCriterion("VALID_TO in", values, "validTo");
            return (Criteria) this;
        }

        public Criteria andValidToNotIn(List<String> values) {
            addCriterion("VALID_TO not in", values, "validTo");
            return (Criteria) this;
        }

        public Criteria andValidToBetween(String value1, String value2) {
            addCriterion("VALID_TO between", value1, value2, "validTo");
            return (Criteria) this;
        }

        public Criteria andValidToNotBetween(String value1, String value2) {
            addCriterion("VALID_TO not between", value1, value2, "validTo");
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

        public Criteria andStateIsNull() {
            addCriterion("STATE is null");
            return (Criteria) this;
        }

        public Criteria andStateIsNotNull() {
            addCriterion("STATE is not null");
            return (Criteria) this;
        }

        public Criteria andStateEqualTo(String value) {
            addCriterion("STATE =", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotEqualTo(String value) {
            addCriterion("STATE <>", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateGreaterThan(String value) {
            addCriterion("STATE >", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateGreaterThanOrEqualTo(String value) {
            addCriterion("STATE >=", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLessThan(String value) {
            addCriterion("STATE <", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLessThanOrEqualTo(String value) {
            addCriterion("STATE <=", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLike(String value) {
            addCriterion("STATE like", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotLike(String value) {
            addCriterion("STATE not like", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateIn(List<String> values) {
            addCriterion("STATE in", values, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotIn(List<String> values) {
            addCriterion("STATE not in", values, "state");
            return (Criteria) this;
        }

        public Criteria andStateBetween(String value1, String value2) {
            addCriterion("STATE between", value1, value2, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotBetween(String value1, String value2) {
            addCriterion("STATE not between", value1, value2, "state");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table def_project_type
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
     * This class corresponds to the database table def_project_type
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