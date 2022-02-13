package com.example.demo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.mockito.Mockito.*;


//Unit Test Demo with async executor
@ExtendWith(MockitoExtension.class)
public class AsyncServiceTest {

    @InjectMocks
    AsyncService service;
    @Spy
    ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
    private final int secondWaitToVerify = 5;

    @BeforeEach
    public void setUp() {
        threadPoolTaskExecutor.initialize();
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        threadPoolTaskExecutor.setAwaitTerminationSeconds(5);
    }

    @AfterEach
    public void cleanUp() {
        threadPoolTaskExecutor.shutdown();
    }

    @Test
    public void testShouldRunAsync() {
        service.normalAsync();
        verify(threadPoolTaskExecutor, timeout(secondWaitToVerify).times(1)).execute(any());
    }

    @Test
    public void testShouldDoNothingWithoutInteractingWithExecutor() {
        service.doNothing();
        verify(threadPoolTaskExecutor, times(0)).execute(any());
    }

}
