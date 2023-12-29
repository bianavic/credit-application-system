package org.edu.fabs.creditrequestsystem.dto.request

import jakarta.validation.constraints.*
import org.edu.fabs.creditrequestsystem.entity.Credit
import org.edu.fabs.creditrequestsystem.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDTO(
    @field:NotNull(message = "Customer ID is required")val customerId: Long,
    @field:Min(0, message = "Min number of installments is 0") @field:Max(48, message = "Max number of installments is 48") val numberOfInstallments: Int,
    @field:Future @field:NotNull(message = "Date is required") val dayFirstInstallment: LocalDate,
    @field:NotNull(message = "Credit value cannot be null") @field:DecimalMin(value = "1.00", message = "The credit value can not be less than 1.00 digit")
    @field:DecimalMax(value = "99999.999", message = "The credit value can not be more than 99999.999")
    val creditValue: BigDecimal,
) {
    fun toEntity(): Credit = Credit(
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstInstallment,
        numberOfInstallments = this.numberOfInstallments,
        customer = Customer(id = this.customerId)
    )
}
