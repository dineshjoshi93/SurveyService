package org.survey.surveyservice.database.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.survey.surveyservice.models.SurveyUser;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class SurveyUserConverter implements AttributeConverter<SurveyUser> {
    private static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public AttributeValue transformFrom(SurveyUser surveyUser) {
        try {
            return AttributeValue.builder().s(OBJECT_MAPPER.writeValueAsString(surveyUser)).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while convering SurveyUser to String" ,e);
        }
    }

    @Override
    public SurveyUser transformTo(AttributeValue attributeValue) {
        try {
            return OBJECT_MAPPER.readValue(attributeValue.s(), new TypeReference<SurveyUser>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while convering String to SurveyUser" ,e);
        }
    }

    @Override
    public EnhancedType<SurveyUser> type() {
        return EnhancedType.of(SurveyUser.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}
