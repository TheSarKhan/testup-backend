package com.exam.examapp.service.interfaces.question;

import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.QuestionUpdateRequest;
import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.question.Question;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface QuestionService {
    Question save(QuestionRequest question,
                  List<MultipartFile> titles,
                  List<MultipartFile> variantPictures,
                  List<MultipartFile> numberPictures,
                  List<MultipartFile> sounds);

    List<Question> getFilteredQuestions(Difficulty difficulty,
                                        QuestionType questionType,
                                        UUID topicId);

    Question getQuestionById(UUID id);

    Question update(QuestionUpdateRequest request,
                    List<MultipartFile> titles,
                    List<MultipartFile> variantPictures,
                    List<MultipartFile> numberPictures,
                    List<MultipartFile> sounds);

    void delete(UUID id);
}
