package org.survey.surveyservice.database.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.survey.surveyservice.models.SurveyData;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class SurveyDataConverter implements AttributeConverter<SurveyData> {
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public AttributeValue transformFrom(SurveyData surveyData) {
        try {
            return AttributeValue.builder().s(OBJECT_MAPPER.writeValueAsString(surveyData)).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while converting SurveyData to String.",e);
        }
    }

    @Override
    public SurveyData transformTo(AttributeValue attributeValue) {
        try {
            return OBJECT_MAPPER.readValue(attributeValue.s(), new TypeReference<SurveyData>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while converting String to SurveyData.",e);
        }
    }

    @Override
    public EnhancedType<SurveyData> type() {
        return EnhancedType.of(SurveyData.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
