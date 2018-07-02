package io.github.pactstart.weixin.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Di.Lei on 2017/8/8.
 */
public class StringUtils {

    public static String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
