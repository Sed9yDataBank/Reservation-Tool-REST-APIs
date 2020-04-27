package com.tool.reservation.repository

import com.tool.reservation.model.Reservation
import org.springframework.data.jpa.repository.JpaRepository

interface ReservationRepository: JpaRepository<Reservation, Int>