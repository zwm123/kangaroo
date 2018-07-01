package io.github.pactstart.system.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Maps;
import io.github.pactstart.biz.common.dto.PageResultDto;
import io.github.pactstart.biz.common.errorcode.ResponseCode;
import io.github.pactstart.biz.common.exception.ApplicationException;
import io.github.pactstart.biz.common.utils.MapperUtils;
import io.github.pactstart.biz.common.utils.PageUtils;
import io.github.pactstart.biz.common.utils.SpringContextHolder;
import io.github.pactstart.commonutils.JsonUtils;
import io.github.pactstart.jpush.autoconfigure.JPushService;
import io.github.pactstart.jpush.autoconfigure.PushObject;
import io.github.pactstart.system.dao.MemberNoticeMapper;
import io.github.pactstart.system.dao.NoticeReadedMapper;
import io.github.pactstart.system.dao.PlatformNoticeMapper;
import io.github.pactstart.system.dto.*;
import io.github.pactstart.system.entity.MemberNotice;
import io.github.pactstart.system.entity.NoticeReaded;
import io.github.pactstart.system.entity.PlatformNotice;
import io.github.pactstart.system.enums.MemberNoticeStatusEnum;
import io.github.pactstart.system.enums.NoticeTypeEnum;
import io.github.pactstart.system.enums.PlatformNoticeStatusEnum;
import io.github.pactstart.system.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private MemberNoticeMapper memberNoticeMapper;

    @Autowired
    private PlatformNoticeMapper platformNoticeMapper;

    @Autowired
    private NoticeReadedMapper noticeReadedMapper;

    @Autowired
    private JPushService jPushService;

    @Autowired
    @Override
    public void sendMemberNotice(MemberNoticeSendDto sendDto) {
        MemberNotice memberNotice = MapperUtils.map(sendDto, MemberNotice.class);
        memberNotice.setReaded(false);
        memberNotice.setDel(false);
        memberNotice.setStatus(MemberNoticeStatusEnum.SENDING.getValue());
        memberNotice.setCreateTime(new Date());
        memberNoticeMapper.insert(memberNotice);

        Map<String, Object> extras = Maps.newHashMap();
        extras.put("bizType", memberNotice.getBizType());
        extras.put("showType", memberNotice.getShowType());
        extras.put("noticeType", sendDto.getNoticeType());
        boolean result = sendJpush(getAlias(sendDto.getMemberId()), sendDto.getTitle(), JsonUtils.obj2String(sendDto.getContent()), extras);

        memberNoticeMapper.updateStatus(memberNotice.getId(), result ? MemberNoticeStatusEnum.SEND_SUCCESS.getValue() : MemberNoticeStatusEnum.SEND_FAIL.getValue());
    }

    @Override
    public MemberNoticeDto readMemberNotice(MemberNoticeReadDto readDto) {
        MemberNotice memberNotice = memberNoticeMapper.selectByPrimaryKey(readDto.getId());
        if (memberNotice == null || memberNotice.getDel() || !memberNotice.getMemberId().equals(readDto.getMemberId())) {
            throw new ApplicationException(ResponseCode.INVALID_PARAM, "消息不存在或非法操作");
        }

        memberNoticeMapper.updateReadedById(memberNotice.getId());
        memberNotice.setReaded(true);
        return MapperUtils.map(memberNotice, MemberNoticeDto.class);
    }

    @Override
    public void readMemberNoticeAll(Integer memberId) {
        memberNoticeMapper.updateReadedByMemberId(memberId);
    }

    @Override
    public PageResultDto<MemberNoticeDto> queryMemberNotice(MemberNoticeQueryDto queryDto) {
        Page<MemberNotice> result = PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize()).doSelectPage(() -> memberNoticeMapper.query(queryDto));
        return PageUtils.convert(result, MemberNoticeDto.class);
    }

    @Override
    public void addPlatformNotice(PlatformNoticeAddDto addDto) {
        PlatformNotice platformNotice = MapperUtils.map(addDto, PlatformNotice.class);
        platformNotice.setStatus(PlatformNoticeStatusEnum.DRAFT.getValue());
        platformNotice.setReadCount(0);
        platformNotice.setOperateTime(new Date());
        platformNotice.setCreateTime(new Date());
        platformNoticeMapper.insert(platformNotice);
    }

    @Override
    public void sendPlatformNotice(PlatformNoticeSendDto platformNoticeSendDto) {
        PlatformNotice platformNotice = platformNoticeMapper.selectByPrimaryKey(platformNoticeSendDto.getPlatformNoticeId());
        if (platformNotice == null) {
            throw new ApplicationException(ResponseCode.INVALID_PARAM, "平台通知不存在");
        }

        if (platformNotice.getStatus().equals(PlatformNoticeStatusEnum.PUBLISH.getValue())) {
            throw new ApplicationException(ResponseCode.INVALID_PARAM, "平台通知已发布，不能重复发布");
        }

        List<String> alias = new ArrayList<>();
        for (Integer id : platformNoticeSendDto.getMemberIdList()) {
            alias.add(getAlias(id));
        }
        //发送
        Map<String, Object> extras = Maps.newHashMap();
        extras.put("noticeType", NoticeTypeEnum.PLATFORM.getValue());
        sendJpush(alias, platformNotice.getTitle(), platformNotice.getContent(), extras);
        platformNoticeMapper.updateStatus(platformNotice.getId(), PlatformNoticeStatusEnum.PUBLISH.getValue());
    }

    @Transactional
    @Override
    public PlatformNoticeDto readPlatformNotice(PlatformNoticeReadDto readDto) {
        PlatformNotice platformNotice = platformNoticeMapper.selectByPrimaryKey(readDto.getPlatformNoticeId());
        if (platformNotice == null) {
            throw new ApplicationException(ResponseCode.INVALID_PARAM, "平台通知不存在");
        }

        Example example = new Example(NoticeReaded.class);
        example.createCriteria().andEqualTo("platformNoticeId", readDto.getPlatformNoticeId()).andEqualTo("memberId", readDto.getMemberId());
        if (noticeReadedMapper.selectCountByExample(example) == 0) {
            NoticeReaded noticeReaded = MapperUtils.map(readDto, NoticeReaded.class);
            noticeReaded.setCreateTime(new Date());
            noticeReadedMapper.insert(noticeReaded);
        }

        return MapperUtils.map(platformNotice, PlatformNoticeDto.class);
    }

    @Override
    public PageResultDto<PlatformNoticeDto> queryPlatformNotice(PlatformNoticeQueryDto queryDto) {
        Page<PlatformNotice> result = PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize()).doSelectPage(() -> platformNoticeMapper.query(queryDto));
        return PageUtils.convert(result, PlatformNoticeDto.class);
    }

    @Override
    public PageResultDto<PlatformNoticeDto> queryMyPlatformNotice(MyPlatformNoticeQueryDto queryDto) {
        Page<PlatformNotice> result = PageHelper.startPage(queryDto.getPageNum(), queryDto.getPageSize()).doSelectPage(() -> platformNoticeMapper.queryMyPlatformNotice(queryDto.getMemberId(), PlatformNoticeStatusEnum.PUBLISH.getValue(), queryDto.getReaded()));
        return PageUtils.convert(result, PlatformNoticeDto.class);
    }

    @Override
    public void updateMemberNickname(Integer memberId, String newNickname) {
        memberNoticeMapper.updateMemberNickname(memberId, newNickname);
    }

    @Override
    public int countUnReadByMemberId(Integer memberId) {
        int unreadMemberNoticeCount = memberNoticeMapper.countUnreadByMemberId(memberId);
        int unreadPlatformNoticeCount = platformNoticeMapper.countUnreadByMemberId(memberId);
        return unreadMemberNoticeCount + unreadPlatformNoticeCount;
    }

    @Transactional
    @Override
    public void readPlatformNoticeAll(Integer memberId) {
        List<PlatformNotice> platformNoticeList = platformNoticeMapper.queryMyPlatformNotice(memberId, PlatformNoticeStatusEnum.PUBLISH.getValue(), false);
        for (PlatformNotice platformNotice : platformNoticeList) {
            NoticeReaded noticeReaded = new NoticeReaded();
            noticeReaded.setMemberId(memberId);
            noticeReaded.setPlatformNoticeId(platformNotice.getId());
            noticeReaded.setCreateTime(new Date());
            noticeReadedMapper.insert(noticeReaded);
        }
    }

    private String getAlias(Integer id) {
        //项目名称_环境_id
        return jPushService.getName() + "_" + SpringContextHolder.getCurrentEnv().name() + "_" + id;
    }

    private boolean sendJpush(String alias, String alert, String msgCotent, Map<String, Object> extras) {
        List<String> listAlias = new ArrayList<>(2);
        listAlias.add(alias);
        return sendJpush(listAlias, alert, msgCotent, extras);
    }

    private boolean sendJpush(List<String> alias, String alert, String msgCotent, Map<String, Object> extras) {
        PushObject pushObject = new PushObject();
        pushObject.setAlias(alias);
        pushObject.setAlert(alert);
        pushObject.setMsgContent(msgCotent);
        pushObject.setExtras(extras);
        return jPushService.sendPush(pushObject);
    }
}
