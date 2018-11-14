package io.github.pactstart.oss.autoconfigure;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Date;

public class OssClient {

    private static Logger logger = LoggerFactory.getLogger(OssClient.class);
    private OssConfig ossConfig;
    private String host;
    private OSSClient internalClient;

    public OssClient(OssConfig ossConfig) {
        this.ossConfig = ossConfig;
        this.host = "http://" + ossConfig.getBucket() + "." + ossConfig.getEndPoint();
        this.internalClient = new OSSClient(ossConfig.getEndPoint(), ossConfig.getAccessKeyId(), ossConfig.getAccessKeySecret());
    }

    /**
     * 上传的目录是由服务端指定的，这样的好处就是安全。 每个客户端只能上传到指定的目录，实现安全隔离
     *
     * @param dir 必须以/结尾
     * @return
     * @throws Exception
     */
    public PostPolicyResponse getPostPolicy(String dir) throws Exception {
        try {
            long expireEndTime = System.currentTimeMillis() + ossConfig.getExpireTime() * 1000;
            Date expiration = new Date(expireEndTime);
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = internalClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = internalClient.calculatePostSignature(postPolicy);

            PostPolicyResponse response = new PostPolicyResponse();
            response.setAccessid(ossConfig.getAccessKeyId());
            response.setPolicy(encodedPolicy);
            response.setSignature(postSignature);
            response.setDir(dir);
            response.setHost(host);
            response.setExpire(expireEndTime / 1000);

            return response;
        } catch (Exception e) {
            logger.error("生成postpolicy发生异常", e);
            throw e;
        }
    }

    /**
     * 上传文件，返回绝对路径
     *
     * @param key
     * @param inputStream
     * @return
     */
    public String uploadFile(String key, InputStream inputStream) {
        internalClient.putObject(ossConfig.getBucket(), key, inputStream);
        return this.host + "/" + key;
    }
}
