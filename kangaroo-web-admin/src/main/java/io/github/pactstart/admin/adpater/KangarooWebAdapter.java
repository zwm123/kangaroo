package io.github.pactstart.admin.adpater;

import com.google.common.collect.Lists;
import io.github.pactstart.admin.system.form.UploadSceneForm;
import io.github.pactstart.simple.web.framework.auth.AuthenticationInfo;
import io.github.pactstart.system.dto.ConfigDto;
import io.github.pactstart.system.dto.UploadPathDto;

import java.util.List;

public class KangarooWebAdapter {

    public void afterUpdateConfig(ConfigDto configDto) {

    }

    public void afterConfigReload() {

    }

    public UploadPathDto beforeGetOssSecurityTokenOrPolicy(AuthenticationInfo authenticationInfo, UploadSceneForm uploadSceneForm) {
        UploadPathDto uploadPathDto = new UploadPathDto();
        uploadPathDto.setPath(uploadSceneForm.getScene());
        return uploadPathDto;
    }

    public List<Integer> beforeSendPlatformNotice() {
        return Lists.newArrayList();
    }

}
