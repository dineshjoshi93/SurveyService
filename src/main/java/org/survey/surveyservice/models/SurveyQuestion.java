package org.survey.surveyservice.models;

import lombok.Data;

@Data
public class SurveyQuestion {
    private String id;
    private String text;
    private String answer;
    private InputDetail inputDetail;
}
