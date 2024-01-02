package org.edu.fabs.creditrequestsystem.repository

import org.assertj.core.api.Assertions
import org.edu.fabs.creditrequestsystem.entity.Credit
import org.edu.fabs.creditrequestsystem.entity.Customer
import org.edu.fabs.creditrequestsystem.stub.CreditStub
import org.edu.fabs.creditrequestsystem.stub.CustomerStub
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.util.*

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditRepositoryTest {

    @Autowired
    lateinit var creditRepository: CreditRepository
    @Autowired
    lateinit var testEntityManager: TestEntityManager

    private lateinit var customer: Customer
    private lateinit var credit1: Credit
    private lateinit var credit2: Credit

    // salvar customer e credit no banco de dados
    @BeforeEach
    fun setUp() {
        // to fix detached entity passed to persist testentitymanager i change persist to merge, cause It will merge the detached objects in the session
        customer = testEntityManager.merge(CustomerStub.buildCustomer())
        credit1 = testEntityManager.persist(CreditStub.buildCredit(customer = customer))
        credit2 = testEntityManager.persist(CreditStub.buildCredit(customer = customer))
    }

    @Test
    fun `should find credit by credit code`() {
        val creditCode1 = UUID.fromString("cea26327-01d0-466d-8b95-980ef51b6440")
        val creditCode2 = UUID.fromString("eeae530a-0784-4f8c-8a0e-bfe9057844ce")
        credit1.creditCode = creditCode1
        credit2.creditCode = creditCode2

        val fakeCredit1 = creditRepository.findByCreditCode(creditCode1)
        val fakeCredit2 = creditRepository.findByCreditCode(creditCode2)

        Assertions.assertThat(fakeCredit1).isNotNull
        Assertions.assertThat(fakeCredit2).isNotNull
        Assertions.assertThat(fakeCredit1).isSameAs(credit1)
        Assertions.assertThat(fakeCredit2).isSameAs(credit2)
    }

    @Test
    fun `should find all credits by customer id`() {
        val customerId: Long = 1L

        val creditList: List<Credit> = creditRepository.findAllByCustomerId(customerId)

        Assertions.assertThat(creditList).isNotNull
        Assertions.assertThat(creditList.size).isEqualTo(2)
        Assertions.assertThat(creditList).contains(credit1, credit2)
    }

}