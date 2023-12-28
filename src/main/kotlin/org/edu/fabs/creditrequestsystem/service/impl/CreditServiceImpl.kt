package org.edu.fabs.creditrequestsystem.service.impl

import org.edu.fabs.creditrequestsystem.entity.Credit
import org.edu.fabs.creditrequestsystem.repository.CreditRepository
import org.edu.fabs.creditrequestsystem.service.CreditService
import org.edu.fabs.creditrequestsystem.service.CustomerService
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreditServiceImpl(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
): CreditService {

    override fun save(credit: Credit): Credit {
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findByCreditCode(customerId: Long, creditCode: UUID): Credit {
        val credit = this.creditRepository.findByCreditCode(creditCode)
            ?: throw RuntimeException("Creditcode $creditCode not found")
        return if (credit.customer?.id == customerId) credit else throw RuntimeException("Contact admin")
    }

    override fun findAllByCustomer(customerId: Long): List<Credit> = this.creditRepository.findAllByCustomerId(customerId)

}