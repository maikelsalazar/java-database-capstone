package com.project.back_end.models;

import com.project.back_end.enums.AvailableTime;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AvailableTimeConverter implements AttributeConverter<AvailableTime, String> {

    @Override
    public String convertToDatabaseColumn(AvailableTime availableTime) {
        return availableTime.getValue();
    }

    @Override
    public AvailableTime convertToEntityAttribute(String fromDb) {
        String normalized = fromDb.trim();

        for (AvailableTime time: AvailableTime.values()) {
            if(time.getValue().equals(fromDb)) {
                return time;
            }
        }

        throw new IllegalArgumentException("Unknown available time: " +  fromDb);
    }
}
