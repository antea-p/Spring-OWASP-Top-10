package rs.ac.metropolitan.anteaprimorac5157.it381spring.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController {

    @GetMapping("/login")
    fun login() = "login"
}