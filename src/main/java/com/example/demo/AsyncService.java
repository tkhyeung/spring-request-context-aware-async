package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncService {

    private final Executor headerAwareExecutor;
    private final Executor normalContextAwareExecutor;

    void doNothing() {

    }

    void normalAsync() {
        CompletableFuture.runAsync(this::async, normalContextAwareExecutor);
    }

    void headerAwareAsync() {
        CompletableFuture.runAsync(this::async, headerAwareExecutor);
    }

    private void async() {
        log.info("[{}] - Entering async method", Thread.currentThread().getName());
        printAllHeaders();
        sleepInSecond(2);
        printAllHeaders();
        sleepInSecond(2);
        printAllHeaders();
    }

    private void printAllHeaders() {
        try {
            ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
            if (servletRequestAttributes == null) return;
            HttpServletRequest request = servletRequestAttributes.getRequest();
            if (request == null) return;
            List<String> list = Collections.list(request.getHeaderNames())
                    .stream().map(name -> name + ":" + getHeader(name))
                    .sorted()
                    .collect(Collectors.toList());
            log.info("[{}] - headers list - {}", Thread.currentThread().getName(), list);
        } catch (Exception e) {
            log.error("Error when printing all headers ", e);
        }
    }

    private void sleepInSecond(int second) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private String getHeader(String name) {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader(name);
    }


}
