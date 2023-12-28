package org.edu.fabs.creditrequestsystem.repository

import org.edu.fabs.creditrequestsystem.entity.Credit
import org.springframework.data.jpa.repository.JpaRepository

interface CreditRepository: JpaRepository<Credit, Long> {
}