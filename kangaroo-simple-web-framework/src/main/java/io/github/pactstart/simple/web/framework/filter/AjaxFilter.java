package io.github.pactstart.simple.web.framework.filter;

import com.ufang.common.errorcode.ResponseCode;
import com.ufang.web.framework.utils.ResponseUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class AjaxFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        boolean isAjaxRequest = request.getHeader("X-Requested-With") != null
                && "XMLHttpRequest".equals(request.getHeader("X-Requested-With").toString());
        if (!isAjaxRequest) {
            ResponseUtils.outputJson((HttpServletResponse) servletResponse, new ResponseCode(ResponseCode.LACK_NECESSARY_REQUEST_HEADER, "非ajax访问"));
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

}
