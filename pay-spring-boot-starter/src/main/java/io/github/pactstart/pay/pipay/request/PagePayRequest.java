package io.github.pactstart.pay.pipay.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagePayRequest {

    private String orderid;

    private String orderDesc;

    private String orderAmount;

    private String extParams;

}
