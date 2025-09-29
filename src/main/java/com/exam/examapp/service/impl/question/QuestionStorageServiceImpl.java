package com.exam.examapp.service.impl.question;

import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.QuestionUpdateRequest;
import com.exam.examapp.exception.custom.BadRequestException;
import com.exam.examapp.exception.custom.DoesNotHavePermissionException;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.model.User;
import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.question.QuestionStorage;
import com.exam.examapp.model.subject.Topic;
import com.exam.examapp.repository.QuestionStorageRepository;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.question.QuestionService;
import com.exam.examapp.service.interfaces.question.QuestionStorageService;
import com.exam.examapp.service.interfaces.subject.TopicService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionStorageServiceImpl implements QuestionStorageService {
    private final QuestionStorageRepository questionStorageRepository;

    private final UserService userService;

    private final QuestionService questionService;

    private final TopicService topicService;

    @Value("${admin.email}")
    String adminEmail;

    private static List<Question> filterQuestions(
            QuestionType type,
            Difficulty difficulty,
            UUID topicId,
            int numberOfQuestions,
            List<Question> questions) {
        if (type != null)
            questions = questions.stream().filter(question -> question.getType() == type).toList();

        if (difficulty != null)
            questions =
                    questions.stream().filter(question -> question.getDifficulty() == difficulty).toList();

        if (topicId != null)
            questions =
                    questions.stream()
                            .filter(question -> question.getTopic().getId().equals(topicId))
                            .toList();

        return questions.stream().limit(numberOfQuestions).toList();
    }

    @Override
    public void addQuestionsToStorage(
            QuestionRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        User user = userService.getCurrentUser();

        if (!user.getPack().isCanPrepareQuestionsDb())
            throw new DoesNotHavePermissionException("You cannot prepare questions in your storage.");

        Optional<QuestionStorage> questionStorageOptional =
                questionStorageRepository.getByTeacher(user);
        Question question =
                questionService.save(request, titles, variantPictures, numberPictures, sounds);

        if (questionStorageOptional.isPresent()) {
            QuestionStorage questionStorage = questionStorageOptional.get();
            List<Question> questions = questionStorage.getQuestions();
            questions.add(question);
            questionStorage.setQuestions(questions);
            questionStorageRepository.save(questionStorage);
        } else {
            List<Question> questions = List.of(question);
            questionStorageRepository.save(
                    QuestionStorage.builder().teacher(user).questions(questions).build());
        }
    }

    @Override
    @Transactional
    public List<Question> getAllQuestionsFromMyStorage() {
        User user = userService.getCurrentUser();
        if (!user.getPack().isCanUseQuestionDb())
            throw new DoesNotHavePermissionException("You cannot use questions in your storage.");

        return questionStorageRepository
                .getByTeacher(user)
                .orElseThrow(
                        () -> new ResourceNotFoundException("You don't have any question in your storage."))
                .getQuestions();
    }

    @Override
    @Transactional
    public List<Question> getQuestionsFromMyStorage(
            QuestionType type, Difficulty difficulty, UUID topicId, int numberOfQuestions) {
        User user = userService.getCurrentUser();
        if (!user.getPack().isCanUseQuestionDb())
            throw new DoesNotHavePermissionException("You cannot use questions in your storage.");

        if (numberOfQuestions <= 0)
            throw new BadRequestException("Number of questions must be greater than 0.");

        List<Question> questions =
                questionStorageRepository
                        .getByTeacher(user)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("You don't have any question in your storage."))
                        .getQuestions();

        return filterQuestions(type, difficulty, topicId, numberOfQuestions, questions);
    }

    @Override
    public List<Question> getQuestionsFromMyStorage(UUID subjectId) {
        User user = userService.getCurrentUser();
        if (!user.getPack().isCanUseQuestionDb())
            throw new DoesNotHavePermissionException("You cannot use questions in your storage.");

        List<Topic> topics = topicService.getAllBySubjectId(subjectId);

        return questionStorageRepository
                .getByTeacher(user)
                .orElseThrow(
                        () -> new ResourceNotFoundException("You don't have any question in your storage."))
                .getQuestions()
                .stream()
                .filter(question -> topics.contains(question.getTopic()))
                .toList();
    }

    @Override
    public List<Question> getAllQuestionsFromAdminStorage() {
        User user = userService.getCurrentUser();
        if (!user.getPack().isCanUseQuestionDb())
            throw new DoesNotHavePermissionException("You cannot use questions in Admin storage.");

        return questionStorageRepository
                .getByTeacher(userService.getByEmail(adminEmail))
                .orElseThrow(
                        () -> new ResourceNotFoundException("Admin don't have any question in Admin storage."))
                .getQuestions();
    }

    @Override
    public List<Question> getQuestionFromAdminStorage(
            QuestionType type, Difficulty difficulty, UUID topicId, int numberOfQuestions) {
        User user = userService.getCurrentUser();
        if (!user.getPack().isCanUseQuestionDb())
            throw new DoesNotHavePermissionException("You cannot use questions in Admin storage.");

        if (numberOfQuestions <= 0)
            throw new BadRequestException("Number of questions must be greater than 0.");

        List<Question> questions =
                questionStorageRepository
                        .getByTeacher(userService.getByEmail(adminEmail))
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Admin don't have any question in Admin storage."))
                        .getQuestions();

        return filterQuestions(type, difficulty, topicId, numberOfQuestions, questions);
    }

    @Override
    @Transactional
    public List<User> getTeachersHasQuestionStorage() {
        return questionStorageRepository.findAll().stream()
                .filter(questionStorage -> !questionStorage.getQuestions().isEmpty())
                .map(QuestionStorage::getTeacher)
                .toList();
    }

    @Override
    public List<Question> getQuestionsByTeacherId(UUID teacherId) {
        return questionStorageRepository.getByTeacher(userService.getUserById(teacherId)).orElseThrow(() ->
                new ResourceNotFoundException("Question Storage Cannot Found.")).getQuestions();
    }

    @Override
    @Transactional
    public void updateQuestionInStorage(
            QuestionUpdateRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        User user = userService.getCurrentUser();

        if (!user.getPack().isCanPrepareQuestionsDb())
            throw new DoesNotHavePermissionException("You cannot prepare questions in your storage.");

        questionService.update(request, titles, variantPictures, numberPictures, sounds);
    }

    @Override
    public void removeQuestionsFromStorage(UUID questionId) {
        QuestionStorage questionStorage = questionStorageRepository
                .getByTeacher(userService.getCurrentUser())
                .orElseThrow(
                        () -> new ResourceNotFoundException("You don't have any question in your storage."));
        questionStorage.getQuestions().removeIf(question -> question.getId().equals(questionId));
        questionService.delete(questionId);
        questionStorageRepository.save(questionStorage);
    }
}
