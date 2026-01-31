package com.booking.system.model;

import com.booking.system.enumeration.BookingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Property property;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Guest guest;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(STRING)
    @Column(nullable = false, length = 50)
    private BookingStatus status = BOOKED;
}
