package cn.nkpro.ts5.engine.doc.model;

import cn.nkpro.ts5.orm.mb.gen.DocI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocIV extends DocI {
    private Object data;
}
