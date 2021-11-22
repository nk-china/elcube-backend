package cn.nkpro.ts5.docengine.cards;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class NkDynamicGridDefI extends NkDynamicFormDefI {
    private Boolean unique;
    private Boolean sortable;
}