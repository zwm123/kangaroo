package io.github.pactstart.openapi.request;

import io.github.pactstart.openapi.response.OrderStatusFeedbackResponse;

/**
 * 订单状态反馈请求
 */
public class OrderStatusFeedbackRequest extends BaseRequest<OrderStatusFeedbackResponse> {

    private OrderStatusFeedbackRequest() {

    }

    /**
     * 合同签订成功
     *
     * @param order_no 订单编号
     * @return ContractStatusFeedbackRequest实例
     */
    public static OrderStatusFeedbackRequest success(String order_no) {
        OrderStatusFeedbackRequest request = new OrderStatusFeedbackRequest();
        request.putBizData("order_no", order_no);
        request.putBizData("contract_status", 1);
        return request;
    }

    @Override
    public String getMethod() {
        return "is.api.v3.order.contractstatus";
    }
}
