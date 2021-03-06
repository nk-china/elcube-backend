/*
 * This file is part of ELCube.
 *
 * ELCube is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ELCube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ELCube.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.nkpro.elcube.docengine.gen;

import java.util.ArrayList;
import java.util.List;

public class DocAsyncQueueExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_async_queue
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_async_queue
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_async_queue
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_async_queue
     *
     * @mbggenerated
     */
    public DocAsyncQueueExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_async_queue
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_async_queue
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_async_queue
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_async_queue
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_async_queue
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_async_queue
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_async_queue
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
     * This method corresponds to the database table nk_doc_async_queue
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
     * This method corresponds to the database table nk_doc_async_queue
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_async_queue
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
     * This class corresponds to the database table nk_doc_async_queue
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

        public Criteria andAsyncIdIsNull() {
            addCriterion("ASYNC_ID is null");
            return (Criteria) this;
        }

        public Criteria andAsyncIdIsNotNull() {
            addCriterion("ASYNC_ID is not null");
            return (Criteria) this;
        }

        public Criteria andAsyncIdEqualTo(String value) {
            addCriterion("ASYNC_ID =", value, "asyncId");
            return (Criteria) this;
        }

        public Criteria andAsyncIdNotEqualTo(String value) {
            addCriterion("ASYNC_ID <>", value, "asyncId");
            return (Criteria) this;
        }

        public Criteria andAsyncIdGreaterThan(String value) {
            addCriterion("ASYNC_ID >", value, "asyncId");
            return (Criteria) this;
        }

        public Criteria andAsyncIdGreaterThanOrEqualTo(String value) {
            addCriterion("ASYNC_ID >=", value, "asyncId");
            return (Criteria) this;
        }

        public Criteria andAsyncIdLessThan(String value) {
            addCriterion("ASYNC_ID <", value, "asyncId");
            return (Criteria) this;
        }

        public Criteria andAsyncIdLessThanOrEqualTo(String value) {
            addCriterion("ASYNC_ID <=", value, "asyncId");
            return (Criteria) this;
        }

        public Criteria andAsyncIdLike(String value) {
            addCriterion("ASYNC_ID like", value, "asyncId");
            return (Criteria) this;
        }

        public Criteria andAsyncIdNotLike(String value) {
            addCriterion("ASYNC_ID not like", value, "asyncId");
            return (Criteria) this;
        }

        public Criteria andAsyncIdIn(List<String> values) {
            addCriterion("ASYNC_ID in", values, "asyncId");
            return (Criteria) this;
        }

        public Criteria andAsyncIdNotIn(List<String> values) {
            addCriterion("ASYNC_ID not in", values, "asyncId");
            return (Criteria) this;
        }

        public Criteria andAsyncIdBetween(String value1, String value2) {
            addCriterion("ASYNC_ID between", value1, value2, "asyncId");
            return (Criteria) this;
        }

        public Criteria andAsyncIdNotBetween(String value1, String value2) {
            addCriterion("ASYNC_ID not between", value1, value2, "asyncId");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefIsNull() {
            addCriterion("ASYNC_OBJECT_REF is null");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefIsNotNull() {
            addCriterion("ASYNC_OBJECT_REF is not null");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefEqualTo(String value) {
            addCriterion("ASYNC_OBJECT_REF =", value, "asyncObjectRef");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefNotEqualTo(String value) {
            addCriterion("ASYNC_OBJECT_REF <>", value, "asyncObjectRef");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefGreaterThan(String value) {
            addCriterion("ASYNC_OBJECT_REF >", value, "asyncObjectRef");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefGreaterThanOrEqualTo(String value) {
            addCriterion("ASYNC_OBJECT_REF >=", value, "asyncObjectRef");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefLessThan(String value) {
            addCriterion("ASYNC_OBJECT_REF <", value, "asyncObjectRef");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefLessThanOrEqualTo(String value) {
            addCriterion("ASYNC_OBJECT_REF <=", value, "asyncObjectRef");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefLike(String value) {
            addCriterion("ASYNC_OBJECT_REF like", value, "asyncObjectRef");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefNotLike(String value) {
            addCriterion("ASYNC_OBJECT_REF not like", value, "asyncObjectRef");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefIn(List<String> values) {
            addCriterion("ASYNC_OBJECT_REF in", values, "asyncObjectRef");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefNotIn(List<String> values) {
            addCriterion("ASYNC_OBJECT_REF not in", values, "asyncObjectRef");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefBetween(String value1, String value2) {
            addCriterion("ASYNC_OBJECT_REF between", value1, value2, "asyncObjectRef");
            return (Criteria) this;
        }

        public Criteria andAsyncObjectRefNotBetween(String value1, String value2) {
            addCriterion("ASYNC_OBJECT_REF not between", value1, value2, "asyncObjectRef");
            return (Criteria) this;
        }

        public Criteria andAsyncRetryIsNull() {
            addCriterion("ASYNC_RETRY is null");
            return (Criteria) this;
        }

        public Criteria andAsyncRetryIsNotNull() {
            addCriterion("ASYNC_RETRY is not null");
            return (Criteria) this;
        }

        public Criteria andAsyncRetryEqualTo(Integer value) {
            addCriterion("ASYNC_RETRY =", value, "asyncRetry");
            return (Criteria) this;
        }

        public Criteria andAsyncRetryNotEqualTo(Integer value) {
            addCriterion("ASYNC_RETRY <>", value, "asyncRetry");
            return (Criteria) this;
        }

        public Criteria andAsyncRetryGreaterThan(Integer value) {
            addCriterion("ASYNC_RETRY >", value, "asyncRetry");
            return (Criteria) this;
        }

        public Criteria andAsyncRetryGreaterThanOrEqualTo(Integer value) {
            addCriterion("ASYNC_RETRY >=", value, "asyncRetry");
            return (Criteria) this;
        }

        public Criteria andAsyncRetryLessThan(Integer value) {
            addCriterion("ASYNC_RETRY <", value, "asyncRetry");
            return (Criteria) this;
        }

        public Criteria andAsyncRetryLessThanOrEqualTo(Integer value) {
            addCriterion("ASYNC_RETRY <=", value, "asyncRetry");
            return (Criteria) this;
        }

        public Criteria andAsyncRetryIn(List<Integer> values) {
            addCriterion("ASYNC_RETRY in", values, "asyncRetry");
            return (Criteria) this;
        }

        public Criteria andAsyncRetryNotIn(List<Integer> values) {
            addCriterion("ASYNC_RETRY not in", values, "asyncRetry");
            return (Criteria) this;
        }

        public Criteria andAsyncRetryBetween(Integer value1, Integer value2) {
            addCriterion("ASYNC_RETRY between", value1, value2, "asyncRetry");
            return (Criteria) this;
        }

        public Criteria andAsyncRetryNotBetween(Integer value1, Integer value2) {
            addCriterion("ASYNC_RETRY not between", value1, value2, "asyncRetry");
            return (Criteria) this;
        }

        public Criteria andAsyncLimitIsNull() {
            addCriterion("ASYNC_LIMIT is null");
            return (Criteria) this;
        }

        public Criteria andAsyncLimitIsNotNull() {
            addCriterion("ASYNC_LIMIT is not null");
            return (Criteria) this;
        }

        public Criteria andAsyncLimitEqualTo(Integer value) {
            addCriterion("ASYNC_LIMIT =", value, "asyncLimit");
            return (Criteria) this;
        }

        public Criteria andAsyncLimitNotEqualTo(Integer value) {
            addCriterion("ASYNC_LIMIT <>", value, "asyncLimit");
            return (Criteria) this;
        }

        public Criteria andAsyncLimitGreaterThan(Integer value) {
            addCriterion("ASYNC_LIMIT >", value, "asyncLimit");
            return (Criteria) this;
        }

        public Criteria andAsyncLimitGreaterThanOrEqualTo(Integer value) {
            addCriterion("ASYNC_LIMIT >=", value, "asyncLimit");
            return (Criteria) this;
        }

        public Criteria andAsyncLimitLessThan(Integer value) {
            addCriterion("ASYNC_LIMIT <", value, "asyncLimit");
            return (Criteria) this;
        }

        public Criteria andAsyncLimitLessThanOrEqualTo(Integer value) {
            addCriterion("ASYNC_LIMIT <=", value, "asyncLimit");
            return (Criteria) this;
        }

        public Criteria andAsyncLimitIn(List<Integer> values) {
            addCriterion("ASYNC_LIMIT in", values, "asyncLimit");
            return (Criteria) this;
        }

        public Criteria andAsyncLimitNotIn(List<Integer> values) {
            addCriterion("ASYNC_LIMIT not in", values, "asyncLimit");
            return (Criteria) this;
        }

        public Criteria andAsyncLimitBetween(Integer value1, Integer value2) {
            addCriterion("ASYNC_LIMIT between", value1, value2, "asyncLimit");
            return (Criteria) this;
        }

        public Criteria andAsyncLimitNotBetween(Integer value1, Integer value2) {
            addCriterion("ASYNC_LIMIT not between", value1, value2, "asyncLimit");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleIsNull() {
            addCriterion("ASYNC_RULE is null");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleIsNotNull() {
            addCriterion("ASYNC_RULE is not null");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleEqualTo(String value) {
            addCriterion("ASYNC_RULE =", value, "asyncRule");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleNotEqualTo(String value) {
            addCriterion("ASYNC_RULE <>", value, "asyncRule");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleGreaterThan(String value) {
            addCriterion("ASYNC_RULE >", value, "asyncRule");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleGreaterThanOrEqualTo(String value) {
            addCriterion("ASYNC_RULE >=", value, "asyncRule");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleLessThan(String value) {
            addCriterion("ASYNC_RULE <", value, "asyncRule");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleLessThanOrEqualTo(String value) {
            addCriterion("ASYNC_RULE <=", value, "asyncRule");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleLike(String value) {
            addCriterion("ASYNC_RULE like", value, "asyncRule");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleNotLike(String value) {
            addCriterion("ASYNC_RULE not like", value, "asyncRule");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleIn(List<String> values) {
            addCriterion("ASYNC_RULE in", values, "asyncRule");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleNotIn(List<String> values) {
            addCriterion("ASYNC_RULE not in", values, "asyncRule");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleBetween(String value1, String value2) {
            addCriterion("ASYNC_RULE between", value1, value2, "asyncRule");
            return (Criteria) this;
        }

        public Criteria andAsyncRuleNotBetween(String value1, String value2) {
            addCriterion("ASYNC_RULE not between", value1, value2, "asyncRule");
            return (Criteria) this;
        }

        public Criteria andAsyncStateIsNull() {
            addCriterion("ASYNC_STATE is null");
            return (Criteria) this;
        }

        public Criteria andAsyncStateIsNotNull() {
            addCriterion("ASYNC_STATE is not null");
            return (Criteria) this;
        }

        public Criteria andAsyncStateEqualTo(String value) {
            addCriterion("ASYNC_STATE =", value, "asyncState");
            return (Criteria) this;
        }

        public Criteria andAsyncStateNotEqualTo(String value) {
            addCriterion("ASYNC_STATE <>", value, "asyncState");
            return (Criteria) this;
        }

        public Criteria andAsyncStateGreaterThan(String value) {
            addCriterion("ASYNC_STATE >", value, "asyncState");
            return (Criteria) this;
        }

        public Criteria andAsyncStateGreaterThanOrEqualTo(String value) {
            addCriterion("ASYNC_STATE >=", value, "asyncState");
            return (Criteria) this;
        }

        public Criteria andAsyncStateLessThan(String value) {
            addCriterion("ASYNC_STATE <", value, "asyncState");
            return (Criteria) this;
        }

        public Criteria andAsyncStateLessThanOrEqualTo(String value) {
            addCriterion("ASYNC_STATE <=", value, "asyncState");
            return (Criteria) this;
        }

        public Criteria andAsyncStateLike(String value) {
            addCriterion("ASYNC_STATE like", value, "asyncState");
            return (Criteria) this;
        }

        public Criteria andAsyncStateNotLike(String value) {
            addCriterion("ASYNC_STATE not like", value, "asyncState");
            return (Criteria) this;
        }

        public Criteria andAsyncStateIn(List<String> values) {
            addCriterion("ASYNC_STATE in", values, "asyncState");
            return (Criteria) this;
        }

        public Criteria andAsyncStateNotIn(List<String> values) {
            addCriterion("ASYNC_STATE not in", values, "asyncState");
            return (Criteria) this;
        }

        public Criteria andAsyncStateBetween(String value1, String value2) {
            addCriterion("ASYNC_STATE between", value1, value2, "asyncState");
            return (Criteria) this;
        }

        public Criteria andAsyncStateNotBetween(String value1, String value2) {
            addCriterion("ASYNC_STATE not between", value1, value2, "asyncState");
            return (Criteria) this;
        }

        public Criteria andAsyncNextIsNull() {
            addCriterion("ASYNC_NEXT is null");
            return (Criteria) this;
        }

        public Criteria andAsyncNextIsNotNull() {
            addCriterion("ASYNC_NEXT is not null");
            return (Criteria) this;
        }

        public Criteria andAsyncNextEqualTo(String value) {
            addCriterion("ASYNC_NEXT =", value, "asyncNext");
            return (Criteria) this;
        }

        public Criteria andAsyncNextNotEqualTo(String value) {
            addCriterion("ASYNC_NEXT <>", value, "asyncNext");
            return (Criteria) this;
        }

        public Criteria andAsyncNextGreaterThan(String value) {
            addCriterion("ASYNC_NEXT >", value, "asyncNext");
            return (Criteria) this;
        }

        public Criteria andAsyncNextGreaterThanOrEqualTo(String value) {
            addCriterion("ASYNC_NEXT >=", value, "asyncNext");
            return (Criteria) this;
        }

        public Criteria andAsyncNextLessThan(String value) {
            addCriterion("ASYNC_NEXT <", value, "asyncNext");
            return (Criteria) this;
        }

        public Criteria andAsyncNextLessThanOrEqualTo(String value) {
            addCriterion("ASYNC_NEXT <=", value, "asyncNext");
            return (Criteria) this;
        }

        public Criteria andAsyncNextLike(String value) {
            addCriterion("ASYNC_NEXT like", value, "asyncNext");
            return (Criteria) this;
        }

        public Criteria andAsyncNextNotLike(String value) {
            addCriterion("ASYNC_NEXT not like", value, "asyncNext");
            return (Criteria) this;
        }

        public Criteria andAsyncNextIn(List<String> values) {
            addCriterion("ASYNC_NEXT in", values, "asyncNext");
            return (Criteria) this;
        }

        public Criteria andAsyncNextNotIn(List<String> values) {
            addCriterion("ASYNC_NEXT not in", values, "asyncNext");
            return (Criteria) this;
        }

        public Criteria andAsyncNextBetween(String value1, String value2) {
            addCriterion("ASYNC_NEXT between", value1, value2, "asyncNext");
            return (Criteria) this;
        }

        public Criteria andAsyncNextNotBetween(String value1, String value2) {
            addCriterion("ASYNC_NEXT not between", value1, value2, "asyncNext");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIsNull() {
            addCriterion("CREATED_TIME is null");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIsNotNull() {
            addCriterion("CREATED_TIME is not null");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeEqualTo(Long value) {
            addCriterion("CREATED_TIME =", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotEqualTo(Long value) {
            addCriterion("CREATED_TIME <>", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeGreaterThan(Long value) {
            addCriterion("CREATED_TIME >", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeGreaterThanOrEqualTo(Long value) {
            addCriterion("CREATED_TIME >=", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeLessThan(Long value) {
            addCriterion("CREATED_TIME <", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeLessThanOrEqualTo(Long value) {
            addCriterion("CREATED_TIME <=", value, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeIn(List<Long> values) {
            addCriterion("CREATED_TIME in", values, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotIn(List<Long> values) {
            addCriterion("CREATED_TIME not in", values, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeBetween(Long value1, Long value2) {
            addCriterion("CREATED_TIME between", value1, value2, "createdTime");
            return (Criteria) this;
        }

        public Criteria andCreatedTimeNotBetween(Long value1, Long value2) {
            addCriterion("CREATED_TIME not between", value1, value2, "createdTime");
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
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table nk_doc_async_queue
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
     * This class corresponds to the database table nk_doc_async_queue
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