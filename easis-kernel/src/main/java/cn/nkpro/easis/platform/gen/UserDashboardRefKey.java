package cn.nkpro.easis.platform.gen;

import java.io.Serializable;

public class UserDashboardRefKey implements Serializable {
    private String accountId;

    private String id;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_user_dashboard_ref
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}