package org.edu.fabs.creditrequestsystem.service.impl

import org.edu.fabs.creditrequestsystem.entity.Credit
import org.edu.fabs.creditrequestsystem.exception.BusinessException
import org.edu.fabs.creditrequestsystem.repository.CreditRepository
import org.edu.fabs.creditrequestsystem.service.CreditService
import org.edu.fabs.creditrequestsystem.service.CustomerService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*

@Service
class CreditServiceImpl(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
) : CreditService {

    override fun save(credit: Credit): Credit {
        this.validateDayFirstInstallment(credit.dayFirstInstallment)
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        return creditRepository.findByCreditCode(creditCode)?.takeIf {
            it.customer?.id == customerId
        } ?: throw BusinessException("Credit code $creditCode or customer ID $customerId not found")
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> {
        val credits = this.creditRepository.findAllByCustomerId(customerId)
        if (credits.isEmpty()) {
            throw BusinessException("Customer ID $customerId not found")
        }
        return credits
    }

    private fun validateDayFirstInstallment(dayFirstInstallment: LocalDate): Boolean {
        return if (dayFirstInstallment.isBefore(LocalDate.now().plusMonths(3))) true
        else throw BusinessException("Date can not be more than 3 months ahead")
    }

}