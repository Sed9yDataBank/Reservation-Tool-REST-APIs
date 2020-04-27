package com.tool.reservation.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
data class User(var firstName: String = "",
                var lastName: String = "",
                var username: String = "",
                var email: String = "",
                @JsonIgnore
                var password: String = "") {

    @Id
    @GeneratedValue
    var userId: Long = 0

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            inverseJoinColumns = [JoinColumn(name = "roles_id")],
            joinColumns = [JoinColumn(name = "user_id")])
    var roles: Set<Role> = HashSet()

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    var reservations: List<Reservation> = listOf()
}
