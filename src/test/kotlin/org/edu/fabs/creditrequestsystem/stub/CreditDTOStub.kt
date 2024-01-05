package org.edu.fabs.creditrequestsystem.stub

import org.edu.fabs.creditrequestsystem.dto.request.CreditDTO
import java.math.BigDecimal
import java.time.LocalDate

open class CreditDTOStub {

    companion object {

        fun builderCreditDTO(
            numberOfInstallments: Int = 15,
            dayFirstInstallment: LocalDate = LocalDate.now().plusMonths(2L),
            creditValue: BigDecimal = BigDecimal.valueOf(100.0),
            customerId: Long = 1L
        ) = CreditDTO(
            numberOfInstallments = numberOfInstallments,
            dayFirstInstallment = dayFirstInstallment,
            creditValue = creditValue,
            customerId = customerId
        )

    }

}