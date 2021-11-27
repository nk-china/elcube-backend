package cn.nkpro.easis.docengine.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class NkLinkageFormDef {
    private Integer col;
    private List<NkLinkageFormDefI> items = new ArrayList<>();
}