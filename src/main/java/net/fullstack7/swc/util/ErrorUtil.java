package net.fullstack7.swc.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Component
@Log4j2
public class ErrorUtil {
    public String redirectWithError(String message, String redirectUri, RedirectAttributes redirectAttributes) {
        LogUtil.logLine("redirectWithError");
        redirectAttributes.addFlashAttribute("errors", message);
        LogUtil.log("errors",message);
        return "redirect:"+redirectUri;
    }

    public String redirectWithError(String redirectUri, RedirectAttributes redirectAttributes, BindingResult bindingResult) {
        LogUtil.logLine("redirectWithError");
        StringBuilder errors = new StringBuilder();
        bindingResult.getAllErrors().forEach(err->{errors.append(err.getDefaultMessage()).append("\n");});
        redirectAttributes.addFlashAttribute("errors", errors.toString());
        LogUtil.log("errors",errors.toString());
        return "redirect:"+redirectUri;
    }
}
