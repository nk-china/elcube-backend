package cn.nkpro.ts5.fs.model;

import cn.nkpro.ts5.basic.Keep;
import lombok.Data;

@Keep
@Data
public class FileInfo {

    private String name;
    private Long size;
    private String type;
}
