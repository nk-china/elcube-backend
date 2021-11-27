package cn.nkpro.easis.co;

import cn.nkpro.easis.annotation.Keep;
import cn.nkpro.easis.platform.gen.PlatformScriptWithBLOBs;
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
