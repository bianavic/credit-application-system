package org.edu.fabs.creditrequestsystem.stub

import org.edu.fabs.creditrequestsystem.dto.request.CustomerDTO
import java.math.BigDecimal

open class CustomerDTOStub {

    companion object {

        fun builderCustomerDTO(
            firstName: String = "Name 2",
            lastName: String = "Surname 2",
            cpf: String = "05655091011",
            email: String = "name2@email.com",
            password: String = "2222",
            income: BigDecimal = BigDecimal.valueOf(1000.0),
            zipCode: String = "2222",
            street: String = "Street 2"

        ) = CustomerDTO(
            firstName = firstName,
            lastName = lastName,
            cpf = cpf,
            email = email,
            password = password,
            income = income,
            zipCode = zipCode,
            street = street
        )
    }
}