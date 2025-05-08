package org.springframework.samples.petclinic.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "users/createUserForm";
    }

    @PostMapping("/register")
    public String processRegistration(@Valid User user, BindingResult result) {
        if (result.hasErrors()) {
            return "users/createUserForm";
        }
        
        userService.saveUser(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "users/login";
    }
}
