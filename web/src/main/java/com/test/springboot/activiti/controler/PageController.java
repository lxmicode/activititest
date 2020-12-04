package com.test.springboot.activiti.controler;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("page")
public class PageController {

    /**
     * 部署一个流程
     * @return
     */
    @GetMapping("/")
    String page() {
        return "index";
    }
}
