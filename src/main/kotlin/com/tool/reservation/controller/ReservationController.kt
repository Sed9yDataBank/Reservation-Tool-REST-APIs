package com.tool.reservation.controller

import com.tool.reservation.model.Reservation
import com.tool.reservation.security.CustomUserDetailsService
import com.tool.reservation.service.ReservationService
import com.tool.reservation.service.ServiceImplementation.AlreadyBookedException
import com.tool.reservation.service.ServiceImplementation.NotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
@EnableGlobalMethodSecurity(prePostEnabled = true)
class ReservationController(@Autowired var service: ReservationService) {

    @Autowired
    lateinit var userDetailsService: CustomUserDetailsService

    @GetMapping("/roles")
    fun getUserRoles(): List<String> {
        return userDetailsService.currentUserRoles()
    }

    @PreAuthorize("@myUserDetailsService.currentUserRoles().contains('Role(roleName=ADMIN)') || @myUserDetailsService.currentUserRoles().contains('Role(roleName=USER)')")
    @GetMapping("/reservations")
    fun findAll(): List<Reservation> {
        val userRole = userDetailsService.currentUserRoles()
        return if (userRole.contains("Role(roleName=ADMIN)")) {
            service.findAll()
        } else {
            service.findUnoccupiedReservations()
        }
    }

    @PreAuthorize("@myUserDetailsService.currentUserRoles().contains('Role(roleName=ADMIN)')")
    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    fun addNewReservation(@RequestBody reservation: Reservation): Reservation {
        return service.addReservation(title = reservation.title, description = reservation.description,
                start = reservation.start, end = reservation.end)
    }

    @PreAuthorize("@myUserDetailsService.currentUserRoles().contains('Role(roleName=ADMIN)')")
    @DeleteMapping("/reservations/{id}")
    fun deleteReservation(@PathVariable(value = "id") id: Int): ResponseEntity<Void> {
        return try {
            service.deleteReservation(id)
            ResponseEntity(HttpStatus.NO_CONTENT)
        } catch (e: NotFoundException) {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PreAuthorize("@myUserDetailsService.currentUserRoles().contains('Role(roleName=ADMIN)') || @myUserDetailsService.currentUserRoles().contains('Role(roleName=USER)')")
    @PutMapping("/reservations")
    fun editReservation(@RequestBody reservation: Reservation): ResponseEntity<Reservation> {
        when {
            userDetailsService.currentUserRoles().contains("Role(roleName=ADMIN)")
            -> return ResponseEntity.ok(service.editReservation(reservation))
            userDetailsService.currentUserRoles().contains("Role(roleName=USER)")
            -> {
                if (SecurityContextHolder.getContext().authentication.name
                        == service.findById(reservation.reservationId)?.user?.username ?: false) {
                    //Already booked by this user
                    return ResponseEntity(HttpStatus.NO_CONTENT)
                } else return try {
                    ResponseEntity.ok(service.addUserToNotReservedReservation(userDetailsService.currentUser(),
                            reservation.reservationId))
                } catch (notFound: NotFoundException) {
                    ResponseEntity(HttpStatus.NOT_FOUND)
                } catch (alreadyBooked: AlreadyBookedException) {
                    ResponseEntity(HttpStatus.CONFLICT)
                }
            }
            else -> return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
