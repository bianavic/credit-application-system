package org.edu.fabs.creditrequestsystem.controller

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.transaction.Transactional
import org.edu.fabs.creditrequestsystem.dto.request.CustomerDTO
import org.edu.fabs.creditrequestsystem.dto.request.CustomerUpdateDTO
import org.edu.fabs.creditrequestsystem.entity.Customer
import org.edu.fabs.creditrequestsystem.repository.CustomerRepository
import org.edu.fabs.creditrequestsystem.stub.CustomerDTOStub
import org.edu.fabs.creditrequestsystem.stub.CustomerUpdateDTOStub
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@SpringBootTest // subir contexto spring
@ContextConfiguration // subir contexto spring
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Autowired
    lateinit var customerRepository: CustomerRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach
    fun setUp() = customerRepository.deleteAll()

    @AfterEach
    fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `should create a customer and return 201 status`() {
        val fakeCustomerDTO: CustomerDTO = CustomerDTOStub.builderCustomerDTO()
        val valueAsString: String = objectMapper.writeValueAsString(fakeCustomerDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Name 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Surname 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("05655091011"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("name2@email.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("2222"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Street 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with empty firstName then should return 400 status`() {
        val fakeCustomerDTO: CustomerDTO = CustomerDTOStub.builderCustomerDTO(firstName = "")
        val valueAsString: String = objectMapper.writeValueAsString(fakeCustomerDTO)

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
    fun `should not save a customer with same CPF then should return 409 status`() {
        customerRepository.save(CustomerDTOStub.builderCustomerDTO().toEntity())
        val fakeCustomerDTO: CustomerDTO = CustomerDTOStub.builderCustomerDTO()
        val valueAsString: String = objectMapper.writeValueAsString(fakeCustomerDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Conflict! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(409))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.springframework.dao.DataIntegrityViolationException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should find a customer by id and return 200 status`() {
        val customer: Customer = customerRepository.save(CustomerDTOStub.builderCustomerDTO().toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Name 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Surname 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("05655091011"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("name2@email.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("2222"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Street 2"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not find a customer with invalid id then should return 400 status`() {
        val invalidId: Long = 3L

        mockMvc.perform(
            MockMvcRequestBuilders.get("$URL/${invalidId}")
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
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should delete customer by id and return 204 status`() {
        val customer: Customer = customerRepository.save(CustomerDTOStub.builderCustomerDTO().toEntity())

        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${customer.id}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNoContent)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not delete customer by id then should return 400 status`() {
        val invalidId: Long = Random().nextLong()

        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${invalidId}")
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
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should throw BusinessException with status  when trying to delete by id that is not found`() {
        val nonExistentId: Long = 10L

        mockMvc.perform(
            MockMvcRequestBuilders.delete("$URL/${nonExistentId}")
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
                    .value("Id 10 not found")
            )
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should update a customer and return 200 status`() {
        val customer: Customer = customerRepository.save(CustomerDTOStub.builderCustomerDTO().toEntity())
        val customerUpdateDTO: CustomerUpdateDTO = CustomerUpdateDTOStub.builderCustomerUpdateDTO()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("NameUpdated"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("SurnameUpdated"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("05655091011"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("name2@email.com"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.income").value("1000.0"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.zipCode").value("2222"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.street").value("Street Updated"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not update a customer with invalid id and return 400 status`() {
        val invalidId: Long = 10L
        val customerUpdateDTO: CustomerUpdateDTO = CustomerUpdateDTOStub.builderCustomerUpdateDTO()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.patch("$URL?customerId=$invalidId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request! Consult the documentation"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(400))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.exception")
                    .value("class org.edu.fabs.creditrequestsystem.exception.BusinessException")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.details[*]").isNotEmpty)
            .andDo(MockMvcResultHandlers.print())
    }

}