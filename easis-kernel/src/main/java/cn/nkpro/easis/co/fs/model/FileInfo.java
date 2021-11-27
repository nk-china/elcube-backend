package cn.nkpro.easis.co.fs.model;

import cn.nkpro.easis.annotation.Keep;
import lombok.Data;

@Keep
@Data
public class FileInfo {

    private String name;
    private Long size;
    private String type;
}
