package org.survey.surveyservice.models;

import lombok.Data;

@Data
public class SurveyUser {
    private String name;
    private String birthYear;
    private String id;
    private String pincode;
}
