package cn.nkpro.ts5.docengine.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class NkLinkageFormDefI extends NkDynamicFormDefI {
    private String spELMapping;
}