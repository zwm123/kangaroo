package io.github.pactstart.system.service.impl;

import com.google.common.collect.Lists;
import io.github.pactstart.biz.common.dto.OperateDto;
import io.github.pactstart.biz.common.utils.CollectionsUtils;
import io.github.pactstart.system.dao.SysRoleAclMapper;
import io.github.pactstart.system.dto.RoleAclListDto;
import io.github.pactstart.system.entity.SysRoleAcl;
import io.github.pactstart.system.service.SysLogService;
import io.github.pactstart.system.service.SysRoleAclService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SysRoleAclServiceImpl implements SysRoleAclService {

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;

    @Autowired
    private SysLogService sysLogService;

    @Transactional
    @Override
    public void changeRoleAcls(RoleAclListDto roleAclListDto) {
        Integer roleId = roleAclListDto.getRoleId();
        List<Integer> aclIdList = roleAclListDto.getAclList();

        List<Integer> originAclIdList = sysRoleAclMapper.getAclIdListByRoleIdList(Lists.newArrayList(roleId));
        //验证角色拥有的权限点有无变化，没有变化直接返回
        if (CollectionsUtils.equals(originAclIdList, aclIdList)) {
            return;
        }
        updateRoleAcls(roleId, aclIdList, roleAclListDto);
        sysLogService.saveRoleAclLog(roleId, originAclIdList, aclIdList, roleAclListDto);
    }

    private void updateRoleAcls(int roleId, List<Integer> aclIdList, OperateDto operateDto) {
        sysRoleAclMapper.deleteByRoleId(roleId);

        if (CollectionUtils.isEmpty(aclIdList)) {
            return;
        }
        List<SysRoleAcl> roleAclList = Lists.newArrayList();
        for (Integer aclId : aclIdList) {
            SysRoleAcl roleAcl = SysRoleAcl.builder().roleId(roleId).aclId(aclId)
                    .operator(operateDto.getOperator()).operateIp(operateDto.getOperateIp()).operateTime(new Date()).build();
            roleAclList.add(roleAcl);
        }
        sysRoleAclMapper.batchInsert(roleAclList);
    }
}
