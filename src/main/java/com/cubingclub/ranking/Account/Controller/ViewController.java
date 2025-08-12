package com.cubingclub.ranking.Account.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    @GetMapping("/signup")
    public String joinPage(){ return "signup"; }
    @GetMapping("/password-reset")
    public String passwordPage(){ return "password"; }
}
