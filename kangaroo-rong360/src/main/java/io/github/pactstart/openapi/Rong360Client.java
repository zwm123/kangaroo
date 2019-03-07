package io.github.pactstart.openapi;

import io.github.pactstart.openapi.exception.Rong360ApiException;
import io.github.pactstart.openapi.request.BaseRequest;
import io.github.pactstart.openapi.response.BaseResponse;

public interface Rong360Client {

    <T extends BaseResponse> T execute(BaseRequest<T> baseRequest) throws Rong360ApiException;

}
