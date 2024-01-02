package org.edu.fabs.creditrequestsystem.service

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions
import org.edu.fabs.creditrequestsystem.entity.Credit
import org.edu.fabs.creditrequestsystem.entity.Customer
import org.edu.fabs.creditrequestsystem.exception.BusinessException
import org.edu.fabs.creditrequestsystem.repository.CreditRepository
import org.edu.fabs.creditrequestsystem.service.impl.CreditServiceImpl
import org.edu.fabs.creditrequestsystem.service.impl.CustomerServiceImpl
import org.edu.fabs.creditrequestsystem.stub.CreditStub
import org.edu.fabs.creditrequestsystem.stub.CreditStub.Companion.buildCredit
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.*

@ExtendWith(MockKExtension::class)
class CreditServiceTest {

    @MockK
    lateinit var creditRepository: CreditRepository

    @MockK
    lateinit var customerServiceImpl: CustomerServiceImpl

    @InjectMockKs
    lateinit var creditServiceImpl: CreditServiceImpl

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should create credit`() {
        val credit: Credit = buildCredit()
        val customerId: Long = 1L

        every { customerServiceImpl.findById(customerId) } returns credit.customer!!
        every { creditRepository.save(credit) } returns credit

        val savedCredit: Credit = this.creditServiceImpl.save(credit)

        verify(exactly = 1) { customerServiceImpl.findById(customerId) }
        verify(exactly = 1) { creditRepository.save(credit) }

        Assertions.assertThat(savedCredit).isNotNull
        Assertions.assertThat(savedCredit).isSameAs(credit)
    }

    @Test
    fun `should not create credit when day first installment is invalid`() {
        val invalidDayFirstInstallment: LocalDate = LocalDate.now().plusMonths(5)
        val credit: Credit = CreditStub.buildCredit(dayFirstInstallment = invalidDayFirstInstallment)

        every { creditRepository.save(credit) } answers { credit }

        Assertions.assertThatThrownBy { creditServiceImpl.save(credit) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Invalid Date")

        verify(exactly = 0) { creditRepository.save(any()) }
    }

    @Nested
    inner class FindCreditTests {

        @Test
        fun `should return credit for a valid customer and credit code`() {
            // Arrange
            val customerId: Long = 1L
            val creditCode: UUID = UUID.randomUUID()
            val credit: Credit = CreditStub.buildCredit(customer = Customer(id = customerId))
            every { creditRepository.findByCreditCode(creditCode) } returns credit

            // Act
            val result: Credit = creditServiceImpl.findByCreditCode(customerId, creditCode)

            // Assert
            Assertions.assertThat(result).isNotNull
            Assertions.assertThat(result).isSameAs(credit)
            verify(exactly = 1) { creditRepository.findByCreditCode(creditCode) }
        }

        @Test
        fun `should throw BusinessException for different customer ID`() {
            // Arrange
            val customerId: Long = 1L
            val creditCode: UUID = UUID.randomUUID()
            val credit: Credit = buildCredit(customer = Customer(id = 3L))
            every { creditRepository.findByCreditCode(creditCode) } returns credit

            // Act & Assert
            Assertions.assertThatThrownBy {
                creditServiceImpl.findByCreditCode(customerId, creditCode)
            }.isInstanceOf(BusinessException::class.java)
                .hasMessage("Credit code $creditCode or customer ID $customerId not found")

            verify { creditRepository.findByCreditCode(creditCode) }
        }

        @Test
        fun `should throw BusinessException for invalid credit code`() {
            // Arrange
            val customerId: Long = 1L
            val invalidCreditCode: UUID = UUID.randomUUID()
            every { creditRepository.findByCreditCode(invalidCreditCode) } returns null

            // Act & Assert
            Assertions.assertThatThrownBy {
                creditServiceImpl.findByCreditCode(customerId, invalidCreditCode)
            }.isInstanceOf(BusinessException::class.java)
                .hasMessage("Credit code $invalidCreditCode or customer ID $customerId not found")

            verify(exactly = 1) { creditRepository.findByCreditCode(invalidCreditCode) }
        }

        @Test
        fun `should return list of credits for a customer`() {
            // Arrange
            val customerId: Long = 1L
            val expectedCreditList: List<Credit> = listOf(buildCredit(), buildCredit(), buildCredit())
            every { creditRepository.findAllByCustomerId(customerId) } returns expectedCreditList

            // Act
            val result: List<Credit> = creditServiceImpl.findAllByCustomer(customerId)

            // Assert
            Assertions.assertThat(result).isNotNull
            Assertions.assertThat(result).isNotEmpty
            Assertions.assertThat(result).isSameAs(expectedCreditList)

            verify(exactly = 1) { creditRepository.findAllByCustomerId(customerId) }
        }
    }

}