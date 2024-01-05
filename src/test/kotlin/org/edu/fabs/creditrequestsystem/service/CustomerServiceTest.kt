package org.edu.fabs.creditrequestsystem.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions
import org.edu.fabs.creditrequestsystem.entity.Customer
import org.edu.fabs.creditrequestsystem.exception.BusinessException
import org.edu.fabs.creditrequestsystem.repository.CustomerRepository
import org.edu.fabs.creditrequestsystem.service.impl.CustomerServiceImpl
import org.edu.fabs.creditrequestsystem.stub.CustomerStub
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class CustomerServiceTest {

    @MockK
    lateinit var customerRepository: CustomerRepository

    @InjectMockKs
    lateinit var customerServiceImpl: CustomerServiceImpl

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Nested
    inner class SaveCustomerTests {

        @Test
        fun `should create customer`() {
            // Given
            val fakeCustomer: Customer = CustomerStub.buildCustomer()
            every { customerRepository.save(any()) } returns fakeCustomer

            // When
            val savedCustomer: Customer = customerServiceImpl.save(fakeCustomer)

            // Then
            Assertions.assertThat(savedCustomer).isNotNull
            Assertions.assertThat(savedCustomer).isSameAs(fakeCustomer)
            verify(exactly = 1) { customerRepository.save(fakeCustomer) }
        }
    }

    @Nested
    inner class FindCustomerTests {

        @Test
        fun `should find customer by id`() {
            // Given
            val fakeId: Long = Random().nextLong()
            val fakeCustomer: Customer = CustomerStub.buildCustomer(id = fakeId)
            every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)

            // When
            val foundCustomer: Customer = customerServiceImpl.findById(fakeId)

            // Then
            Assertions.assertThat(foundCustomer).isNotNull
            Assertions.assertThat(foundCustomer).isExactlyInstanceOf(Customer::class.java)
            Assertions.assertThat(foundCustomer).isSameAs(fakeCustomer)
            verify(exactly = 1) { customerRepository.findById(fakeId) }
        }

        @Test
        fun `should not find customer by invalid id and throw BusinessException`() {
            // Given
            val fakeId: Long = Random().nextLong()
            every { customerRepository.findById(fakeId) } returns Optional.empty()

            // When & Then
            Assertions.assertThatExceptionOfType(BusinessException::class.java)
                .isThrownBy { customerServiceImpl.findById(fakeId) }
                .withMessage("Id $fakeId not found")
            verify(exactly = 1) { customerRepository.findById(fakeId) }
        }
    }

    @Nested
    inner class DeleteCustomerTests {

        @Test
        fun `should delete customer by id`() {
            // Given
            val fakeId: Long = Random().nextLong()
            val fakeCustomer: Customer = CustomerStub.buildCustomer(id = fakeId)
            every { customerRepository.findById(fakeId) } returns Optional.of(fakeCustomer)
            every { customerRepository.delete(fakeCustomer) } just runs

            // When
            customerServiceImpl.delete(fakeId)

            // Then
            verify(exactly = 1) { customerRepository.findById(fakeId) }
            verify(exactly = 1) { customerRepository.delete(fakeCustomer) }
        }
    }

}