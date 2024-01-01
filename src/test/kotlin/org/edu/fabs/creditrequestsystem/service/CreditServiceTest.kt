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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
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

        val actual: Credit = this.creditServiceImpl.save(credit)

        verify(exactly = 1) { customerServiceImpl.findById(customerId) }
        verify(exactly = 1) { creditRepository.save(credit) }

        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(credit)
    }

    @Test
    fun `should not create credit when day first installment is invalid`() {
        val invalidDayFirstInstallment: LocalDate = LocalDate.now().plusMonths(5)
        val credit: Credit = buildCredit(dayFirstInstallment = invalidDayFirstInstallment)

        every { creditRepository.save(credit) } answers { credit }

        Assertions.assertThatThrownBy { creditServiceImpl.save(credit) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Invalid Date")

        verify(exactly = 0) { creditRepository.save(any()) }
    }

    @Test
    fun `should return credit for a valid customer and credit code`() {
        //given
        val customerId: Long = 1L
        val creditCode: UUID = UUID.randomUUID()
        val credit: Credit = buildCredit(customer = Customer(id = customerId))
        every { creditRepository.findByCreditCode(creditCode) } returns credit
        //when
        val actual: Credit = creditServiceImpl.findByCreditCode(customerId, creditCode)
        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(credit)
        verify(exactly = 1) { creditRepository.findByCreditCode(creditCode) }
    }

    @Test
    fun `should return list of credits for a customer`() {
        val customerId: Long = 1L
        val expectedCreditList: List<Credit> = listOf(buildCredit(), buildCredit(), buildCredit())
        every { creditRepository.findAllByCustomerId(customerId) } returns expectedCreditList

        val actual: List<Credit> = creditServiceImpl.findAllByCustomer(customerId)

        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isNotEmpty
        Assertions.assertThat(actual).isSameAs(expectedCreditList)

        verify(exactly = 1) { creditRepository.findAllByCustomerId(customerId) }
    }

    @Test
    fun `should throw BusinessException for different customer ID`() {
        val customerId: Long = 1L
        val creditCode: UUID = UUID.randomUUID()
        val credit: Credit = buildCredit(customer = Customer(id = 3L))
        every { creditRepository.findByCreditCode(creditCode) } returns credit

        Assertions.assertThatThrownBy { creditServiceImpl.findByCreditCode(customerId, creditCode) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Credit code $creditCode or customer ID $customerId not found")

        verify { creditRepository.findByCreditCode(creditCode) }
    }

    @Test
    fun `should throw BusinessException for invalid credit code`() {
        val customerId: Long = 1L
        val invalidCreditCode: UUID = UUID.randomUUID()
        every { creditRepository.findByCreditCode(invalidCreditCode) } returns null

        Assertions.assertThatThrownBy { creditServiceImpl.findByCreditCode(customerId, invalidCreditCode) }
            .isInstanceOf(BusinessException::class.java)
            .hasMessage("Credit code $invalidCreditCode or customer ID $customerId not found")

        verify(exactly = 1) { creditRepository.findByCreditCode(invalidCreditCode) }
    }

    companion object {
        private fun buildCredit(
            creditValue: BigDecimal = BigDecimal.valueOf(100.0),
            dayFirstInstallment: LocalDate = LocalDate.now().plusMonths(2L),
            numberOfInstallments: Int = 15,
            customer: Customer = CustomerServiceTest.buildCustomer()
        ): Credit = Credit(
            creditValue = creditValue,
            dayFirstInstallment = dayFirstInstallment,
            numberOfInstallments = numberOfInstallments,
            customer = customer
        )
    }

}