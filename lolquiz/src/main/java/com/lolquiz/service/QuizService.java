package com.lolquiz.service;

import com.lolquiz.model.AnswerResult;
import com.lolquiz.model.Question;
import com.lolquiz.model.QuizSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class QuizService {

  private final List<Question> questionBank;
  private final Map<String, QuizSession> activeSessions = new ConcurrentHashMap<>();

  public QuizService(List<Question> questionBank) {
    if (questionBank == null || questionBank.isEmpty()) {
      throw new IllegalArgumentException("Question bank must not be empty");
    }

    this.questionBank = List.copyOf(questionBank);
  }

  public QuizSession startSession(String category, int count) {
    if (count <= 0) {
      throw new IllegalArgumentException("Question count must be greater than 0");
    }

    List<Question> filtered = new ArrayList<>();
    for (Question q : questionBank) {
      boolean matchesCategory = category == null
          || category.isBlank()
          || category.equalsIgnoreCase("ALL")
          || q.category().equalsIgnoreCase(category);
      if (matchesCategory) {
        filtered.add(q);
      }
    }

    if (filtered.isEmpty()) {
      throw new IllegalArgumentException("No questions found for category: " + category);
    }

    Collections.shuffle(filtered);
    int limit = Math.min(count, filtered.size());
    List<Question> selectedQuestions = filtered.subList(0, limit);

    QuizSession session = new QuizSession(selectedQuestions);
    activeSessions.put(session.getSessionId(), session);
    return session;
  }

  public QuizSession getSession(String sessionId) {
    QuizSession session = activeSessions.get(sessionId);
    if (session == null) {
      throw new IllegalArgumentException("Unkown session ID: " + sessionId);
    }
    return session;
  }

  public AnswerResult submitAnswer(String sessionId, String questionId, String selectedAnswer) {
    QuizSession session = getSession(sessionId);
    return session.submitAnswer(questionId, selectedAnswer);
  }
}
