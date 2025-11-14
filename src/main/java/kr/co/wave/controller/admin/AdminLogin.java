package kr.co.wave.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminLogin {
    @GetMapping("/login")
    public String login(){
        return "admin/login";
    }
}
