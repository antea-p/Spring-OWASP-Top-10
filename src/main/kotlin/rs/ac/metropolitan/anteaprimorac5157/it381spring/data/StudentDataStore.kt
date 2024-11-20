package rs.ac.metropolitan.anteaprimorac5157.it381spring.data

import rs.ac.metropolitan.anteaprimorac5157.it381spring.model.Student

object StudentDataStore {
    val students = listOf(
        Student(1, "Amanda Smith", "amanda.smith.1@university.edu", 7.95, "Test comment 1"),
        Student(2, "Charlie Brown", "charlie.brown.2@university.edu", 6.50, "Test comment 2"),
        Student(3, "Bob Johnson", "bob.johnson.3@university.edu", 9.75, "Test comment 3"),
        Student(4, "David Lightman", "david.lightman.4@university.edu", 7.10, "A rebellious kid who's too smart for his own good"),
    )

    fun findById(id: Long): Student? = students.find { it.id == id }

    fun findStudentIdByEmail(email: String): Long? =
        students.find { it.email == email }?.id

    fun isStudentEmail(email: String, studentId: Long): Boolean =
        findById(studentId)?.email == email
}