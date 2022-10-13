package com.intellias.intellistart.interviewplanning;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class FileTestUtils {

  public static String readFile(String filePath) {
    try (InputStream inputStream = FileTestUtils.class.getClassLoader()
        .getResourceAsStream(filePath);
        InputStreamReader isr = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr)) {

      StringBuilder stringBuilder = new StringBuilder();
      br.lines().forEach(stringBuilder::append);

      return stringBuilder.toString();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
