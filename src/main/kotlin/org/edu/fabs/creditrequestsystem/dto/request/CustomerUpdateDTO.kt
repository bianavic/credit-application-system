package org.edu.fabs.creditrequestsystem.dto.request

import jakarta.validation.constraints.*
import org.edu.fabs.creditrequestsystem.entity.Customer
import org.hibernate.validator.constraints.Length
import java.math.BigDecimal

data class CustomerUpdateDTO(
    @field:NotEmpty(message = "First name must not be empty") @field:Length(min = 3, max = 20, message = "First name should be within 3 to 20 characters") val firstName: String,
    @field:NotEmpty(message = "Last name must not be empty") @field:Length(min = 3, max = 50, message = "Last name should be within 3 to 50 characters") val lastName: String,
    @field:NotEmpty(message = "Zip code must not be empty") @field:Length(max = 50) val zipCode: String,
    @field:NotEmpty(message = "Street must not be empty") @field:Length(max = 255) val street: String,
    @field:NotNull(message = "Income can not be null") @field:DecimalMin(value = "1.00", message = "The income value can not be less than 1.00 digit")
    @field:DecimalMax(value = "99999.999", message = "The credit value can not be more than 99999.999") val income: BigDecimal
) {
    // recebe o customer q iremos atualizar, com ele atualizamos os dados recebidos no customerupdate
    fun toEntity(customer: Customer): Customer {
        customer.firstName = this.firstName
        customer.lastName = this.lastName
        customer.income = this.income
        customer.address.zipCode = this.zipCode
        customer.address.street = this.street
        return customer
    }
}
