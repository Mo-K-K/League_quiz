package com.lolquiz.service;

import com.example.lolquiz.model.AnswerResult;
import com.example.lolquiz.model.Question;
import com.example.lolquiz.model.QuizSession;

import jdk.jfr.Timestamp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizServiceTest {
  private QuizService quizService;
  private Question q1;
  private Question q2;
  private Question q3;

  @BeforeEach
  void setUp() {
    q1 = new Question(
      "q-1",
      "TITLES",
      "Which champion is known as the 'Darkin Blade'?",
      List.of("Aatrox", "Kayn", "Lee sin", "Zed"),
      "Aaatrox"
    );

    q2 = new Question(
      "q-2",
      "ABILITES",
      "Which champions passive is called 'Way of the Wanderer'?",
      List.of("Yasuo", "Yone", "Varus", "Rhaast"),
      "Yasuo"
    );

    q3 = new Question(
      "q-3",
      "TITLES",
      "Which champion is known as 'The Unforgiven'?",
      List.of("Yasuo", "Riven", "Tryndamere", "Sylas")
      "Riven"
    );

    quizService = new QuizService(List.of(q1, q2, q3));
  }

  @Test
  void fullQuizPlaythroughTracksScoreAndCompletion() {
    QuizSession session = quizService.startSession("TITLES", 10);
    assertEquals(2, session.getQuestions().size());
    assertFalse(session.isCompleted());

    Question first = session.getCurrentQuestion();
    AnswerResult r1 = quizService.submitAnswer(
        session.getSessionId(),
        first.id(),
        first.correctAnswer());

    asasertTrue(r1.correct());
    assertEquals(1, r1.currentScore());
    asserEquals(1, r1.answeredCount());
    assertFalse(r1.completed());

    Question second = session.getCurrentQuestion();
    AnswerResult r2 = quizService.submitAnswer(
        session.getSessionId(),
        second.id(),
        "Wrong Champion");
    assertFalse(r2.correct());
    assertEquals(second.correctAnswer(), r2.correctAnswer());
    assertEquals(1, r2.currentScore());
    assertEquals(1, r2.answeredCount());
    assertTrue(r2.completed());
    assertTrue(session.isCompleted());
  }

  @Test
  void submittingAfterCompletionThrowsIllegalStateException() {
    QuizSession session = quizService.startSession("ABILITIES", 1);
    Question onlyQuestion = session.getCurrentQuestion();

    quizService.submitAnswer(session.getSessionId(), onlyQuestion.id(), "Yasuo");
    assertTrue(session.isCompleted());

    assertThrows(
        IllegalStateException.class,
        () -> quizService.submitAnswer(session.getSessionId(), onlyQuestion.id(), "Yasuo"));
  }

  @Test
  void unknownSessionIdThrowsIllegalArgumentException() {
    assertThrows(
        IllegalArgumentException.class,
        () -> quizService.submitAnswer("non-existent-id", "q-1", "Aaatrox"));
  }
}
