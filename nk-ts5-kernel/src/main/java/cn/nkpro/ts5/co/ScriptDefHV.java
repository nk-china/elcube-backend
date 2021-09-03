package cn.nkpro.ts5.co;

import cn.nkpro.ts5.co.mybatis.gen.ScriptDefHWithBLOBs;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
public class ScriptDefHV extends ScriptDefHWithBLOBs {
    private boolean debug;
}
