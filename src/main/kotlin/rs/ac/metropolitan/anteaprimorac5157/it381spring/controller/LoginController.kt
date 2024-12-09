package rs.ac.metropolitan.anteaprimorac5157.it381spring.controller

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import rs.ac.metropolitan.anteaprimorac5157.it381spring.data.StudentDataStore

@Controller
class LoginController {

    @GetMapping("/login")
    fun login() = "login"

    @GetMapping("/forgot-password")
    fun forgotPassword() = "forgot-password"

    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestParam email: String,
        @RequestParam securityAnswer: String,
        model: Model
    ): String {
        val student = StudentDataStore.findByEmail(email)

        return if (student?.securityAnswer.equals(securityAnswer, ignoreCase = true)) {
            model.addAttribute("success", "A temporary password has been sent to your email. Please check your inbox.")
            "forgot-password"
        } else {
            model.addAttribute("error", "Incorrect answer")
            "forgot-password"
        }
    }
}