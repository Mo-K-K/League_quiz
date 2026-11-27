package com.lolquiz.model;

import java.util.List;

public record Question(
    String id,
    String category,
    String prompt,
    List<String> options,
    String correctAnswer) {
  public Question {
    if (id == null || id.isBlank()) {
      throw new IllegalArgumentException("Question id must not be blank");
    }
    if (options == null || options.size() < 2) {
      throw new IllegalArgumentException("Question must have more than one option");
    }
    if (!options.contains(correctAnswer)) {
      throw new IllegalArgumentException("Correct answer must be one of the options");
    }
    options = List.copyOf(options);
  }
}
