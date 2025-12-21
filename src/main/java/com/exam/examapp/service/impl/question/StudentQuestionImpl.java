package com.exam.examapp.service.impl.question;

import com.exam.examapp.dto.response.StudentQuestionResponse;
import com.exam.examapp.dto.response.subject.QuestionResponse;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.ExamMapper;
import com.exam.examapp.model.enums.AnswerStatus;
import com.exam.examapp.model.exam.StudentExam;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.subject.SubjectStructureQuestion;
import com.exam.examapp.service.interfaces.exam.StudentExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentQuestionImpl {
    private final StudentExamService studentExamService;

    private final ExamMapper examMapper;

    public StudentQuestionResponse getStudentQuestionResponse(UUID studentExamId, UUID questionId){
        StudentExam studentExam = studentExamService.getStudentExam(studentExamId);
        List<SubjectStructureQuestion> subjectStructureQuestions = studentExam.getExam().getSubjectStructureQuestions();

        for (SubjectStructureQuestion subjectStructureQuestion : subjectStructureQuestions) {
            List<Question> questions = subjectStructureQuestion.getQuestion();
            for (Question question : questions) {
                List<Question> questionList = question.getQuestions();
                if (questionList != null && !questionList.isEmpty()){
                    boolean isThere = false;
                    for (Question questionQuestion : questionList) {
                        if (questionQuestion.getId().equals(questionId)) {
                            isThere = true;
                            break;
                        }
                    }
                    if (isThere){
                        QuestionResponse questionResponse = examMapper.questionToResponse(question);
                        List<String> answers = new ArrayList<>();
                        List<Boolean> isAnswerPictures = new ArrayList<>();
                        List<AnswerStatus> answerStatuses = new ArrayList<>();
                        for (Question value : questionList) {
                            UUID uuid = value.getId();
                            String answer = studentExam.getQuestionIdToAnswerMap().get(uuid);
                            AnswerStatus answerStatus = studentExam.getQuestionIdToAnswerStatusMap().get(uuid);
                            Boolean isAnswerPicture = studentExam.getQuestionIdToIsAnswerPictureMap().get(uuid);

                            answers.add(answer);
                            isAnswerPictures.add(isAnswerPicture);
                            answerStatuses.add(answerStatus);
                        }
                        return new StudentQuestionResponse(
                                questionResponse,
                                answers,
                                isAnswerPictures,
                                answerStatuses
                        );
                    }
                }
                if (question.getId().equals(questionId)){
                    QuestionResponse questionResponse = examMapper.questionToResponse(question);
                    String answer = studentExam.getQuestionIdToAnswerMap().get(questionId);
                    AnswerStatus answerStatus = studentExam.getQuestionIdToAnswerStatusMap().get(questionId);
                    Boolean isAnswerPicture = studentExam.getQuestionIdToIsAnswerPictureMap().get(questionId);
                    return new StudentQuestionResponse(questionResponse,
                            List.of(answer), List.of(isAnswerPicture), List.of(answerStatus));
                }
            }
        }
        throw new ResourceNotFoundException("Verilen id ye uygun sual tapilmadi.");
    }
}
