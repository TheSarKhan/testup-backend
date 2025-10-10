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
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.question.QuestionService;
import com.exam.examapp.service.interfaces.question.QuestionStorageService;
import com.exam.examapp.service.interfaces.subject.TopicService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionStorageServiceImpl implements QuestionStorageService {
    private final QuestionStorageRepository questionStorageRepository;

    private final UserService userService;

    private final QuestionService questionService;

    private final TopicService topicService;

    private final LogService logService;

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
    @Transactional
    public void addQuestionsToStorage(
            QuestionRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        log.info("Sual sual bazasına əlavə edilir");
        User user = userService.getCurrentUser();

        if (!user.getPack().isCanPrepareQuestionsDb())
            throw new DoesNotHavePermissionException("Sual bazasına sual əlavə edə bilməzsiniz");

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
        log.info("Sual sual bazasına əlavə edildi");
        logService.save("Sual sual bazasına əlavə edildi", userService.getCurrentUserOrNull());
    }

    @Override
    @Transactional
    public List<Question> getAllQuestionsFromMyStorage() {
        User user = userService.getCurrentUser();
        if (!user.getPack().isCanUseQuestionDb())
            throw new DoesNotHavePermissionException("Sual bazasındaki suallardan istifadə edə bilməzsiniz");

        return questionStorageRepository
                .getByTeacher(user)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Sual bazanızda heç bir sualınız yoxdur"))
                .getQuestions();
    }

    @Override
    @Transactional
    public List<Question> getQuestionsFromMyStorage(
            QuestionType type, Difficulty difficulty, UUID topicId, int numberOfQuestions) {
        User user = userService.getCurrentUser();
        if (!user.getPack().isCanUseQuestionDb())
            throw new DoesNotHavePermissionException("Sual bazasındaki suallardan istifadə edə bilməzsiniz");

        if (numberOfQuestions <= 0)
            throw new BadRequestException("Sualların sayı 0-dan çox olmalıdır");

        List<Question> questions =
                questionStorageRepository
                        .getByTeacher(user)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Sual bazanızda heç bir sualınız yoxdur"))
                        .getQuestions();

        return filterQuestions(type, difficulty, topicId, numberOfQuestions, questions);
    }

    @Override
    @Transactional
    public List<Question> getQuestionsFromMyStorage(UUID subjectId) {
        User user = userService.getCurrentUser();
        if (!user.getPack().isCanUseQuestionDb())
            throw new DoesNotHavePermissionException("Sual bazasındaki suallardan istifadə edə bilməzsiniz");

        List<Topic> topics = topicService.getAllBySubjectId(subjectId);

        return questionStorageRepository
                .getByTeacher(user)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Sual bazanızda heç bir sualınız yoxdur"))
                .getQuestions()
                .stream()
                .filter(question -> topics.contains(question.getTopic()))
                .toList();
    }

    @Override
    @Transactional
    public List<Question> getAllQuestionsFromAdminStorage() {
        User user = userService.getCurrentUser();
        if (!user.getPack().isCanUseQuestionDb())
            throw new DoesNotHavePermissionException("Adminin sual bazasındaki suallardan istifadə edə bilməzsiniz");

        return questionStorageRepository
                .getByTeacher(userService.getByEmail(adminEmail))
                .orElseThrow(
                        () -> new ResourceNotFoundException("Adminin sual bazasında heç bir sual yoxdur"))
                .getQuestions();
    }

    @Override
    @Transactional
    public List<Question> getQuestionFromAdminStorage(
            QuestionType type, Difficulty difficulty, UUID topicId, int numberOfQuestions) {
        User user = userService.getCurrentUser();
        if (!user.getPack().isCanUseQuestionDb())
            throw new DoesNotHavePermissionException("Adminin sual bazasındaki suallardan istifadə edə bilməzsiniz");

        if (numberOfQuestions <= 0)
            throw new BadRequestException("Sualların sayı 0-dan çox olmalıdır");

        List<Question> questions =
                questionStorageRepository
                        .getByTeacher(userService.getByEmail(adminEmail))
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Adminin sual bazasında heç bir sual yoxdur"))
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
    @Transactional
    public List<Question> getQuestionsByTeacherId(UUID teacherId) {
        return questionStorageRepository.getByTeacher(userService.getUserById(teacherId)).orElseThrow(() ->
                new ResourceNotFoundException("Sual bazası tapılmadı.")).getQuestions();
    }

    @Override
    @Transactional
    public void updateQuestionInStorage(
            QuestionUpdateRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        log.info("Sual bazasındaki sual yenilənir");
        User user = userService.getCurrentUser();

        if (!user.getPack().isCanPrepareQuestionsDb())
            throw new DoesNotHavePermissionException("Sual bazasındaki sualı yeniləyə bilməzsiniz");

        questionService.update(request, titles, variantPictures, numberPictures, sounds);
        log.info("Sual bazasındaki sual yeniləndi");
        logService.save("Sual bazasındaki sual yeniləndi", userService.getCurrentUserOrNull());
    }

    @Override
    @Transactional
    public void removeQuestionsFromStorage(UUID questionId) {
        log.info("Sual bazasındaki sual silinir");
        QuestionStorage questionStorage = questionStorageRepository
                .getByTeacher(userService.getCurrentUser())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Sual bazanızda heç bir sualınız yoxdur"));
        questionStorage.getQuestions().removeIf(question -> question.getId().equals(questionId));
        questionService.delete(questionId);
        questionStorageRepository.save(questionStorage);
        log.info("Sual bazasındaki sual silindi");
        logService.save("Sual bazasındaki sual silindi", userService.getCurrentUserOrNull());
    }
}
