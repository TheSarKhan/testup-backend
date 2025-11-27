package com.exam.examapp.service.impl.question;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.dto.request.QuestionRequest;
import com.exam.examapp.dto.request.QuestionUpdateRequest;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.QuestionMapper;
import com.exam.examapp.model.enums.Difficulty;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.subject.Topic;
import com.exam.examapp.repository.question.QuestionRepository;
import com.exam.examapp.service.interfaces.FileService;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.question.QuestionService;
import com.exam.examapp.service.interfaces.subject.TopicService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private static final String IMAGE_TITLE_PATH = "uploads/images/questions/titles";

    private static final String IMAGE_VARIANT_PATH = "uploads/images/questions/variants";

    private static final String IMAGE_NUMBER_PATH = "uploads/images/questions/numbers";

    private static final String SOUND_PATH = "uploads/sounds";

    private final QuestionRepository questionRepository;

    private final TopicService topicService;

    private final FileService fileService;

    private final LogService logService;

    private final UserService userService;

    private final QuestionUpdateHelper questionUpdateHelper;

    @Override
    @Transactional
    public Question save(
            QuestionRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        log.info("Sual yaradılır");
        return getQuestion(request, titles, variantPictures, numberPictures, sounds);
    }

    private Question getQuestion(QuestionRequest request, List<MultipartFile> titles, List<MultipartFile> variantPictures, List<MultipartFile> numberPictures, List<MultipartFile> sounds) {
        log.info("Sual yaradilmaga baslanilir: {} type: {}", request.title(), request.type());
        Question question = QuestionMapper.requestTo(request);
        log.info("Sual uğurla xəritələndi");

        try {
            if (request.isTitlePicture()) {
                String titleUrl = fileService.uploadFile(IMAGE_TITLE_PATH, titles.getFirst());
                titles.removeFirst();
                question.setTitle(titleUrl);
            }
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("Başlıq yüklənməyib");
        }
        log.info("Başlıq uğurla yükləndi");

        try {
            if (QuestionType.LISTENING.equals(request.type())) {
                String soundUrl = fileService.uploadFile(SOUND_PATH, sounds.getFirst());
                sounds.removeFirst();
                question.setSoundUrl(soundUrl);
            }
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("Səs yüklənməyib");
        }

        log.info("Səs uğurla yükləndi");

        return finishEdit(titles, variantPictures, numberPictures, request, question, sounds);
    }

    private void createQuestionDetails(
            QuestionRequest request,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            Question question) {
        log.info("Sual detalları yaradılır");
        QuestionDetails questionDetails = request.questionDetails();

        log.info("Sual detalları xəritələndi");

        Map<Character, String> variantToContentMap = null;
        try {
            if (!request.type().equals(QuestionType.LISTENING) &&
                    !request.type().equals(QuestionType.TEXT_BASED) &&
                    !request.type().equals(QuestionType.OPEN_ENDED))
                variantToContentMap =
                        getCharacterStringMap(
                                questionDetails.variantToContentMap(),
                                questionDetails.variantToIsPictureMap(),
                                IMAGE_VARIANT_PATH,
                                variantPictures);
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("Kifayət qədər variant şəkilləri yüklənməyib");
        }
        log.info("Variant şəkillər uğurla yükləndi");

        Map<Character, String> numberToContentMap = null;
        try {
            if (request.type().equals(QuestionType.MATCH))
                numberToContentMap =
                        getCharacterStringMap(
                                questionDetails.numberToContentMap(),
                                questionDetails.numberToIsPictureMap(),
                                IMAGE_NUMBER_PATH,
                                numberPictures);
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("Yüklənmiş şəkillərin sayı kifayət deyil");
        }
        log.info("Nömrə şəkillər uğurla yükləndi");

        question.setQuestionDetails(
                new QuestionDetails(
                        variantToContentMap,
                        questionDetails.variantToIsPictureMap(),
                        questionDetails.variantToMathContentMap(),
                        questionDetails.correctVariants(),
                        numberToContentMap,
                        questionDetails.numberToIsPictureMap(),
                        questionDetails.numberToMathContentMap(),
                        questionDetails.numberToCorrectVariantsMap(),
                        questionDetails.isAuto(),
                        questionDetails.listeningTime(),
                        questionDetails.answer()));
    }

    private Map<Character, String> getCharacterStringMap(
            Map<Character, String> intCharToContentMap,
            Map<Character, Boolean> characterIsPictureMap,
            String imageNumberPath,
            List<MultipartFile> variantPictures) {
        Map<Character, String> charToContentMap =
                intCharToContentMap == null ? new HashMap<>() : new HashMap<>(intCharToContentMap);
        log.info("Simvol sətir xəritəsi uğurla yaradıldı");
        if (characterIsPictureMap != null && !charToContentMap.isEmpty()) {
            for (Map.Entry<Character, Boolean> characterBooleanEntry : characterIsPictureMap.entrySet()) {
                if (characterBooleanEntry.getValue()) {
                    String numberPictureUrl =
                            fileService.uploadFile(imageNumberPath, variantPictures.getFirst());
                    variantPictures.removeFirst();
                    charToContentMap.put(characterBooleanEntry.getKey(), numberPictureUrl);
                }
            }
        }
        log.info("Xarakter şəkilləri uğurla yükləndi");
        return charToContentMap;
    }

    @Override
    public List<Question> getFilteredQuestions(
            Difficulty difficulty, QuestionType questionType, UUID topicId) {
        Specification<Question> specification = Specification.unrestricted();

        if (difficulty != null)
            specification = specification.and(QuestionSpecification.hasDifficulty(difficulty));

        if (questionType != null)
            specification = specification.and(QuestionSpecification.hasType(questionType));

        if (topicId != null)
            specification =
                    specification.and(QuestionSpecification.hasTopic(topicService.getById(topicId)));

        return questionRepository.findAll(specification);
    }

    @Override
    public Question getQuestionById(UUID id) {
        return questionRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sual tapılmadı"));
    }

    @Override
    public Question update(
            QuestionUpdateRequest updateRequest,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        return questionUpdateHelper.update(updateRequest, titles, variantPictures, numberPictures, sounds);
    }

    private Question finishEdit(
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            QuestionRequest request,
            Question question,
            List<MultipartFile> sounds) {
        log.info("Son mərhələyə keçirildi");

        createQuestionDetails(request, variantPictures, numberPictures, question);

        log.info("Sual təfərrüatları uğurla yaradıldı");
        if (request.topicId() != null) {
            Topic topic = topicService.getById(request.topicId());
            question.setTopic(topic);
        }
        log.info("Mövzu uğurla quruldu");

        List<Question> questions = new ArrayList<>();
        if (request.questions() != null)
            for (QuestionRequest questionRequest : request.questions()) {
                questions.add(getQuestion(questionRequest, titles, variantPictures, numberPictures, sounds));
            }
        question.setQuestions(questions);

        log.info("Suallar uğurla əlavə edildi");
        Question savedQuestion = questionRepository.save(question);
        logService.save("Suallar uğurla əlavə edildi", userService.getCurrentUserOrNull());
        return savedQuestion;
    }

    @Override
    public void delete(UUID id) {
        log.info("Sual silinir");
        Question byId = getQuestionById(id);
        if (byId.isTitlePicture()) fileService.deleteFile(IMAGE_TITLE_PATH, byId.getTitle());

        for (Question question : byId.getQuestions()) delete(question.getId());

        deleteQuestionPictures(byId);

        questionRepository.delete(byId);
        log.info("Sual silindi");
        logService.save("Sual silindi", userService.getCurrentUserOrNull());
    }

    private void deleteQuestionPictures(Question byId) {
        log.info("Sual şəkilləri silinir");
        QuestionDetails questionDetails = byId.getQuestionDetails();
        for (Map.Entry<Character, Boolean> characterBooleanEntry :
                questionDetails.variantToIsPictureMap().entrySet()) {
            if (characterBooleanEntry.getValue()) {
                String imageUrl = questionDetails.variantToContentMap().get(characterBooleanEntry.getKey());
                fileService.deleteFile(IMAGE_VARIANT_PATH, imageUrl);
            }
        }

        if (byId.getSoundUrl() != null) fileService.deleteFile(IMAGE_NUMBER_PATH, byId.getSoundUrl());

        for (Map.Entry<Character, Boolean> characterBooleanEntry :
                questionDetails.numberToIsPictureMap().entrySet()) {
            if (characterBooleanEntry.getValue()) {
                String imageUrl = questionDetails.numberToContentMap().get(characterBooleanEntry.getKey());
                fileService.deleteFile(IMAGE_NUMBER_PATH, imageUrl);
            }
        }

        log.info("Sual şəkilləri silindi");
    }
}
