package org.edu.fabs.creditrequestsystem.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.edu.fabs.creditrequestsystem.dto.request.CreditDTO
import org.edu.fabs.creditrequestsystem.entity.Credit
import org.edu.fabs.creditrequestsystem.entity.Customer
import org.edu.fabs.creditrequestsystem.repository.CreditRepository
import org.edu.fabs.creditrequestsystem.repository.CustomerRepository
import org.edu.fabs.creditrequestsystem.stub.CreditDTOStub
import org.edu.fabs.creditrequestsystem.stub.CreditStub
import org.edu.fabs.creditrequestsystem.stub.CustomerDTOStub
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDate
import java.util.*

@SpringBootTest
@ContextConfiguration
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class CreditControllerTest {

    @Autowired
    lateinit var creditRepository: CreditRepository

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeEach
    fun setUp() {
        customerRepository.deleteAll()
        creditRepository.deleteAll()
    }

    @AfterEach
    fun tearDown() {
        customerRepository.deleteAll()
        creditRepository.deleteAll()
    }

    @Test
    fun `should create a credit and return 201 status`() {
        val customer: Customer = customerRepository.save(CustomerDTOStub.builderCustomerDTO().toEntity())
        val fakeCreditDTO: CreditDTO? = customer.id?.let { CreditDTOStub.builderCreditDTO(customerId = it) }
        val valueAsString: String = objectMapper.writeValueAsString(fakeCreditDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(customer.id))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a credit if customer ID is not found then should throw BusinessException and return 400 status`() {
        val invalidId: Long = Random().nextLong()

        val fakeCreditDTO: CreditDTO = CreditDTOStub.builderCreditDTO(customerId = invalidId)
        val valueAsString: String = objectMapper.writeValueAsString(fakeCreditDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.edu.fabs.creditrequestsystem.exception.BusinessException")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.details['null']")
                    .value("Id $invalidId not found")
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a credit if the day of the first installment is in the past then should throw MethodArgumentNotValidException and return 400 status`() {
        val dateFirstInstallmentInThePast: LocalDate = LocalDate.now()

        val fakeCreditDTO: CreditDTO =
            CreditDTOStub.builderCreditDTO(dayFirstInstallment = dateFirstInstallmentInThePast)
        val valueAsString: String = objectMapper.writeValueAsString(fakeCreditDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a credit if the day of the first installment is more than 3 months from now then should throw BusinessException and return 400 status`() {
        val dateFirstInstallment3MonthsLater: LocalDate = LocalDate.now().plusMonths(4L)

        val fakeCreditDTO: CreditDTO =
            CreditDTOStub.builderCreditDTO(dayFirstInstallment = dateFirstInstallment3MonthsLater)
        val valueAsString: String = objectMapper.writeValueAsString(fakeCreditDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.edu.fabs.creditrequestsystem.exception.BusinessException")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.details['null']")
                    .value("Date can not be more than 3 months ahead")
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a credit if the number of installments is less than 0 then should throw BusinessException and return 400 status`() {
        val invalidNumberOfInstallments: Int = -1

        val fakeCreditDTO: CreditDTO =
            CreditDTOStub.builderCreditDTO(numberOfInstallments = invalidNumberOfInstallments)
        val valueAsString: String = objectMapper.writeValueAsString(fakeCreditDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a credit if the number of installments is more than 48 then should throw BusinessException and return 400 status`() {
        val invalidNumberOfInstallments: Int = 49

        val fakeCreditDTO: CreditDTO =
            CreditDTOStub.builderCreditDTO(numberOfInstallments = invalidNumberOfInstallments)
        val valueAsString: String = objectMapper.writeValueAsString(fakeCreditDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.web.bind.MethodArgumentNotValidException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find all credits for a customer by it id and then return 200 status`() {
        val customer: Customer = customerRepository.save(CustomerDTOStub.builderCustomerDTO().toEntity())

        val credit1: Credit = CreditStub.buildCredit(customer = customer)
        val credit2: Credit = CreditStub.buildCredit(customer = customer)
        creditRepository.saveAll(listOf(credit1, credit2))

        mockMvc.perform(
            MockMvcRequestBuilders.get("${URL}/all")
                .queryParam("customerId", customer.id.toString())
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].creditCode").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].creditValue").value("100.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].numberOfInstallments").value(15))

            .andExpect(MockMvcResultMatchers.jsonPath("$[1].creditCode").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].creditValue").value("100.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].numberOfInstallments").value(15))

            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find credit by its customer ID and Credit Code and then return 200 status`() {
        val customer: Customer = customerRepository.save(CustomerDTOStub.builderCustomerDTO().toEntity())
        val credit: Credit = CreditStub.buildCredit(customer = customer)
        creditRepository.save(credit)

        mockMvc.perform(
            MockMvcRequestBuilders.get("${URL}/${credit.creditCode}")
                .queryParam("customerId", customer.id.toString())
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditCode").value(credit.creditCode.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.creditValue").value(credit.creditValue))
            .andExpect(MockMvcResultMatchers.jsonPath("$.numberOfInstallments").value(credit.numberOfInstallments))
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(credit.status.toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.emailCustomer").value(customer.email))
            .andExpect(MockMvcResultMatchers.jsonPath("$.incomeCustomer").value(customer.income))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find credits if the customer ID is not found then should throw BusinessException and return 400 status`() {
        val invalidId: Long = Random().nextLong()

        val customer: Customer = customerRepository.save(CustomerDTOStub.builderCustomerDTO().toEntity())
        val credit: Credit = CreditStub.buildCredit(customer = customer)
        creditRepository.save(credit)

        mockMvc.perform(
            MockMvcRequestBuilders.get("${URL}/all")
                .queryParam("customerId", invalidId.toString())
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.edu.fabs.creditrequestsystem.exception.BusinessException")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.details['null']")
                    .value("Customer ID $invalidId not found")
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find a credit if credit code or customer ID is not found then should throw BusinessException and return 400 status`() {
        val invalidCreditCode: UUID = UUID.randomUUID()

        val customer: Customer = customerRepository.save(CustomerDTOStub.builderCustomerDTO().toEntity())
        val credit: Credit = CreditStub.buildCredit(customer = customer)
        creditRepository.save(credit)

        mockMvc.perform(
            MockMvcRequestBuilders.get("${URL}/{invalidCreditCode}", invalidCreditCode)
                .queryParam("customerId", customer.id.toString())
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.edu.fabs.creditrequestsystem.exception.BusinessException")
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.details['null']")
                    .value(
                        Matchers.containsString("Credit code ${invalidCreditCode} or customer ID ${customer.id} not found"))
            )
            .andDo(MockMvcResultHandlers.print())
    }

}