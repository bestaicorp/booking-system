package com.booking.system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.NONE;

@Data
@NoArgsConstructor
@Entity
public class Guest {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Setter(NONE)
    private Long id;

    private String name;

    private String email;
}
