package rs.ac.metropolitan.anteaprimorac5157.it381spring.model

data class Student(
    val id: Long? = null,
    val name: String,
    val email: String,
    var grade: Double,
    var comment: String,
    val securityQuestion: String = "What is your favourite movie?",
    var securityAnswer: String = "WarGames"
)