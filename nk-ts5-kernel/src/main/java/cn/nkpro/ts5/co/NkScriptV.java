package cn.nkpro.ts5.co;

import cn.nkpro.ts5.annotation.Keep;
import cn.nkpro.ts5.platform.gen.PlatformScriptWithBLOBs;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude()
@Keep
public class NkScriptV extends PlatformScriptWithBLOBs {
    private boolean debug;
}
