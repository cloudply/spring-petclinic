package org.springframework.samples.petclinic.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/userLogin")
    public String login() {
        return "security/login";
    }
}
