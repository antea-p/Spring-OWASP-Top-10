package rs.ac.metropolitan.anteaprimorac5157.it381spring.data

import rs.ac.metropolitan.anteaprimorac5157.it381spring.model.Student

object StudentDataStore {
    val students = listOf(
        Student(1, "Amanda Smith", 7.95, "Test comment 1"),
        Student(2, "Charlie Brown", 6.50, "Test comment 2"),
        Student(3, "Bob Johnson", 9.75, "Test comment 3"),
    )

    fun findById(id: Long): Student? = students.find { it.id == id }

    fun findStudentIdByEmail(email: String): Long? {
        return students.find { student ->
            getStudentEmail(student) == email
        }?.id
    }

    fun getStudentEmail(student: Student): String {
        val nameParts = student.name.lowercase().split(" ")
        return "${nameParts[0]}.${nameParts[1]}.${student.id}@university.edu"
    }

    fun isStudentEmail(email: String, studentId: Long): Boolean =
    findById(studentId)
        ?.let { getStudentEmail(it) == email }
        ?: false

}