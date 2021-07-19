package cn.nkpro.tfms.platform.model;

import cn.nkpro.tfms.platform.model.po.DefDocStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class DefDocStatusExt extends DefDocStatus {

    @Getter@Setter
    private Boolean available = true;

    @Override
    public boolean equals(Object obj) {
        return obj instanceof DefDocStatus && Objects.equals(getDocState(),((DefDocStatus) obj).getDocState());
    }

    @Override
    public int hashCode() {
        return getDocState()!=null?getDocState().hashCode():super.hashCode();
    }
}