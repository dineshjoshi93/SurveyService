package org.survey.surveyservice.models;

import lombok.Data;

import java.util.List;

@Data
public class SurveySection {
    private String description;
    private String id;
    private List<SurveyQuestion>  surveyQuestions;
}
