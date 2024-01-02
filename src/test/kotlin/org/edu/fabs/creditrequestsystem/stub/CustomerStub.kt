package org.edu.fabs.creditrequestsystem.stub

import org.edu.fabs.creditrequestsystem.entity.Address
import org.edu.fabs.creditrequestsystem.entity.Customer
import java.math.BigDecimal

open class CustomerStub {

    companion object {

        fun buildCustomer(
            firstName: String = "Name 1",
            lastName: String = "Surname 1",
            cpf: String = "28475934625",
            email: String = "name1@email.com",
            password: String = "1111",
            income: BigDecimal = BigDecimal.valueOf(1000.0),
            address: Address = Address(
                zipCode = "1111",
                street = "Street 1"
            ),
            id: Long = 1L
        ) = Customer(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            income = income,
            address = address,
            id = id
        )
    }
}