package cn.nkpro.elcube.docengine.gen;

import java.util.ArrayList;
import java.util.List;

public class DocIBillExample {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_i_bill
     *
     * @mbggenerated
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_i_bill
     *
     * @mbggenerated
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_i_bill
     *
     * @mbggenerated
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_bill
     *
     * @mbggenerated
     */
    public DocIBillExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_bill
     *
     * @mbggenerated
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_bill
     *
     * @mbggenerated
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_bill
     *
     * @mbggenerated
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_bill
     *
     * @mbggenerated
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_bill
     *
     * @mbggenerated
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_bill
     *
     * @mbggenerated
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_bill
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
     * This method corresponds to the database table nk_doc_i_bill
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
     * This method corresponds to the database table nk_doc_i_bill
     *
     * @mbggenerated
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table nk_doc_i_bill
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
     * This class corresponds to the database table nk_doc_i_bill
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

        public Criteria andBillPartnerIdIsNull() {
            addCriterion("BILL_PARTNER_ID is null");
            return (Criteria) this;
        }

        public Criteria andBillPartnerIdIsNotNull() {
            addCriterion("BILL_PARTNER_ID is not null");
            return (Criteria) this;
        }

        public Criteria andBillPartnerIdEqualTo(String value) {
            addCriterion("BILL_PARTNER_ID =", value, "billPartnerId");
            return (Criteria) this;
        }

        public Criteria andBillPartnerIdNotEqualTo(String value) {
            addCriterion("BILL_PARTNER_ID <>", value, "billPartnerId");
            return (Criteria) this;
        }

        public Criteria andBillPartnerIdGreaterThan(String value) {
            addCriterion("BILL_PARTNER_ID >", value, "billPartnerId");
            return (Criteria) this;
        }

        public Criteria andBillPartnerIdGreaterThanOrEqualTo(String value) {
            addCriterion("BILL_PARTNER_ID >=", value, "billPartnerId");
            return (Criteria) this;
        }

        public Criteria andBillPartnerIdLessThan(String value) {
            addCriterion("BILL_PARTNER_ID <", value, "billPartnerId");
            return (Criteria) this;
        }

        public Criteria andBillPartnerIdLessThanOrEqualTo(String value) {
            addCriterion("BILL_PARTNER_ID <=", value, "billPartnerId");
            return (Criteria) this;
        }

        public Criteria andBillPartnerIdLike(String value) {
            addCriterion("BILL_PARTNER_ID like", value, "billPartnerId");
            return (Criteria) this;
        }

        public Criteria andBillPartnerIdNotLike(String value) {
            addCriterion("BILL_PARTNER_ID not like", value, "billPartnerId");
            return (Criteria) this;
        }

        public Criteria andBillPartnerIdIn(List<String> values) {
            addCriterion("BILL_PARTNER_ID in", values, "billPartnerId");
            return (Criteria) this;
        }

        public Criteria andBillPartnerIdNotIn(List<String> values) {
            addCriterion("BILL_PARTNER_ID not in", values, "billPartnerId");
            return (Criteria) this;
        }

        public Criteria andBillPartnerIdBetween(String value1, String value2) {
            addCriterion("BILL_PARTNER_ID between", value1, value2, "billPartnerId");
            return (Criteria) this;
        }

        public Criteria andBillPartnerIdNotBetween(String value1, String value2) {
            addCriterion("BILL_PARTNER_ID not between", value1, value2, "billPartnerId");
            return (Criteria) this;
        }

        public Criteria andBillTypeIsNull() {
            addCriterion("BILL_TYPE is null");
            return (Criteria) this;
        }

