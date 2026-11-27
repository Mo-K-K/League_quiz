package com.lolquiz.model;

import java.util.List;
import java.util.UUID;

public class QuizSession {

    private final String sessionId;
    private final List<Question> questions;
    private int currentIndex;
    private int score;
    private boolean completed;

    public QuizSession(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalArgumentException("QuizSession requires at least one question");
        }
        this.sessionId = UUID.randomUUID().toString();
        this.questions = List.copyOf(questions);
        this.currentIndex = 0;
        this.score = 0;
        this.completed = false;
    }

    public String getSessionId() {
        return sessionId;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getScore() {
        return score;
    }

    public boolean isCompleted() {
        return completed;
    }

    public Question getCurrentQuestion() {
        if (completed) {
            throw new IllegalStateException("Quiz session is already completed");
        }
        return questions.get(currentIndex);
    }

    public AnswerResult submitAnswer(String questionId, String selectedAnswer) {
        if (completed) {
            throw new IllegalStateException("Cannot answer questions in a completed session");
        }

        Question current = getCurrentQuestion();
        if (!current.id().equals(questionId)) {
            throw new IllegalArgumentException(
                    "Expected question '" + current.id() + "' but received '" + questionId + "'"
            );
        }

        // In Java, always compare strings with .equals(), never ==
        boolean isCorrect = current.correctAnswer().equals(selectedAnswer);
        if (isCorrect) {
            score++;
        }

        currentIndex++;
        if (currentIndex >= questions.size()) {
            completed = true;
        }

        return new AnswerResult(
                isCorrect,
                current.correctAnswer(),
                score,
                currentIndex,
                questions.size(),
                completed
        );
    }
}
