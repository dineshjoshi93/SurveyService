package org.survey.surveyservice;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.NonNull;
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
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.endpoints.internal.Value;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class SurveyServiceController {

    @Autowired
    private DynamoDbTemplate dynamoDbTemplate;

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
        surveyData.setSurveySections(List.of(
                buildSurveySectionGeneralHealthStatus(),
                buildSurveySectionLifestyleHabits(),
                buildSurveySectionMedicalHistory()));
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

    private SurveySection buildSurveySectionLifestyleHabits() {
        String sectionId = "s2";
        String description = "Lifestyle Habits";
        SurveyQuestion surveyQuestion = new SurveyQuestion();
        surveyQuestion.setId(sectionId + "-q1");
        surveyQuestion.setText("How many servings of fruits and vegetables do you eat daily?");

        SurveyQuestion surveyQuestion2 = new SurveyQuestion();
        surveyQuestion2.setId(sectionId + "-q2");
        surveyQuestion2.setText("Do you regularly engage in physical activity?");

        SurveyQuestion surveyQuestion3 = new SurveyQuestion();
        surveyQuestion3.setId(sectionId + "-q3");
        surveyQuestion3.setText("How many alcoholic drinks do you consume per week?");

        SurveyQuestion surveyQuestion4 = new SurveyQuestion();
        surveyQuestion4.setId(sectionId + "-q4");
        surveyQuestion4.setText("How many hours of sleep do you typically get per night?");

        SurveySection surveySection = new SurveySection();
        surveySection.setDescription(description);
        surveySection.setId(sectionId);
        surveySection.setSurveyQuestions(List.of(surveyQuestion, surveyQuestion2, surveyQuestion3, surveyQuestion4));
        return surveySection;
    }

    private SurveySection buildSurveySectionMedicalHistory() {
        String sectionId = "s3";
        String description = "Medical History";
        SurveyQuestion surveyQuestion = new SurveyQuestion();
        surveyQuestion.setId(sectionId + "-q1");
        surveyQuestion.setText("Do you have any current medical conditions?");

        SurveyQuestion surveyQuestion2 = new SurveyQuestion();
        surveyQuestion2.setId(sectionId + "-q2");
        surveyQuestion2.setText("Have you ever been diagnosed with [specific condition]?");

        SurveyQuestion surveyQuestion3 = new SurveyQuestion();
        surveyQuestion3.setId(sectionId + "-q3");
        surveyQuestion3.setText("Does anyone in your family have a history of [specific condition]?");

        SurveyQuestion surveyQuestion4 = new SurveyQuestion();
        surveyQuestion4.setId(sectionId + "-q4");
        surveyQuestion4.setText("Are you currently taking any medications?");

        SurveySection surveySection = new SurveySection();
        surveySection.setDescription(description);
        surveySection.setId(sectionId);
        surveySection.setSurveyQuestions(List.of(surveyQuestion, surveyQuestion2, surveyQuestion3, surveyQuestion4));
        return surveySection;
    }

    private SurveySection buildSurveySectionGeneralHealthStatus() {
        String sectionId = "s1";
        String description = "Health Status";
        SurveyQuestion surveyQuestion = new SurveyQuestion();
        surveyQuestion.setId(sectionId + "-q1");
        surveyQuestion.setText("How healthy do you consider yourself to be?");

        SurveyQuestion surveyQuestion2 = new SurveyQuestion();
        surveyQuestion2.setId(sectionId + "-q2");
        surveyQuestion2.setText("Have you experienced any major health issues in the past year?");

        SurveyQuestion surveyQuestion3 = new SurveyQuestion();
        surveyQuestion3.setId(sectionId + "-q3");
        surveyQuestion3.setText("How often do you visit a doctor for routine checkups?");

        SurveySection surveySection = new SurveySection();
        surveySection.setDescription(description);
        surveySection.setId(sectionId);
        surveySection.setSurveyQuestions(List.of(surveyQuestion, surveyQuestion2, surveyQuestion3));
        return surveySection;
    }
}
