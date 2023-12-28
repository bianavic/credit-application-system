package org.edu.fabs.creditrequestsystem.repository

import org.edu.fabs.creditrequestsystem.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository: JpaRepository<Customer, Long> {
}