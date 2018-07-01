package io.github.pactstart.system.facade.dto;

import com.google.common.collect.Maps;
import io.github.pactstart.biz.common.dto.OperateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
public class SmsSendParamDto extends OperateDto {

    @NotBlank(message = "手机号不能为空")
    @Length(min = 11, max = 11, message = "手机号长度必须为11位")
    private String phone;

    @NotBlank(message = "短信模板id不能为空")
    private String templateId;

    @NotNull(message = "短信签名不能不能为空")
    private String signName;

    private Map<String, String> params = Maps.newHashMap();
}
