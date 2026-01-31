package com.booking.system.model;

import com.booking.system.enumeration.BookingStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import static com.booking.system.enumeration.BookingStatus.BOOKED;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.NONE;

@Data
@NoArgsConstructor
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Setter(NONE)
    private Long id;

    @ManyToOne
    private Property property;

    @ManyToOne
    private Guest guest;

    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(STRING)
    private BookingStatus status = BOOKED;
}
