package org.edu.fabs.creditrequestsystem.dto.request

import org.edu.fabs.creditrequestsystem.entity.Customer
import java.math.BigDecimal

data class CustomerUpdateDTO(
    val firstName: String,
    val lastName: String,
    val income: BigDecimal,
    val zipCode: String,
    val street: String
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
