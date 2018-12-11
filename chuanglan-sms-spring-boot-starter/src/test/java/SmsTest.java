import io.github.pactrex.chuanglan.autoconfiguration.ChuangLanConfig;
import io.github.pactrex.chuanglan.autoconfiguration.ChuangLanSmsClient;

public class SmsTest {

    public static void main(String[] args) {
        ChuangLanConfig chuangLanConfig = new ChuangLanConfig();
        chuangLanConfig.setAccount("");
        chuangLanConfig.setPassword("");
        chuangLanConfig.setUrl("");
        ChuangLanSmsClient chuangLanSmsClient = new ChuangLanSmsClient(chuangLanConfig);
        chuangLanSmsClient.sendSmsVariableRequest("【xxx】验证码{$var}，您正在登录{$var}，请不要把验证码透露给其他人！", "150xxxxxxxx,888888,yyy;");
    }
}
