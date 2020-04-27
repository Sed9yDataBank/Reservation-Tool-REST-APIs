package com.tool.reservation.model

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
data class Role(
        var roleName: String = "") {

    @Id
    @GeneratedValue
    val id: Long = 0

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_roles",
            inverseJoinColumns = [JoinColumn(name = "user_id")],
            joinColumns = [JoinColumn(name = "roles_id")])
    val users: Set<User> = HashSet()
}
