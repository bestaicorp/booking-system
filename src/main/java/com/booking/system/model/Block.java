package com.booking.system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.NONE;

@Data
@NoArgsConstructor
@Entity
@Table(name = "blocks")
public class Block {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Setter(NONE)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private Property property;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(length = 255)
    private String reason;
}
