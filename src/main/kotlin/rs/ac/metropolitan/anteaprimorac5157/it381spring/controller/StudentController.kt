package rs.ac.metropolitan.anteaprimorac5157.it381spring.controller

import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.server.ResponseStatusException
import rs.ac.metropolitan.anteaprimorac5157.it381spring.model.Student
import java.util.*

@Controller
class StudentController {
    val studentsList = listOf(
        Student(1, "Amanda Smith", 7.95, "Test comment 1"),
        Student(2, "Charlie Brown", 6.50, "Test comment 2"),
        Student(3, "Bob Johnson", 9.75, "Test comment 3"),
    )

    @GetMapping("")
    fun home() = "home"

    @GetMapping("/students")
    fun students(model: Model): String {
        model.addAttribute("students", studentsList)
        return "students"
    }


    @GetMapping("/students/{id}")
    fun getStudent(@PathVariable id: Long, model: Model): String {
        val student = studentsList.find { student -> student.id == id }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")
        model.addAttribute("student", student)
        return "student-details"
    }
}