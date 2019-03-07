package io.github.pactstart.openapi.response;

import org.apache.http.HttpResponse;

public interface BaseResponse {

    void process(HttpResponse response) throws Exception;

}