package net.fullstack7.swc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
// 관리자(수진) 스케줄러추가
@EnableScheduling
// 관리자(수진) 스케줄러추가
public class SwcApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwcApplication.class, args);
    }

}
