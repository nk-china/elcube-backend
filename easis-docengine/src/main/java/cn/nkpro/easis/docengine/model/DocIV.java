package cn.nkpro.easis.docengine.model;

import cn.nkpro.easis.docengine.gen.DocI;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
public class DocIV extends DocI {
    private Object data;
}
