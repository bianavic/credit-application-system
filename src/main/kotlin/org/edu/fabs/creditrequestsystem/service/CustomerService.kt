package org.edu.fabs.creditrequestsystem.service

import org.edu.fabs.creditrequestsystem.entity.Customer

interface CustomerService {

    fun save(customer: Customer): Customer

    fun findById(id: Long): Customer

    fun delete(id: Long)
}