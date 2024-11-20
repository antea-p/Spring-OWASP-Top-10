package rs.ac.metropolitan.anteaprimorac5157.it381spring.controller

import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import rs.ac.metropolitan.anteaprimorac5157.it381spring.data.StudentDataStore
import rs.ac.metropolitan.anteaprimorac5157.it381spring.model.Student
import java.util.*

@Controller
class StudentController {

    @GetMapping("/", "/students")
    fun students(model: Model): String {
        model.addAttribute("students", StudentDataStore.students)
        return "students"
    }

    @GetMapping("/students/{id}")
    fun getStudent(@PathVariable id: Long, model: Model, @AuthenticationPrincipal user: UserDetails
    ): String {
        val student = StudentDataStore.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")

        if (!hasAccessToStudent(user, id)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied")
        }

        model.addAttribute("student", student)
        model.addAttribute("isTeacher", user.authorities.any { it.authority == "ROLE_TEACHER" })
        return "student-details"
    }

    private fun hasAccessToStudent(user: UserDetails, studentId: Long): Boolean {
        fun UserDetails.hasRole(role: String) =
            authorities.any { it.authority == "ROLE_$role" }

        return when {
            user.hasRole("TEACHER") -> true
            user.hasRole("STUDENT") -> StudentDataStore.isStudentEmail(user.username, studentId)
            else -> false
        }
    }

    @PostMapping("/students/{id}/grade")
    fun updateGrade(@PathVariable id: Long, @RequestParam grade: Double) =
        StudentDataStore.findById(id)
            ?.also { it.grade = grade }
            .let { "redirect:/students/$id" }

    @PostMapping("/students/{id}/comment")
    fun updateComment(@PathVariable id: Long, @RequestParam comment: String) =
        StudentDataStore.findById(id)
            ?.also { it.comment = comment }
            .let { "redirect:/students/$id" }
}