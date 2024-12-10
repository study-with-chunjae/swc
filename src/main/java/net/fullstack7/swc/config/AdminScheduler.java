package net.fullstack7.swc.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.fullstack7.swc.repository.MemberRepository;
import net.fullstack7.swc.service.MemberServiceIf;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Log4j2
public class AdminScheduler {

    private final MemberServiceIf memberService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateTest() {
        int updatedCount = memberService.updateMemberStatus();
        log.info(updatedCount + " 명 회원 상태변경");
    }

//    @Scheduled(cron = "0 * * * * ?")
//    public void updateTest() {
//        log.info("회원상태확인"+LocalDateTime.now());
//        int updatedCount = memberService.updateMemberStatus();
//        log.info(updatedCount + " 명 회원 상태변경");
//    }

//    @Scheduled(fixedRate = 5000) // 5초마다 실행
//    public void testScheduler() {
//        log.info("스케줄러 실행됨: " + LocalDateTime.now());
//    }

}
