package org.edu.fabs.creditrequestsystem.entity

data class Customer(
    var firstName: String = "",
    var lastName: String = "",
    val cpf: String,
    var email: String = "",
    val password: String = "",
    val address: Address = Address(),
    val credits: List<Credit> = mutableListOf(),
    val id: Long? = null
)
