package org.survey.surveyservice.models;

import lombok.Data;

import java.util.List;

@Data
public class SurveyData {
    private String id;
    private String surveyUserId;

    private List<SurveySection> surveySections;
}
