package rs.ac.metropolitan.anteaprimorac5157.it381spring.controller
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.server.ResponseStatusException
import rs.ac.metropolitan.anteaprimorac5157.it381spring.data.StudentDataStore
import java.net.HttpURLConnection
import java.net.URL

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

        tryLoadUrlPreview(student.comment, model)

        model.addAttribute("student", student)
        model.addAttribute("isTeacher", user.authorities.any { it.authority == "ROLE_TEACHER" })
        return "student-details"
    }

    private fun tryLoadUrlPreview(comment: String, model: Model) {
        val urlRegex = Regex("""https?://[^\s<>"]+|www\.[^\s<>"]""")

        urlRegex.find(comment)?.value?.let { url ->
            try {
                println("Attempting to load URL: $url")

                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val response = connection.inputStream.bufferedReader().use { it.readText() }
                println("Content: $response")

                model.addAttribute("previewTitle", "Content from: $url")
                model.addAttribute("previewContent", response.take(4096))
            } catch (e: Exception) {
                println("Error loading URL: ${e.message}")
                model.addAttribute("previewError", "Error: ${e.message}")
            }
        }
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