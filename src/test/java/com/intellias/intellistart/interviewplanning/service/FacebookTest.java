package com.intellias.intellistart.interviewplanning.service;

import com.intellias.intellistart.interviewplanning.exceptions.InvalidAccessTokenException;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.Facebook;
import com.intellias.intellistart.interviewplanning.security.authentication.facebook.Profile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class FacebookTest {


  @Mock
  private RestTemplate restTemplate;
  private Facebook facebook;

  private static final String ACCESS_TOKEN =
      "blabalbalblalbalbablabalbalblalbal" + "baVblabalbalblalbalba";
  private static final Profile VALID_PROFILE = new Profile("Dorothy Alhgbadhecbbg Shepardman",
      "rcpytlaiid_1667565185@tfbnw.net");
  private static final String URL = "https://graph.facebook.com/v2.12/me?fields=email,name,"
      + "id&access_token=";

  @BeforeEach
  public void setUp() {
    facebook = new Facebook(restTemplate);
  }

  @Test
  void passUserTokenAndRetrieveProfile() {
    Mockito.when(restTemplate.getForObject(URL + ACCESS_TOKEN, Profile.class))
        .thenReturn(VALID_PROFILE);

    Profile profile = facebook.getProfile(ACCESS_TOKEN);

    Assertions.assertEquals(VALID_PROFILE.getName(), profile.getName());
    Assertions.assertEquals(VALID_PROFILE.getEmail(), profile.getEmail());
    Assertions.assertEquals(VALID_PROFILE, profile);
  }

  @Test
  void passInvalidUserTokenAndThrowInvalidAccessTokenException() {
    Mockito.when(restTemplate.getForObject(URL + ACCESS_TOKEN, Profile.class))
        .thenThrow(InvalidAccessTokenException.class);

    Assertions.assertThrows(InvalidAccessTokenException.class,
        () -> facebook.getProfile(ACCESS_TOKEN));
  }
}
