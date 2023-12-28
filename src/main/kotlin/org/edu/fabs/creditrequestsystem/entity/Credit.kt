package org.edu.fabs.creditrequestsystem.entity

import jakarta.persistence.*
import org.edu.fabs.creditrequestsystem.ennumeration.Status
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@Entity
data class Credit(
    @Column(
        nullable = false,
        unique = true
    ) var creditCode: UUID = UUID.randomUUID(), // gerado automaticamente (e randomicamente) assim q instanciar este objeto
    @Column(nullable = false) val creditValue: BigDecimal = BigDecimal.ZERO,
    @Column(nullable = false) val dayFirstInstallment: LocalDate,
    @Column(nullable = false) val numberOfInstallments: Int = 0,
    // boas praticas banco de dados: deixar enum como int
    // alterei para string apenas para estudo
    @Enumerated(value = EnumType.STRING) val status: Status = Status.IN_PROGRESS,
    @ManyToOne var customer: Customer? = null,
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
)