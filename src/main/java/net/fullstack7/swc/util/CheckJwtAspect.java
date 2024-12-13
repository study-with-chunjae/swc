package net.fullstack7.swc.util;

import groovy.util.logging.Log4j;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fullstack7.swc.service.MemberServiceIf;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class CheckJwtAspect {
    private final CookieUtil cookieUtil;
    @Around("@annotation(checkJwtToken)")
    public Object checkJwtToken(ProceedingJoinPoint joinPoint, CheckJwtToken checkJwtToken) throws Throwable {
        log.debug("Checking jwt token");
        Object[] args = joinPoint.getArgs();
        try{
            String accessToken = extractAccessToken(args);
            if(accessToken==null){
                log.debug("Access token is null");
                return redirectWithError("로그인 상태가 아닙니다.", checkJwtToken.redirectUri(),args);
            }
            log.debug("Access token is not null");
            return joinPoint.proceed();
        }catch(RuntimeException e){
            log.error("RuntimeException 발생", e);
            return redirectWithError("로그인 상태가 아닙니다.", checkJwtToken.redirectUri(),args);
        }catch(Exception e){
            log.error("Exception 발생", e);
            return redirectWithError(e.getMessage(), checkJwtToken.redirectUri(),args);
        }
    }
    private String extractAccessToken(Object[] args) throws UnsupportedEncodingException {
        for(Object arg : args){
            if(arg instanceof HttpServletRequest request){
                log.debug("Extracting access token from request");
                request.setCharacterEncoding(StandardCharsets.UTF_8.name());
                String accessToken = cookieUtil.getCookieValue(request,"accessToken");
                if(accessToken!=null && !accessToken.isEmpty()){
                    return accessToken;
                }
            }
        }
        log.debug("Access token is null");
        return null;
    }

    private Object redirectWithError(String message, String redirectUrl, Object[] args) {
        log.debug("redirectWithError");
        RedirectAttributes redirectAttributes = null;
        HttpServletRequest request = null;
        for (Object arg : args) {
            if (arg instanceof RedirectAttributes ra) {
                log.debug("redirectAttribute");
                redirectAttributes = ra;
            } else if (arg instanceof HttpServletRequest req) {
                log.debug("HttpRequest");
                request = req;
            }
        }
        if (redirectAttributes != null) {
            log.debug("return redirectAttribute");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:" + redirectUrl;
        } else if (request != null) {
            log.debug("return request");
            log.debug("return flashMap");
            var flashMap = org.springframework.web.servlet.support.RequestContextUtils.getOutputFlashMap(request);
            flashMap.put("error", message);
            return "redirect:" + redirectUrl;
        }
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        return "redirect:" + redirectUrl + "?errors=" + encodedMessage;
    }
}
