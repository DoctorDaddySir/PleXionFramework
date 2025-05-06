package com.doctordaddysir.annotations.testClasses;

import com.doctordaddysir.annotations.Bean;
import com.doctordaddysir.annotations.Inject;
import com.doctordaddysir.annotations.Injectable;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Bean
@Slf4j
@ToString
public class ReflectionPracticeClass {
    private final int port;
    private final boolean enabled;

    @Injectable
    public ReflectionPracticeClass(@Inject int port, @Inject boolean enabled) {
        this.port = port;
        this.enabled = enabled;
    }

}
