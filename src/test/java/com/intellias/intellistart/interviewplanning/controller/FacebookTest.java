package com.intellias.intellistart.interviewplanning.controller;

import com.intellias.intellistart.interviewplanning.InterviewPlanningApplication;
import com.intellias.intellistart.interviewplanning.exceptions.InvalidAccessTokenException;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.Facebook;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.Profile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = InterviewPlanningApplication.class)
@ActiveProfiles("test")
class FacebookTest {

  @Autowired
  private Facebook facebook;

  private static final String ACCESS_TOKEN_WITH_EMAIL_AND_NAME =
      "EAAVwMOQSGrEBAN95v7SGLGkyJm1fnQ9ZCCfQCSq4WGRdZALMSXcQsxhmR3WuWJJqEZCeJumgxkVkGdTlAso2XNZC3ph"
          + "jNzZB5XZATYDjtQEXWUT4bZCFJkWLuZAywYTPhUM2QeTLUqXdy65ZCee5jtkhFabP80gmIUt5Nz5gcyT7aJbZBT"
          + "x1UPD7AqWvbEalVM9ERfZCKB5TjMvywZDZD";
  private static final String INVALID_ACCESS_TOKEN = "blabalbalblalbalbablabalbalblalbal"
      + "baVblabalbalblalbalba";
  private static final Profile VALID_PROFILE = new Profile("Dorothy Alhgbadhecbbg Shepardman",
      "rcpytlaiid_1667565185@tfbnw.net");

  @Test
  void passInvalidUserTokenAndThrowInvalidAccessTokenException() {
    Assertions.assertThrows(InvalidAccessTokenException.class,
        () -> facebook.getProfile(INVALID_ACCESS_TOKEN));
  }
}
