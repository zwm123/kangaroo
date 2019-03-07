package io.github.pactstart.openapi.request;

import io.github.pactstart.openapi.enums.Rong360OrderStatus;
import io.github.pactstart.openapi.response.ContractStatusFeedbackResponse;

import java.util.Date;

/**
 * 合同状态反馈请求
 */
public class ContractStatusFeedbackRequest extends BaseRequest<ContractStatusFeedbackResponse> {

    public ContractStatusFeedbackRequest(String order_no, Rong360OrderStatus order_status, Date updateT_time) {
        this.putBizData("order_no", order_no);
        this.putBizData("order_status", order_status.getValue());
        this.putBizData("updateT_time", updateT_time.getTime() / 1000);
    }

    @Override
    public String getMethod() {
        return "is.api.v3.order.contractstatus";
    }
}
