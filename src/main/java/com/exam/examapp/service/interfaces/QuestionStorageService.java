package com.exam.examapp.service.interfaces;

import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.QuestionUpdateRequest;
import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.question.Question;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface QuestionStorageService {
  void addQuestionsToStorage(QuestionRequest request,
                             List<MultipartFile> titles,
                             List<MultipartFile> variantPictures,
                             List<MultipartFile> numberPictures,
                             List<MultipartFile> sounds);

  List<Question> getAllQuestionsFromMyStorage();

  List<Question> getQuestionsFromMyStorage(
      QuestionType type, Difficulty difficulty, UUID topicId, int numberOfQuestions);

  List<Question> getAllQuestionsFromAdminStorage();

  List<Question> getQuestionFromAdminStorage(
      QuestionType type, Difficulty difficulty, UUID topicId, int numberOfQuestions);

  void updateQuestionInStorage(QuestionUpdateRequest request,
                                List<MultipartFile> titles,
                                List<MultipartFile> variantPictures,
                                List<MultipartFile> numberPictures,
                                List<MultipartFile> sounds);

  void removeQuestionsFromStorage(UUID questionId);
}
