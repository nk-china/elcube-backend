package cn.nkpro.elcube.docengine.gen;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocIBill extends DocIBillKey implements Serializable {
    /**
     * 相关方、付款方
     *
     * @mbggenerated
     */
    private String billPartnerId;

    private Double amount;

    private Double received;

    private Double receivable;

    /**
     * 0 未激活 1 激活
     *
     * @mbggenerated
     */
    private Integer state;

    /**
     * 0 正常 1 过期的、失效的
     *
     * @mbggenerated
     */
    private Integer discard;

    private Long updatedTime;

    private String details;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_doc_i_bill
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * 获取 相关方、付款方
     *
     * @return 相关方、付款方
     *
     * @mbggenerated
     */
    @cn.nkpro.elcube.annotation.CodeFieldNotes("相关方、付款方")
    public String getBillPartnerId() {
        return billPartnerId;
    }

    /**
     * 设置 相关方、付款方
     *
     * @return 相关方、付款方
     *
     * @mbggenerated
     */
    public void setBillPartnerId(String billPartnerId) {
        this.billPartnerId = billPartnerId;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public Double getReceived() {
        return received;
    }

    public void setReceived(Double received) {
        this.received = received;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public Double getReceivable() {
        return receivable;
    }

    public void setReceivable(Double receivable) {
        this.receivable = receivable;
    }

    /**
     * 获取 0 未激活 1 激活
     *
     * @return 0 未激活 1 激活
     *
     * @mbggenerated
     */
    @cn.nkpro.elcube.annotation.CodeFieldNotes("0 未激活 1 激活")
    public Integer getState() {
        return state;
    }

    /**
     * 设置 0 未激活 1 激活
     *
     * @return 0 未激活 1 激活
     *
     * @mbggenerated
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 获取 0 正常 1 过期的、失效的
     *
     * @return 0 正常 1 过期的、失效的
     *
     * @mbggenerated
     */
    @cn.nkpro.elcube.annotation.CodeFieldNotes("0 正常 1 过期的、失效的")
    public Integer getDiscard() {
        return discard;
    }

    /**
     * 设置 0 正常 1 过期的、失效的
     *
     * @return 0 正常 1 过期的、失效的
     *
     * @mbggenerated
     */
    public void setDiscard(Integer discard) {
        this.discard = discard;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public Long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(Long updatedTime) {
        this.updatedTime = updatedTime;
    }

    @cn.nkpro.elcube.annotation.CodeFieldNotes("")
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}