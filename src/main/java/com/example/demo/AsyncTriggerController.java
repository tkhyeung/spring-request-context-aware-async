package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AsyncTriggerController {

    private final Executor headerAwareExecutor;
    private final Executor normalContextAwareExecutor;

    @PostMapping("/test/async")
    public String test(@RequestBody String body) {

        CompletableFuture.runAsync(this::async, headerAwareExecutor);
//        CompletableFuture.runAsync(this::async, normalContextAwareExecutor);

        log.info("[{}] - response committing", Thread.currentThread().getName());
        return "SUCCESS";
    }

    private void async() {
        log.info("[{}] - Entering async method", Thread.currentThread().getName());
        printAllHeaders();
        sleepInSecond(2);
        printAllHeaders();
        sleepInSecond(2);
        printAllHeaders();
    }

    private void printAllHeaders(){
        try{
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if(request == null) return;
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
