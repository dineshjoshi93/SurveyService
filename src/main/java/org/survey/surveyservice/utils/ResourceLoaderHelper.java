package org.survey.surveyservice.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.survey.surveyservice.models.SurveySection;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class ResourceLoaderHelper {

    private ResourceLoader resourceLoader;

    private String[] sectionFileNames = {"section1.json", "section2.json", "section3.json"};

    @Autowired
    public ResourceLoaderHelper(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public List<SurveySection> loadSurveyDataFromFile() {
        try {
            List<SurveySection> surveySections = new ArrayList<>();
            for (String section : sectionFileNames) {
                surveySections.add(CommonUtils.getObjectMapper()
                        .readValue(resourceLoader.getResource("classpath:static/sections/" + section)
                                        .getInputStream(), SurveySection.class));

            }
            log.info("Successfully loaded from file");
            return surveySections;
        } catch (Exception e) {
            log.warn("Exception on loading question from file ", e);
            throw new RuntimeException(e);
        }
    }
}
