package io.github.pactstart.weixin.mp.message.inbound.event;

import io.github.pactstart.weixin.mp.message.inbound.InboundXmlMessage;
import org.dom4j.Element;

public abstract class AbstractReceivedEvent extends InboundXmlMessage {

    private String toUserName;

    private String fromUserName;

    private long createTime;

    private String msgType;

    private String event;

    @Override
    public void read(Element root) {
        this.toUserName = root.elementText("ToUserName");
        this.fromUserName = root.elementText("FromUserName");
        this.createTime = Long.parseLong(root.elementText("CreateTime"));
        this.msgType = root.elementText("MsgType");
        this.event = root.elementText("Event");
        this.readMore(root);
    }

    public abstract void readMore(Element root);

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}
