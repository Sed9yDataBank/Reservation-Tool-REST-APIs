package com.tool.reservation.model

import com.fasterxml.jackson.annotation.JsonFormat
import java.sql.Timestamp
import javax.persistence.*

@Entity(name = "reservations")
data class Reservation(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val reservationId: Int = 0,
        val title: String = "",
        val description: String = "",
        @ManyToOne
        @JoinColumn(name = "userId")
        var user: User? = null,
        @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        var start: Timestamp = Timestamp(0),
        @get:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        var end: Timestamp = Timestamp(0)
)
