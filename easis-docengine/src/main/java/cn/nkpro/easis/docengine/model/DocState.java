package cn.nkpro.easis.docengine.model;

import lombok.Data;

@Data
public class DocState {
    private String docId;

    private String identification;

    private Long updatedTime;
}