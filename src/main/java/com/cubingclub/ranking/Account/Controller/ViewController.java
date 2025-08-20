package com.cubingclub.ranking.Account.Controller;

import ch.qos.logback.core.model.Model;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

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
    @GetMapping("/oauth2/consent")
    public String consentPage(Principal principal, Model model, @RequestParam(OAuth2ParameterNames.CLIENT_ID) String clientId, @RequestParam(OAuth2ParameterNames.SCOPE) String scope, @RequestParam(OAuth2ParameterNames.STATE) String state) {

        return "consent";
    }
}
