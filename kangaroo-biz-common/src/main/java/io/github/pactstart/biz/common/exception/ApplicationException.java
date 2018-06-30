package io.github.pactstart.biz.common.exception;

import io.github.pactstart.biz.common.errorcode.ResponseCode;

public class ApplicationException extends RuntimeException {

    private ResponseCode responseCode;

    public ApplicationException() {
        //默认系统错误
        responseCode = ResponseCode.SYSTEM_ERROR;
    }

    public ApplicationException(ResponseCode responseCode) {
        super(responseCode.getMsg());
        this.responseCode = responseCode;
    }

    /**
     * @param responseCode
     * @param message      覆盖错误码中的描述
     */
    public ApplicationException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = new ResponseCode(responseCode.getCode(), message);
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

}
