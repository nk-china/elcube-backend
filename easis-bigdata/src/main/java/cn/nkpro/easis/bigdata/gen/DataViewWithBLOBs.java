package cn.nkpro.easis.bigdata.gen;

import java.io.Serializable;

public class DataViewWithBLOBs extends DataView implements Serializable {
    private String thumbnail;

    private String config;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table nk_data_view
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @cn.nkpro.easis.annotation.CodeFieldNotes("")
    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }
}