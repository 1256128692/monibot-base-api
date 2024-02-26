package cn.shmedo.monitor.monibotbaseapi.interceptor;

import jakarta.servlet.*;

import java.io.IOException;

//@Component
//@Order(1)
public class CustomCorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletResponse res = (HttpServletResponse) response;
//        res.setHeader("Access-Control-Allow-Origin", "*");
//        res.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS,DELETE,PUT");
//        res.setHeader("Access-Control-Max-Age", "3600");
//                res.setHeader("Access-Control-Allow-Headers", "x-requested-with,Authorization,Content-Type,access_token," +
//                "access_type,access_service,content-type,app_key,app_secret,x-md-currentuser,x-md-request-code,x-md-request-id," +
//                "Referer,x-md-set-real-ip,X-Forwarded-For,x-md-rnd-value");
//        chain.doFilter(request, response);
    }
}
