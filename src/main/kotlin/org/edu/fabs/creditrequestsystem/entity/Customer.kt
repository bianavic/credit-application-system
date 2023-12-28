package org.edu.fabs.creditrequestsystem.entity

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
data class Customer(
    @Column(nullable = false) var firstName: String = "",
    @Column(nullable = false) var lastName: String = "",
    @Column(nullable = false, unique = true) var cpf: String = "",
    @Column(nullable = false, unique = true) var email: String = "",
    @Column(nullable = false) var password: String = "",
    @Column(nullable = false) var income: BigDecimal = BigDecimal.ZERO,
    @Column(nullable = false) @Embedded var address: Address = Address(),
    // ao carregar o Customer nao carrega a lista de credito dele, carrega apenas se fizer esta solicitacao -> por isso primeiro nao aparece essa coluna no primeiro acesso ao h2-console
    @Column(nullable = false) @OneToMany(fetch = FetchType.LAZY,
        cascade = arrayOf(CascadeType.ALL, CascadeType.PERSIST),
        mappedBy = "customer") var credits: List<Credit> = mutableListOf(),
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null
)