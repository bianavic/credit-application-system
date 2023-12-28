package org.edu.fabs.creditrequestsystem.dto.response

import org.edu.fabs.creditrequestsystem.entity.Customer
import java.math.BigDecimal

/**
 * DTO for {@link org.edu.fabs.creditrequestsystem.entity.Customer}
 */
data class CustomerView(
    val firstName: String,
    val lastName: String,
    val cpf: String,
    val email: String,
    val income: BigDecimal = BigDecimal.ZERO,
    val zipCode: String,
    val street: String
) {
    // montar customerview com as infos do customer
    constructor(customer: Customer): this (
        firstName = customer.firstName,
        lastName = customer.lastName,
        cpf = customer.cpf,
        email = customer.email,
        income = customer.income,
        zipCode = customer.address.zipCode,
        street = customer.address.street
    )
}