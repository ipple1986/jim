package cn.jim.filters;

import javax.servlet.*;
import java.io.IOException;

public class Filter2 implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("==Filter2==init()============");
    }

    // 不调用chain.doFilter(request,response);request将不经过servlet
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            System.out.println("====Filter2===doFilter()======classLoader="+this.getClass().getClassLoader().hashCode());
            chain.doFilter(request,response);
    }

    @Override
    public void destroy() {
        System.out.println("==Filter2==destroy()============");
    }
}
