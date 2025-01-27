package org.survey.surveyservice.database;

import org.survey.surveyservice.database.converter.DateConverter;
import org.survey.surveyservice.database.converter.SurveyDataConverter;
import org.survey.surveyservice.models.SurveyData;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.util.Date;

@DynamoDbBean
public class SurveyDataTable {
    private String id;
    private SurveyData surveyData;
    private Date creationDate;
    private Date lastUpdatedDate;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbConvertedBy(SurveyDataConverter.class)
    public SurveyData getSurveyData() {
        return surveyData;
    }

    public void setSurveyData(SurveyData surveyData) {
        this.surveyData = surveyData;
    }

    @DynamoDbConvertedBy(DateConverter.class)
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @DynamoDbConvertedBy(DateConverter.class)
    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
