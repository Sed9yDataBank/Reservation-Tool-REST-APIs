package com.tool.reservation.service

import com.tool.reservation.model.Reservation
import com.tool.reservation.model.User
import java.sql.Timestamp

interface ReservationService {

    fun findAll(): List<Reservation>

    fun findUnoccupiedReservations(): List<Reservation>

    fun findById(id: Int): Reservation?

    fun addReservation(title: String, description: String, start: Timestamp, end: Timestamp): Reservation

    fun deleteReservation(id: Int)

    fun deleteReservation(reservation: Reservation)

    fun editReservation(reservation: Reservation): Reservation

    fun addUserToNotReservedReservation(user: User, reservationId: Int): Reservation
}
