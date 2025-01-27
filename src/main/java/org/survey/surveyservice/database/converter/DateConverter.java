package org.survey.surveyservice.database.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Date;

public class DateConverter implements AttributeConverter<Date> {
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public AttributeValue transformFrom(Date date) {
        try {
            return AttributeValue.builder().s(OBJECT_MAPPER.writeValueAsString(date)).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error on converting date to string", e);
        }
    }

    @Override
    public Date transformTo(AttributeValue attributeValue) {
        try {
            return OBJECT_MAPPER.readValue(attributeValue.s(), new TypeReference<Date>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error on converting string to date", e);
        }
    }

    @Override
    public EnhancedType<Date> type() {
        return EnhancedType.of(Date.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
