package cn.nkpro.ts5.co.fs.model;

import cn.nkpro.ts5.annotation.Keep;
import lombok.Data;

@Keep
@Data
public class FileInfo {

    private String name;
    private Long size;
    private String type;
}
