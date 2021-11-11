package cn.nkpro.ts5.dataengine.model;

import lombok.Data;

@Data
public class DataFieldDesc {
    private String name;
    private String type;
    private Boolean aggregatable;
    private Boolean searchable;
}
