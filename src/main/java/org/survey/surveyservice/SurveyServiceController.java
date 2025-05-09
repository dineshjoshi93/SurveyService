package org.survey.surveyservice;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.survey.surveyservice.database.SurveyDataTable;
import org.survey.surveyservice.database.SurveyUserTable;
import org.survey.surveyservice.models.SurveyData;
import org.survey.surveyservice.models.SurveyQuestion;
import org.survey.surveyservice.models.SurveySection;
import org.survey.surveyservice.models.SurveyUser;
import org.survey.surveyservice.utils.ResourceLoaderHelper;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Date;
import java.util.UUID;

@Log4j2
@Controller
public class SurveyServiceController {

    @Autowired
    private DynamoDbTemplate dynamoDbTemplate;

    @Autowired
    private ResourceLoaderHelper resourceLoaderHelper;

    @GetMapping("/survey-user")
    public String getSurveyUserForm(Model model) {
        SurveyUser surveyUser = new SurveyUser();
        model.addAttribute("surveyUser", surveyUser);
        return "survey-user";
    }

    @PostMapping("/survey-user")
    public String addSurveyUser(@ModelAttribute SurveyUser surveyUser, Model model) {
        System.out.println("SURVEY USER " + surveyUser.toString());
        final String surveyUserId = generateSurveyUserId(surveyUser.getFname(), surveyUser.getLname(), surveyUser.getBirthYear(), surveyUser.getPincode());
        surveyUser.setId(surveyUserId);

        SurveyUserTable surveyUserTable = new SurveyUserTable();
        surveyUserTable.setId(surveyUser.getId());
        surveyUserTable.setSurveyUser(surveyUser);
        surveyUserTable.setCreationDate(new Date());
        surveyUserTable.setLastUpdatedDate(new Date());
        dynamoDbTemplate.save(surveyUserTable);
        return "redirect:/survey/" + surveyUserId;
    }

    @GetMapping("/survey/{surveyUserId}")
    public String getSurveyForm(@PathVariable String surveyUserId, Model model) {
        SurveyData surveyData = new SurveyData();
        surveyData.setId(UUID.randomUUID().toString());
        surveyData.setSurveyUserId(surveyUserId);
        surveyData.setSurveySections(resourceLoaderHelper.loadSurveyDataFromFile());
        model.addAttribute("surveyData", surveyData);
        return "survey";
    }

    @PostMapping("/survey/{surveyUserId}")
    public String addSurveyData(@PathVariable String surveyUserId, @ModelAttribute SurveyData surveyData, Model model) {
        SurveyDataTable surveyDataTable = new SurveyDataTable();
        surveyDataTable.setId(surveyData.getId());
        surveyDataTable.setSurveyData(surveyData);
        surveyDataTable.setCreationDate(new Date());
        surveyDataTable.setLastUpdatedDate(new Date());
        dynamoDbTemplate.save(surveyDataTable);
        return "redirect:/thankyou/"+surveyData.getId();
    }

    @GetMapping("/thankyou/{surveyDataId}")
    public String thankYou(@PathVariable String surveyDataId, Model model){

        SurveyDataTable surveyDataTable = dynamoDbTemplate.load(Key.builder().partitionValue(surveyDataId).build(), SurveyDataTable.class);
        String surveyUserId = surveyDataTable.getSurveyData().getSurveyUserId();
        SurveyUserTable surveyUserTable = dynamoDbTemplate.load(Key.builder().partitionValue(surveyUserId).build(), SurveyUserTable.class);
        String score = calculateScore(surveyDataTable.getSurveyData());
        model.addAttribute("surveyUser", surveyUserTable.getSurveyUser());
        model.addAttribute("surveyData", surveyDataTable.getSurveyData());
        model.addAttribute("score", score);
        return "thankyou";
    }

    private String calculateScore(SurveyData surveyData) {
        int sum = 0;
        int questions = 0;
        for(SurveySection ss: surveyData.getSurveySections()) {
            for (SurveyQuestion sq: ss.getSurveyQuestions()) {
                sum += mapAnswer(sq.getAnswer());
                questions++;
            }
        }
        return String.valueOf(sum/questions);
    }

    private int mapAnswer(String answer) {
        if ("Dissatisfied".equals(answer)) {
            return 10;
        } else if ("Neutral".equals(answer)) {
            return 20;
        } else if ("Satisfied".equals(answer)) {
            return 30;
        } else if ("VerySatisfied".equals(answer)) {
            return 60;
        }
        return 0;
    }

    private String generateSurveyUserId(@NonNull String fname, @NonNull String lname, @NonNull String birthYear, @NonNull String pincode) {
        StringBuilder stringBuilder = new StringBuilder();
        int hashCode = stringBuilder
                .append(fname.toLowerCase())
                .append(lname.toLowerCase())
                .append(birthYear.toLowerCase())
                .append(pincode.toLowerCase())
                .toString().hashCode();
        return String.valueOf(hashCode);
    }
}