        public Criteria andBillTypeIsNotNull() {
            addCriterion("BILL_TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andBillTypeEqualTo(String value) {
            addCriterion("BILL_TYPE =", value, "billType");
            return (Criteria) this;
        }

        public Criteria andBillTypeNotEqualTo(String value) {
            addCriterion("BILL_TYPE <>", value, "billType");
            return (Criteria) this;
        }

        public Criteria andBillTypeGreaterThan(String value) {
            addCriterion("BILL_TYPE >", value, "billType");
            return (Criteria) this;
        }

        public Criteria andBillTypeGreaterThanOrEqualTo(String value) {
            addCriterion("BILL_TYPE >=", value, "billType");
            return (Criteria) this;
        }

        public Criteria andBillTypeLessThan(String value) {
            addCriterion("BILL_TYPE <", value, "billType");
            return (Criteria) this;
        }

        public Criteria andBillTypeLessThanOrEqualTo(String value) {
            addCriterion("BILL_TYPE <=", value, "billType");
            return (Criteria) this;
        }

        public Criteria andBillTypeLike(String value) {
            addCriterion("BILL_TYPE like", value, "billType");
            return (Criteria) this;
        }

        public Criteria andBillTypeNotLike(String value) {
            addCriterion("BILL_TYPE not like", value, "billType");
            return (Criteria) this;
        }

        public Criteria andBillTypeIn(List<String> values) {
            addCriterion("BILL_TYPE in", values, "billType");
            return (Criteria) this;
        }

        public Criteria andBillTypeNotIn(List<String> values) {
            addCriterion("BILL_TYPE not in", values, "billType");
            return (Criteria) this;
        }

        public Criteria andBillTypeBetween(String value1, String value2) {
            addCriterion("BILL_TYPE between", value1, value2, "billType");
            return (Criteria) this;
        }

        public Criteria andBillTypeNotBetween(String value1, String value2) {
            addCriterion("BILL_TYPE not between", value1, value2, "billType");
            return (Criteria) this;
        }

        public Criteria andCardKeyIsNull() {
            addCriterion("CARD_KEY is null");
            return (Criteria) this;
        }

        public Criteria andCardKeyIsNotNull() {
            addCriterion("CARD_KEY is not null");
            return (Criteria) this;
        }

        public Criteria andCardKeyEqualTo(String value) {
            addCriterion("CARD_KEY =", value, "cardKey");
            return (Criteria) this;
        }

        public Criteria andCardKeyNotEqualTo(String value) {
            addCriterion("CARD_KEY <>", value, "cardKey");
            return (Criteria) this;
        }

        public Criteria andCardKeyGreaterThan(String value) {
            addCriterion("CARD_KEY >", value, "cardKey");
            return (Criteria) this;
        }

        public Criteria andCardKeyGreaterThanOrEqualTo(String value) {
            addCriterion("CARD_KEY >=", value, "cardKey");
            return (Criteria) this;
        }

        public Criteria andCardKeyLessThan(String value) {
            addCriterion("CARD_KEY <", value, "cardKey");
            return (Criteria) this;
        }

        public Criteria andCardKeyLessThanOrEqualTo(String value) {
            addCriterion("CARD_KEY <=", value, "cardKey");
            return (Criteria) this;
        }

        public Criteria andCardKeyLike(String value) {
            addCriterion("CARD_KEY like", value, "cardKey");
            return (Criteria) this;
        }

        public Criteria andCardKeyNotLike(String value) {
            addCriterion("CARD_KEY not like", value, "cardKey");
            return (Criteria) this;
        }

        public Criteria andCardKeyIn(List<String> values) {
            addCriterion("CARD_KEY in", values, "cardKey");
            return (Criteria) this;
        }

        public Criteria andCardKeyNotIn(List<String> values) {
            addCriterion("CARD_KEY not in", values, "cardKey");
            return (Criteria) this;
        }

        public Criteria andCardKeyBetween(String value1, String value2) {
            addCriterion("CARD_KEY between", value1, value2, "cardKey");
            return (Criteria) this;
        }

        public Criteria andCardKeyNotBetween(String value1, String value2) {
            addCriterion("CARD_KEY not between", value1, value2, "cardKey");
            return (Criteria) this;
        }

        public Criteria andDocIdIsNull() {
            addCriterion("DOC_ID is null");
            return (Criteria) this;
        }

        public Criteria andDocIdIsNotNull() {
            addCriterion("DOC_ID is not null");
            return (Criteria) this;
        }

        public Criteria andDocIdEqualTo(String value) {
            addCriterion("DOC_ID =", value, "docId");
            return (Criteria) this;
        }

        public Criteria andDocIdNotEqualTo(String value) {
            addCriterion("DOC_ID <>", value, "docId");
            return (Criteria) this;
        }

        public Criteria andDocIdGreaterThan(String value) {
            addCriterion("DOC_ID >", value, "docId");
            return (Criteria) this;
        }

        public Criteria andDocIdGreaterThanOrEqualTo(String value) {
            addCriterion("DOC_ID >=", value, "docId");
            return (Criteria) this;
        }

        public Criteria andDocIdLessThan(String value) {
            addCriterion("DOC_ID <", value, "docId");
            return (Criteria) this;
        }

        public Criteria andDocIdLessThanOrEqualTo(String value) {
            addCriterion("DOC_ID <=", value, "docId");
            return (Criteria) this;
        }

        public Criteria andDocIdLike(String value) {
            addCriterion("DOC_ID like", value, "docId");
            return (Criteria) this;
        }

        public Criteria andDocIdNotLike(String value) {
            addCriterion("DOC_ID not like", value, "docId");
            return (Criteria) this;
        }

        public Criteria andDocIdIn(List<String> values) {
            addCriterion("DOC_ID in", values, "docId");
            return (Criteria) this;
        }

        public Criteria andDocIdNotIn(List<String> values) {
            addCriterion("DOC_ID not in", values, "docId");
            return (Criteria) this;
        }

        public Criteria andDocIdBetween(String value1, String value2) {
            addCriterion("DOC_ID between", value1, value2, "docId");
            return (Criteria) this;
        }

        public Criteria andDocIdNotBetween(String value1, String value2) {
            addCriterion("DOC_ID not between", value1, value2, "docId");
            return (Criteria) this;
        }

        public Criteria andExpireDateIsNull() {
            addCriterion("EXPIRE_DATE is null");
            return (Criteria) this;
        }

        public Criteria andExpireDateIsNotNull() {
            addCriterion("EXPIRE_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andExpireDateEqualTo(Long value) {
            addCriterion("EXPIRE_DATE =", value, "expireDate");
            return (Criteria) this;
        }

        public Criteria andExpireDateNotEqualTo(Long value) {
            addCriterion("EXPIRE_DATE <>", value, "expireDate");
            return (Criteria) this;
        }

        public Criteria andExpireDateGreaterThan(Long value) {
            addCriterion("EXPIRE_DATE >", value, "expireDate");
            return (Criteria) this;
        }

        public Criteria andExpireDateGreaterThanOrEqualTo(Long value) {
            addCriterion("EXPIRE_DATE >=", value, "expireDate");
            return (Criteria) this;
        }

        public Criteria andExpireDateLessThan(Long value) {
            addCriterion("EXPIRE_DATE <", value, "expireDate");
            return (Criteria) this;
        }

        public Criteria andExpireDateLessThanOrEqualTo(Long value) {
            addCriterion("EXPIRE_DATE <=", value, "expireDate");
            return (Criteria) this;
        }

        public Criteria andExpireDateIn(List<Long> values) {
            addCriterion("EXPIRE_DATE in", values, "expireDate");
            return (Criteria) this;
        }

        public Criteria andExpireDateNotIn(List<Long> values) {
            addCriterion("EXPIRE_DATE not in", values, "expireDate");
            return (Criteria) this;
        }

        public Criteria andExpireDateBetween(Long value1, Long value2) {
            addCriterion("EXPIRE_DATE between", value1, value2, "expireDate");
            return (Criteria) this;
        }

        public Criteria andExpireDateNotBetween(Long value1, Long value2) {
            addCriterion("EXPIRE_DATE not between", value1, value2, "expireDate");
            return (Criteria) this;
        }

        public Criteria andAmountIsNull() {
            addCriterion("AMOUNT is null");
            return (Criteria) this;
        }

        public Criteria andAmountIsNotNull() {
            addCriterion("AMOUNT is not null");
            return (Criteria) this;
        }

        public Criteria andAmountEqualTo(Double value) {
            addCriterion("AMOUNT =", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountNotEqualTo(Double value) {
            addCriterion("AMOUNT <>", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountGreaterThan(Double value) {
            addCriterion("AMOUNT >", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountGreaterThanOrEqualTo(Double value) {
            addCriterion("AMOUNT >=", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountLessThan(Double value) {
            addCriterion("AMOUNT <", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountLessThanOrEqualTo(Double value) {
            addCriterion("AMOUNT <=", value, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountIn(List<Double> values) {
            addCriterion("AMOUNT in", values, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountNotIn(List<Double> values) {
            addCriterion("AMOUNT not in", values, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountBetween(Double value1, Double value2) {
            addCriterion("AMOUNT between", value1, value2, "amount");
            return (Criteria) this;
        }

        public Criteria andAmountNotBetween(Double value1, Double value2) {
            addCriterion("AMOUNT not between", value1, value2, "amount");
            return (Criteria) this;
        }

        public Criteria andReceivedIsNull() {
            addCriterion("RECEIVED is null");
            return (Criteria) this;
        }

        public Criteria andReceivedIsNotNull() {
            addCriterion("RECEIVED is not null");
            return (Criteria) this;
        }

        public Criteria andReceivedEqualTo(Double value) {
            addCriterion("RECEIVED =", value, "received");
            return (Criteria) this;
        }

        public Criteria andReceivedNotEqualTo(Double value) {
            addCriterion("RECEIVED <>", value, "received");
            return (Criteria) this;
        }

        public Criteria andReceivedGreaterThan(Double value) {
            addCriterion("RECEIVED >", value, "received");
            return (Criteria) this;
        }

        public Criteria andReceivedGreaterThanOrEqualTo(Double value) {
            addCriterion("RECEIVED >=", value, "received");
            return (Criteria) this;
        }

        public Criteria andReceivedLessThan(Double value) {
            addCriterion("RECEIVED <", value, "received");
            return (Criteria) this;
        }

        public Criteria andReceivedLessThanOrEqualTo(Double value) {
            addCriterion("RECEIVED <=", value, "received");
            return (Criteria) this;
        }

        public Criteria andReceivedIn(List<Double> values) {
            addCriterion("RECEIVED in", values, "received");
            return (Criteria) this;
        }

        public Criteria andReceivedNotIn(List<Double> values) {
            addCriterion("RECEIVED not in", values, "received");
            return (Criteria) this;
        }

        public Criteria andReceivedBetween(Double value1, Double value2) {
            addCriterion("RECEIVED between", value1, value2, "received");
            return (Criteria) this;
        }

        public Criteria andReceivedNotBetween(Double value1, Double value2) {
            addCriterion("RECEIVED not between", value1, value2, "received");
            return (Criteria) this;
        }

        public Criteria andReceivableIsNull() {
            addCriterion("RECEIVABLE is null");
            return (Criteria) this;
        }

        public Criteria andReceivableIsNotNull() {
            addCriterion("RECEIVABLE is not null");
            return (Criteria) this;
        }

        public Criteria andReceivableEqualTo(Double value) {
            addCriterion("RECEIVABLE =", value, "receivable");
            return (Criteria) this;
        }

        public Criteria andReceivableNotEqualTo(Double value) {
            addCriterion("RECEIVABLE <>", value, "receivable");
            return (Criteria) this;
        }

        public Criteria andReceivableGreaterThan(Double value) {
            addCriterion("RECEIVABLE >", value, "receivable");
            return (Criteria) this;
        }

        public Criteria andReceivableGreaterThanOrEqualTo(Double value) {
            addCriterion("RECEIVABLE >=", value, "receivable");
            return (Criteria) this;
        }

        public Criteria andReceivableLessThan(Double value) {
            addCriterion("RECEIVABLE <", value, "receivable");
            return (Criteria) this;
        }

        public Criteria andReceivableLessThanOrEqualTo(Double value) {
            addCriterion("RECEIVABLE <=", value, "receivable");
            return (Criteria) this;
        }

        public Criteria andReceivableIn(List<Double> values) {
            addCriterion("RECEIVABLE in", values, "receivable");
            return (Criteria) this;
        }

        public Criteria andReceivableNotIn(List<Double> values) {
            addCriterion("RECEIVABLE not in", values, "receivable");
            return (Criteria) this;
        }

        public Criteria andReceivableBetween(Double value1, Double value2) {
            addCriterion("RECEIVABLE between", value1, value2, "receivable");
            return (Criteria) this;
        }

        public Criteria andReceivableNotBetween(Double value1, Double value2) {
            addCriterion("RECEIVABLE not between", value1, value2, "receivable");
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

        public Criteria andStateEqualTo(Integer value) {
            addCriterion("STATE =", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotEqualTo(Integer value) {
            addCriterion("STATE <>", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateGreaterThan(Integer value) {
            addCriterion("STATE >", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateGreaterThanOrEqualTo(Integer value) {
            addCriterion("STATE >=", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLessThan(Integer value) {
            addCriterion("STATE <", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateLessThanOrEqualTo(Integer value) {
            addCriterion("STATE <=", value, "state");
            return (Criteria) this;
        }

        public Criteria andStateIn(List<Integer> values) {
            addCriterion("STATE in", values, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotIn(List<Integer> values) {
            addCriterion("STATE not in", values, "state");
            return (Criteria) this;
        }

        public Criteria andStateBetween(Integer value1, Integer value2) {
            addCriterion("STATE between", value1, value2, "state");
            return (Criteria) this;
        }

        public Criteria andStateNotBetween(Integer value1, Integer value2) {
            addCriterion("STATE not between", value1, value2, "state");
            return (Criteria) this;
        }

        public Criteria andDiscardIsNull() {
            addCriterion("DISCARD is null");
            return (Criteria) this;
        }

        public Criteria andDiscardIsNotNull() {
            addCriterion("DISCARD is not null");
            return (Criteria) this;
        }

        public Criteria andDiscardEqualTo(Integer value) {
            addCriterion("DISCARD =", value, "discard");
            return (Criteria) this;
        }

        public Criteria andDiscardNotEqualTo(Integer value) {
            addCriterion("DISCARD <>", value, "discard");
            return (Criteria) this;
        }

        public Criteria andDiscardGreaterThan(Integer value) {
            addCriterion("DISCARD >", value, "discard");
            return (Criteria) this;
        }

        public Criteria andDiscardGreaterThanOrEqualTo(Integer value) {
            addCriterion("DISCARD >=", value, "discard");
            return (Criteria) this;
        }

        public Criteria andDiscardLessThan(Integer value) {
            addCriterion("DISCARD <", value, "discard");
            return (Criteria) this;
        }

        public Criteria andDiscardLessThanOrEqualTo(Integer value) {
            addCriterion("DISCARD <=", value, "discard");
            return (Criteria) this;
        }

        public Criteria andDiscardIn(List<Integer> values) {
            addCriterion("DISCARD in", values, "discard");
            return (Criteria) this;
        }

        public Criteria andDiscardNotIn(List<Integer> values) {
            addCriterion("DISCARD not in", values, "discard");
            return (Criteria) this;
        }

        public Criteria andDiscardBetween(Integer value1, Integer value2) {
            addCriterion("DISCARD between", value1, value2, "discard");
            return (Criteria) this;
        }

        public Criteria andDiscardNotBetween(Integer value1, Integer value2) {
            addCriterion("DISCARD not between", value1, value2, "discard");
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
     * This class corresponds to the database table nk_doc_i_bill
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
     * This class corresponds to the database table nk_doc_i_bill
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