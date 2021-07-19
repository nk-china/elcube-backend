package cn.nkpro.tfms.platform.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class DefDocPartnerBO{
    @Getter
    @Setter
    private List<DefDocPartnerRoleBO> roles;
}
