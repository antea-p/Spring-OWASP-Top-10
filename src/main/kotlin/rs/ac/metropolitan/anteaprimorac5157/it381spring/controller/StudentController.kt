package rs.ac.metropolitan.anteaprimorac5157.it381spring.controller

import org.slf4j.LoggerFactory
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
import rs.ac.metropolitan.anteaprimorac5157.it381spring.security.SecurityLogger

@Controller
class StudentController(private val securityLogger: SecurityLogger) {

    @GetMapping("/")
    fun home(): String = "home"

    @GetMapping("/students")
    @PreAuthorize("hasRole('TEACHER')")
    fun students(model: Model, @AuthenticationPrincipal user: UserDetails): String {
        securityLogger.logStudentListAccess(user.username)
        model.addAttribute("students", StudentDataStore.students)
        return "students"
    }

    @GetMapping("/students/{id}")
    @PreAuthorize("hasRole('TEACHER') or (hasRole('STUDENT') and @studentDataStore.isStudentEmail(authentication.name, #id))")
    fun getStudent(@PathVariable id: Long, model: Model, @AuthenticationPrincipal user: UserDetails): String {
        try {
            val student = StudentDataStore.findById(id)
                ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")

            securityLogger.logStudentDataAccess(id, user.username)

            model.addAttribute("student", student)
            model.addAttribute("isTeacher", user.authorities.any { it.authority == "ROLE_TEACHER" })
            return "student-details"
        } catch (e: ResponseStatusException) {
            securityLogger.logAccessDenied(user.username, "/students/$id")
            throw e
        }
    }

    @PostMapping("/students/{id}/grade")
    @PreAuthorize("hasRole('TEACHER')")
    fun updateGrade(
        @PathVariable id: Long,
        @RequestParam grade: Double,
        @AuthenticationPrincipal user: UserDetails
    ) = StudentDataStore.findById(id)?.also { student ->
        val oldGrade = student.grade
        student.grade = grade
        securityLogger.logGradeChange(id, oldGrade, grade, user.username)
    }.let { "redirect:/students/$id" }

    @PostMapping("/students/{id}/comment")
    @PreAuthorize("hasRole('TEACHER') or (hasRole('STUDENT') and @studentDataStore.isStudentEmail(authentication.name, #id))")
    fun updateComment(
        @PathVariable id: Long,
        @RequestParam comment: String,
        @AuthenticationPrincipal user: UserDetails
    ) = StudentDataStore.findById(id)?.also { student ->
        val oldComment = student.comment
        student.comment = comment
        securityLogger.logCommentChange(id, oldComment, comment, user.username)
    }.let { "redirect:/students/$id" }
}