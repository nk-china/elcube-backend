package cn.nkpro.easis.docengine.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude()
public class CardDescribe {
    private String      cardHandler;
    private String      cardName;
    private String      dataComponentName;
    private String[]    defComponentNames;
}
