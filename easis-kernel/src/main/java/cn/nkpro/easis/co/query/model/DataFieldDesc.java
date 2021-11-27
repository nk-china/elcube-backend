package cn.nkpro.easis.co.query.model;

import lombok.Data;

@Data
public class DataFieldDesc {
    private String name;
    private String type;
    private Boolean aggregatable;
    private Boolean searchable;
}
