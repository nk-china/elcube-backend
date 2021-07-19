package cn.nkpro.tfms.platform.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface DefModalSelectorAble {

    DefModalSelectorProxy getModalSelector();

    void setModalSelector(DefModalSelectorProxy modalSelector);

}
