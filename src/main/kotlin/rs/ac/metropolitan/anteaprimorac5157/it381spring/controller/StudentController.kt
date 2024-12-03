package rs.ac.metropolitan.anteaprimorac5157.it381spring.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import rs.ac.metropolitan.anteaprimorac5157.it381spring.data.StudentDataStore

@Controller
class StudentController {

    @GetMapping("/")
    fun home(): String = "home"

    @GetMapping("/students")
    @PreAuthorize("hasRole('TEACHER')")
    fun students(model: Model): String {
        model.addAttribute("students", StudentDataStore.students)
        return "students"
    }

    @GetMapping("/students/{id}")
    @PreAuthorize("hasRole('TEACHER') or (hasRole('STUDENT') and @studentDataStore.isStudentEmail(authentication.name, #id))")
    fun getStudent(@PathVariable id: Long, model: Model, @AuthenticationPrincipal user: UserDetails): String {
        val student = StudentDataStore.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")

        model.addAttribute("student", student)
        model.addAttribute("isTeacher", user.authorities.any { it.authority == "ROLE_TEACHER" })
        return "student-details"
    }

    @PostMapping("/students/{id}/grade")
    @PreAuthorize("hasRole('TEACHER')")
    fun updateGrade(@PathVariable id: Long, @RequestParam grade: Double) =
        StudentDataStore.findById(id)
            ?.also { it.grade = grade }
            .let { "redirect:/students/$id" }

    @PostMapping("/students/{id}/comment")
    @PreAuthorize("hasRole('TEACHER') or (hasRole('STUDENT') and @studentDataStore.isStudentEmail(authentication.name, #id))")
    fun updateComment(@PathVariable id: Long, @RequestParam comment: String) =
        StudentDataStore.findById(id)
            ?.also { it.comment = comment }
            .let { "redirect:/students/$id" }
}