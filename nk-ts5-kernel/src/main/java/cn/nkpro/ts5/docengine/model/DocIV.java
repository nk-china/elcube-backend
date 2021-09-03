package cn.nkpro.ts5.docengine.model;

import cn.nkpro.ts5.docengine.gen.DocI;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
public class DocIV extends DocI {
    private Object data;
}
