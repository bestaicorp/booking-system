package com.booking.system.service;

import com.booking.system.exception.InvalidDateRangeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class DateValidationService {

    private static final int MAX_FUTURE_YEARS = 2;

    /** Validates that start is before end, dates are not in the past, and end is within 2 years. */
    public void validate(LocalDate startDate, LocalDate endDate) {
        log.debug("Validating date range: {} - {}", startDate, endDate);
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException("Start date must be before end date");
        }

        if (startDate.isEqual(endDate)) {
            throw new InvalidDateRangeException("Start date and end date cannot be the same");
        }

        if (startDate.isBefore(LocalDate.now())) {
            throw new InvalidDateRangeException("Start date cannot be in the past");
        }

        LocalDate maxDate = LocalDate.now().plusYears(MAX_FUTURE_YEARS);
        if (endDate.isAfter(maxDate)) {
            throw new InvalidDateRangeException("End date cannot be more than " + MAX_FUTURE_YEARS + " years in the future");
        }
    }
}
