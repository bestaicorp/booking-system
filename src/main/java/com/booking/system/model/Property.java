package com.booking.system.model;

import com.booking.system.enumeration.PropertyType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.NONE;

@Data
@NoArgsConstructor
@Entity
public class Property {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Setter(NONE)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Enumerated(STRING)
    @Column(nullable = false, length = 50)
    private PropertyType type;
}
