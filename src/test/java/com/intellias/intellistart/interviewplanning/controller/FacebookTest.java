package com.intellias.intellistart.interviewplanning.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
      "EAAVwMOQSGrEBAH22e0uj1lPiBJdiztC2ZBOFRF2yBCyugNVQs85ysT0J7dofgr3vT4X7iXSVdJObX5aZCZAk0F8ZCp"
          + "jNm83ZAymU6cv3LZCAuVzAXYjvFVdTkkzB2dMouy1cdVngoqlm00ZCz4hAYvXqiTZABPXZAndkaTAmZCjZCYPyZ"
          + "Auq8cZAJ63ocIZB9ZAnzdLNpGh5ietWBrTL7CNgoOovHRf";
  private static final String INVALID_ACCESS_TOKEN = "blabalbalblalbalbablabalbalblalbal"
      + "baVblabalbalblalbalba";
  private static final Profile VALID_PROFILE = new Profile("Dorothy Alhgbadhecbbg Shepardman",
      "rcpytlaiid_1667565185@tfbnw.net");

  @Test
  void passValidUserTokenAndRetrieveUserProfile() {
    /*Profile profile = facebook.getProfile(ACCESS_TOKEN_WITH_EMAIL_AND_NAME);

    assertNotNull(profile);
    Assertions.assertEquals(profile.getEmail(), VALID_PROFILE.getEmail());
    Assertions.assertEquals(profile.getName(), VALID_PROFILE.getName());
    Assertions.assertEquals(profile.hashCode(), VALID_PROFILE.hashCode());*/
  }


  @Test
  void passInvalidUserTokenAndThrowInvalidAccessTokenException() {
    Assertions.assertThrows(InvalidAccessTokenException.class,
        () -> facebook.getProfile(INVALID_ACCESS_TOKEN));
  }
}
