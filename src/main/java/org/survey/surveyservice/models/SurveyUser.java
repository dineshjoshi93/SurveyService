package org.survey.surveyservice.models;

import lombok.Data;

@Data
public class SurveyUser {
    private String id;
    private String name;
    private String fname;
    private String mname;
    private String lname;
    private String birthYear;
    private String pincode;
    private String area;
}
