package cn.nkpro.easis.docengine.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class NkLinkageFormDefI extends NkDynamicFormDefI {
    private String spELMapping;
}