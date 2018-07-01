package io.github.pactstart.admin.system.form;

import io.github.pactstart.simple.web.framework.common.form.PageForm;

public class AclQueryForm extends PageForm {

    private Integer aclModuleId;

    public Integer getAclModuleId() {
        return aclModuleId;
    }

    public void setAclModuleId(Integer aclModuleId) {
        this.aclModuleId = aclModuleId;
    }
}
