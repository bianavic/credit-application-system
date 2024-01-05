package org.edu.fabs.creditrequestsystem.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.edu.fabs.creditrequestsystem.dto.request.CreditDTO
import org.edu.fabs.creditrequestsystem.dto.response.CreditSaveView
import org.edu.fabs.creditrequestsystem.dto.response.CreditView
import org.edu.fabs.creditrequestsystem.dto.response.CreditViewList
import org.edu.fabs.creditrequestsystem.entity.Credit
import org.edu.fabs.creditrequestsystem.service.CreditService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/credits")
class CreditController(
    private val creditService: CreditService
) {

    @Operation(summary = "Add Credit", description = "Returns 201 if successful")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Successful Operation"),
            ApiResponse(responseCode = "400", description = "Bad Request if ID is not found"),
            ApiResponse(responseCode = "400", description = "Date must be in future"),
            ApiResponse(responseCode = "400", description = "Date can not be more than 3 months ahead")
        ]
    )
    @PostMapping
    fun saveCredit(@RequestBody @Valid creditDTO: CreditDTO): ResponseEntity<CreditSaveView> {
        val credit = this.creditService.save(creditDTO.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CreditSaveView(credit))
    }

    @Operation(summary = "Find all Credits from a Customer By its ID", description = "Returns 200 if successful")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful Operation"),
            ApiResponse(responseCode = "400", description = "Bad Request Customer ID not found")
        ]
    )
    @GetMapping("/all")
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): ResponseEntity<List<CreditViewList>> {
        val creditViewLists: List<CreditViewList> = this.creditService.findAllByCustomer(customerId).stream()
            .map { credit: Credit -> CreditViewList(credit) }
            .collect(Collectors.toList())
        return ResponseEntity.status(HttpStatus.OK).body(creditViewLists)
    }

    @Operation(summary = "Fetch a Customer credit By its credit code", description = "Returns 200 if successful")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Successful Operation"),
            ApiResponse(responseCode = "400", description = "Bad Request if credit code or customer ID is not found")
        ]
    )
    @GetMapping("/{creditCode}")
    fun findByCreditCode(
        @RequestParam(value = "customerId") customerId: Long,
        @PathVariable creditCode: UUID
    ): ResponseEntity<CreditView> {
        val credit = this.creditService.findByCreditCode(customerId, creditCode)
        return ResponseEntity.status(HttpStatus.OK).body(CreditView(credit))
    }

}