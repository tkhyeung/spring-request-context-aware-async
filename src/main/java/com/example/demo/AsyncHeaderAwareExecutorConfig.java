package com.example.demo;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;
import java.util.concurrent.Executor;

@Configuration
public class AsyncHeaderAwareExecutorConfig extends AsyncConfigurerSupport {

    @Override
    @Bean("headerAwareExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor poolExecutor = new ThreadPoolTaskExecutor();
        poolExecutor.setTaskDecorator(new HeaderAwareDecorator());
        poolExecutor.setThreadNamePrefix("HeaderAwareExecutor-");
        poolExecutor.initialize();
        return poolExecutor;
    }

    public static class HeaderAwareHttpServletRequestWrapper extends HttpServletRequestWrapper {

        private final Map<String, String> headerMap = new HashMap<>();

        public HeaderAwareHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            initHeaderMap(request);
        }

        private void initHeaderMap(HttpServletRequest request) {
            Collections.list(request.getHeaderNames()).forEach(
                    name -> headerMap.put(name, request.getHeader(name))
            );
        }

        @Override
        public String getHeader(String name) {
            return headerMap.get(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            return Collections.enumeration(headerMap.keySet());
        }
    }

    public static class HeaderAwareDecorator implements TaskDecorator {

        @Override
        public Runnable decorate(Runnable runnable) {
            RequestAttributes context =
                    RequestContextHolder.currentRequestAttributes();
            Map<String, String> contextMap = MDC.getCopyOfContextMap();
            return () -> {
                try {
                    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new HeaderAwareHttpServletRequestWrapper(((ServletRequestAttributes) context).getRequest())));
                    if (Objects.nonNull(contextMap)) {
                        MDC.setContextMap(contextMap);
                    }
                    runnable.run();
                } finally {
                    MDC.clear();
                    RequestContextHolder.resetRequestAttributes();
                }
            };
        }
    }


}
