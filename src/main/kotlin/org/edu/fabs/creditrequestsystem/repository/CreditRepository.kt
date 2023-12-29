package org.edu.fabs.creditrequestsystem.repository

import org.edu.fabs.creditrequestsystem.entity.Credit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CreditRepository : JpaRepository<Credit, Long> {
    fun findByCreditCode(creditCode: UUID): Credit?

    fun findAllByCustomerId(customerId: Long): List<Credit>

}