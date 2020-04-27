package com.tool.reservation.service.ServiceImplementation

import com.tool.reservation.model.Reservation
import com.tool.reservation.model.User
import com.tool.reservation.repository.ReservationRepository
import com.tool.reservation.service.ReservationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.util.stream.Collectors

@Service
class ReservationServiceImpl : ReservationService {

    @Autowired
    lateinit var repository: ReservationRepository

    override fun findAll(): List<Reservation> {
        return repository.findAll()
    }

    override fun findUnoccupiedReservations(): List<Reservation> {
        return repository.findAll().stream()
                .filter { reservation -> reservation.user == null }.collect(Collectors.toList())
    }

    override fun findById(id: Int): Reservation {
        val optional = repository.findById(id)
        if (optional.isPresent) {
            return optional.get()
        } else throw NotFoundException("Not Found Record With id: $id")
    }

    override fun addReservation(title: String, description: String, start: Timestamp, end: Timestamp): Reservation {
        return repository.save(Reservation(title = title, description = description, start = start, end = end))
    }

    override fun deleteReservation(id: Int) {
        val reservation = findById(id)
        repository.deleteById(reservation.reservationId)
    }

    override fun deleteReservation(reservation: Reservation) {
        repository.delete(reservation)
    }

    override fun editReservation(reservation: Reservation): Reservation {
        return repository.save(reservation)
    }

    override fun addUserToNotReservedReservation(user: User, reservationId: Int): Reservation {
        val optionalReservation = repository.findById(reservationId)
        if (optionalReservation.isPresent) {
            val reservation = optionalReservation.get()
            if (reservation.user == null) {
                reservation.user = user
                repository.save(reservation)
                return reservation
            } else throw AlreadyBookedException("Reservation With id: $reservationId Is Already Booked")
        }
        throw NotFoundException("Not Found Record With id: $reservationId")
    }
}

class NotFoundException(message: String) : Exception(message)

class AlreadyBookedException(message: String) : Exception(message)
