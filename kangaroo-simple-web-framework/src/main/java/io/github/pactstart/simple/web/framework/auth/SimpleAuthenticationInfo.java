package io.github.pactstart.simple.web.framework.auth;

public class SimpleAuthenticationInfo implements AuthenticationInfo {

    private Integer userId;

    private String userName;

    @Override
    public String getUserName() {
        return null;
    }

    @Override
    public Integer getUserId() {
        return null;
    }
}
