package com.lolquiz.model;

public record AnswerResult(
    boolean correct,
    String correctAnswer,
    int currentScore,
    int answeredCount,
    int totalQuestions,
    boolean completed) {
}
