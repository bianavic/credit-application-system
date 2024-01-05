package org.edu.fabs.creditrequestsystem.stub

import org.edu.fabs.creditrequestsystem.dto.request.CustomerUpdateDTO
import java.math.BigDecimal

class CustomerUpdateDTOStub {

    companion object {

        fun builderCustomerUpdateDTO(
            firstName: String = "NameUpdated",
            lastName: String = "SurnameUpdated",
            income: BigDecimal = BigDecimal.valueOf(1000.0),
            zipCode: String = "2222",
            street: String = "Street Updated"
        ) = CustomerUpdateDTO(
            firstName = firstName,
            lastName = lastName,
            income = income,
            zipCode = zipCode,
            street = street
        )
    }

}