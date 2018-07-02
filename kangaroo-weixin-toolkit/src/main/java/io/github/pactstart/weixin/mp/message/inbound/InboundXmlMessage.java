package io.github.pactstart.weixin.mp.message.inbound;

import io.github.pactstart.weixin.common.exception.WeixinApiException;
import io.github.pactstart.weixin.mp.message.InboundMessage;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by Rex.Lei on 2017/7/28.
 */
public abstract class InboundXmlMessage implements InboundMessage {

    private static Logger logger = LoggerFactory.getLogger(InboundXmlMessage.class);

    @Override
    public void read(InputStream in) {
        SAXReader saxReader = new SAXReader();
        Document document = null;
        try {
            document = saxReader.read(in);
        } catch (DocumentException e) {
            throw new WeixinApiException("解析xml流数据失败", e);
        }
        this.read(document.getRootElement());
    }

    public abstract void read(Element element);

}
