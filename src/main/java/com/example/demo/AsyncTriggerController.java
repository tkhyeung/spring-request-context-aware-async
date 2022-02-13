package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AsyncTriggerController {

    private final AsyncService service;

    @PostMapping("/test/async")
    public String test(@RequestBody String body) {


//        service.normalAsync();
        service.headerAwareAsync();
        log.info("[{}] - response committing", Thread.currentThread().getName());
        return "SUCCESS";
    }
}
