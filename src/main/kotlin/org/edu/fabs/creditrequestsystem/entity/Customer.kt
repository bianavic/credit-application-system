package org.edu.fabs.creditrequestsystem.entity

import jakarta.persistence.*
import org.springframework.data.annotation.Id

@Entity
@Table(name = "Client")
data class Customer(
    @Column(nullable = false) var firstName: String = "",
    @Column(nullable = false) var lastName: String = "",
    @Column(nullable = false, unique = true) val cpf: String,
    @Column(nullable = false, unique = true) var email: String = "",
    @Column(nullable = false) var password: String = "",
    @Column(nullable = false) @Embedded var address: Address = Address(),
    // ao carregar o Customer nao carrega a lista de rcedito dele, carrega apenas se fizer esta solicitacao
    @Column(nullable = false) @OneToMany(fetch = FetchType.LAZY,
        cascade = arrayOf(CascadeType.ALL, CascadeType.PERSIST),
        mappedBy = "customer") var credits: List<Credit> = mutableListOf(),
    @jakarta.persistence.Id @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null
)
