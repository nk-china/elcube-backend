package cn.nkpro.ts5.docengine.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class NkDynamicGridDef {
    private Boolean seq;
    private Boolean sortable;
    private String preSpEL;
    private List<NkDynamicGridDefI> items = new ArrayList<>();
}