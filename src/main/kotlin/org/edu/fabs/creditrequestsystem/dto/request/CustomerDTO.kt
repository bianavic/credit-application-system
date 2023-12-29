package org.edu.fabs.creditrequestsystem.dto.request

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.edu.fabs.creditrequestsystem.entity.Address
import org.edu.fabs.creditrequestsystem.entity.Customer
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.br.CPF
import java.math.BigDecimal

data class CustomerDTO(
    @field:NotEmpty(message = "First name cannot be empty") @field:Length(min = 3, max = 20, message = "First name should be within 3 to 20 characters") val firstName: String,
    @field:NotEmpty(message = "Last name cannot be empty") @field:Length(min = 3, max = 50, message = "Last name should be within 3 to 50 characters") val lastName: String,
    @field:NotEmpty(message = "CPF cannot be empty") @field:CPF(message = "Invalid CPF format") @field:Length(max = 20) val cpf: String,
    @field:NotEmpty(message = "Email cannot be empty") @field:Email(message = "Invalid email format") @field:Length(max = 50) val email: String,
    @field:NotEmpty(message = "Password cannot be empty") @field:Length(min = 4, max = 20, message = "Password should be within 4 to 20 characters") val password: String,
    @field:NotEmpty(message = "Zip code cannot be empty") @field:Length(max = 50) val zipCode: String,
    @field:NotEmpty(message = "Street cannot be empty") @field:Length(max = 255) val street: String,
    @field:NotNull(message = "Income cannot be null") @field:DecimalMin(value = "1.00", message = "The decimal value can not be less than 1.00 digit ")
    @field:DecimalMax(value = "99999.999", message = "The decimal value can not be more than 99999.999") val income: BigDecimal
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
