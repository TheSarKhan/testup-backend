package com.exam.examapp.service.interfaces.question;

import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.QuestionUpdateRequest;
import com.exam.examapp.dto.response.subject.QuestionResponse;
import com.exam.examapp.model.User;
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

  List<QuestionResponse> getAllQuestionsFromMyStorage();

  List<QuestionResponse> getQuestionsFromMyStorage(
      QuestionType type, Difficulty difficulty, UUID topicId, int numberOfQuestions);

  List<QuestionResponse> getQuestionsFromMyStorage(UUID subjectId);

  List<QuestionResponse> getAllQuestionsFromAdminStorage();

  List<QuestionResponse> getQuestionFromAdminStorage(
      QuestionType type, Difficulty difficulty, UUID topicId, int numberOfQuestions);

  List<User> getTeachersHasQuestionStorage();

  List<QuestionResponse> getQuestionsByTeacherId(UUID teacherId);

  void updateQuestionInStorage(QuestionUpdateRequest request,
                                List<MultipartFile> titles,
                                List<MultipartFile> variantPictures,
                                List<MultipartFile> numberPictures,
                                List<MultipartFile> sounds);

  void removeQuestionsFromStorage(UUID questionId);
}
