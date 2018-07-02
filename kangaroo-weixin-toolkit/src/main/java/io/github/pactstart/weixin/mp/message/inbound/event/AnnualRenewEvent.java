package io.github.pactstart.weixin.mp.message.inbound.event;

import org.dom4j.Element;

/**
 * 年审通知
 * Created by Di.Lei on 2017/8/12.
 */
public class AnnualRenewEvent extends AbstractReceivedEvent {

    private Long expiredTime;

    @Override
    public void readMore(Element root) {
        this.expiredTime = Long.valueOf(root.elementText("ExpiredTime"));
    }

    public Long getExpiredTime() {
        return expiredTime;
    }
}
