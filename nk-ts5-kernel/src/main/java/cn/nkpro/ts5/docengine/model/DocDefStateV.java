package cn.nkpro.ts5.docengine.model;

import cn.nkpro.ts5.docengine.gen.DocDefState;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
public class DocDefStateV extends DocDefState {
    private boolean visible;
}
