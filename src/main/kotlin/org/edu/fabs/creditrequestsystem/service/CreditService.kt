package org.edu.fabs.creditrequestsystem.service

import org.edu.fabs.creditrequestsystem.entity.Credit
import java.util.UUID

interface CreditService {

    fun save(credit: Credit): Credit
    fun findByCreditCode(customerId: Long, creditCode: UUID): Credit
    fun findAllByCustomer(customerId: Long): List<Credit>

}