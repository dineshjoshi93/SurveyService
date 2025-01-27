package org.survey.surveyservice.database;

import org.survey.surveyservice.database.converter.DateConverter;
import org.survey.surveyservice.database.converter.SurveyUserConverter;
import org.survey.surveyservice.models.SurveyUser;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Date;

@DynamoDbBean
public class SurveyUserTable {
    private String id;
    private SurveyUser surveyUser;
    private Date creationDate;
    private Date lastUpdatedDate;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbConvertedBy(SurveyUserConverter.class)
    public SurveyUser getSurveyUser() {
        return surveyUser;
    }

    public void setSurveyUser(SurveyUser surveyUser) {
        this.surveyUser = surveyUser;
    }

    @DynamoDbConvertedBy(DateConverter.class)
    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    @DynamoDbConvertedBy(DateConverter.class)
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
