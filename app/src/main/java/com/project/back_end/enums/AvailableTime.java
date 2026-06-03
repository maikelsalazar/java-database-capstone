package com.project.back_end.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.project.back_end.exceptions.UnavailableDoctorException;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

public enum AvailableTime {
    SLOT_08_09("08:00-09:00"),
    SLOT_09_10("09:00-10:00"),
    SLOT_10_11("10:00-11:00"),
    SLOT_11_12("11:00-12:00"),
    SLOT_12_13("12:00-13:00"),
    SLOT_13_14("13:00-14:00"),
    SLOT_14_15("14:00-15:00"),
    SLOT_15_16("15:00-16:00"),
    SLOT_16_17("16:00-17:00"),
    SLOT_17_18("17:00-18:00");

    private final String value;

    AvailableTime(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static AvailableTime fromStartTime(LocalDateTime startDatetime) {
        LocalTime start = startDatetime.toLocalTime();
        int hour = start.getHour();
        int nextHour = start.plusHours(1).getHour();

        String enumName = String.format("SLOT_%02d_%02d", hour, nextHour);

        try {
            return AvailableTime.valueOf(enumName);
        } catch (IllegalArgumentException ex) {
            throw new UnavailableDoctorException();
        }
    }

    public static Set<AvailableTime> amTimes() {
        return Set.of(
                SLOT_08_09,
                SLOT_09_10,
                SLOT_10_11,
                SLOT_11_12
        );
    }

    public static Set<AvailableTime> pmTimes() {
        return Set.of(
                SLOT_12_13,
                SLOT_13_14,
                SLOT_14_15,
                SLOT_15_16,
                SLOT_16_17,
                SLOT_17_18
        );
    }
}
