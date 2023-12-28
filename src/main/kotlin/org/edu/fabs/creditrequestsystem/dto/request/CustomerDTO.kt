package org.edu.fabs.creditrequestsystem.dto.request

import org.edu.fabs.creditrequestsystem.entity.Address
import org.edu.fabs.creditrequestsystem.entity.Customer
import java.math.BigDecimal

data class CustomerDTO(
    val firstName: String,
    val lastName: String,
    val cpf: String,
    val email: String,
    val password: String,
    val income: BigDecimal,
    val zipCode: String,
    val street: String
) {
    // montar o Customer para salver no DB com as infos que chegaram no DTO
    fun toEntity(): Customer = Customer(
        firstName = this.firstName,
        lastName = this.lastName,
        cpf = this.cpf,
        email = this.email,
        password = this.password,
        income = this.income,
        address = Address(
            zipCode = this.zipCode,
            street = this.street
        )
    )
}
