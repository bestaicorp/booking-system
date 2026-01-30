package com.booking.system.service;

import com.booking.system.exception.InvalidDateRangeException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DateValidationService {

    private static final int MAX_FUTURE_YEARS = 2;

    public void validate(LocalDate startDate, LocalDate endDate) {
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
