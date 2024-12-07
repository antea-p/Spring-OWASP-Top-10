package rs.ac.metropolitan.anteaprimorac5157.it381spring.security

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SecurityLogger {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun logAuthenticationSuccess(username: String, request: HttpServletRequest) {
        logger.info("Successful login - User: $username, IP: ${request.remoteAddr}, Session: ${request.session.id}")
    }

    fun logAuthenticationFailure(username: String, request: HttpServletRequest, error: String?) {
        logger.warn("Failed login attempt - User: $username, IP: ${request.remoteAddr}, Error: $error")
    }

    fun logLogout(username: String, request: HttpServletRequest) {
        logger.info("User logged out - User: $username, IP: ${request.remoteAddr}, Session: ${request.session.id}")
    }

    fun logStudentListAccess(accessedBy: String) {
        logger.info("Student list accessed by: $accessedBy")
    }

    fun logStudentDataAccess(studentId: Long, accessedBy: String) {
        logger.info("Student data accessed - Student ID: $studentId, Accessed by: $accessedBy")
    }

    fun logGradeChange(studentId: Long, oldGrade: Double, newGrade: Double, modifiedBy: String) {
        logger.info("Grade modified - Student ID: $studentId, Old: $oldGrade, New: $newGrade, Modified by: $modifiedBy")
    }

    fun logCommentChange(studentId: Long, oldComment: String, newComment: String, modifiedBy: String) {
        logger.info("Comment modified - Student ID: $studentId, Old: '$oldComment', New: '$newComment', Modified by: $modifiedBy")
    }

    fun logAccessDenied(username: String, resource: String) {
        logger.warn("Access denied - User: $username, Resource: $resource")
    }

}