package com.mysite.sbb;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    // 특정 url로 요청을 보내면 Controller에서 어떠한 방식으로 처리할지 정의
    @RequestMapping("/")
    public String root() {
        return "redirect:/question/list";
    }
}
