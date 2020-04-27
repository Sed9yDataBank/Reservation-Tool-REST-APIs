package com.tool.reservation.repository

import com.tool.reservation.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findOneByUsername(userName: String): User?
}