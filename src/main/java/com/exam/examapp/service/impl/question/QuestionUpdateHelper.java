package com.exam.examapp.service.impl.question;

import com.exam.examapp.dto.QuestionDetails;
import com.exam.examapp.dto.request.QuestionUpdateRequest;
import com.exam.examapp.exception.custom.ResourceNotFoundException;
import com.exam.examapp.mapper.QuestionMapper;
import com.exam.examapp.model.enums.QuestionType;
import com.exam.examapp.model.question.Question;
import com.exam.examapp.model.subject.Topic;
import com.exam.examapp.repository.question.QuestionRepository;
import com.exam.examapp.service.interfaces.FileService;
import com.exam.examapp.service.interfaces.LogService;
import com.exam.examapp.service.interfaces.UserService;
import com.exam.examapp.service.interfaces.subject.TopicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionUpdateHelper {
    private static final String IMAGE_TITLE_PATH = "uploads/images/questions/titles";

    private static final String IMAGE_VARIANT_PATH = "uploads/images/questions/variants";

    private static final String IMAGE_NUMBER_PATH = "uploads/images/questions/numbers";

    private static final String SOUND_PATH = "uploads/sounds";

    private final QuestionRepository questionRepository;

    private final FileService fileService;

    private final TopicService topicService;

    private final LogService logService;

    private final UserService userService;

    public Question update(
            QuestionUpdateRequest updateRequest,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        log.info("Sual yenilənir");
        Question byId = questionRepository
                .findById(updateRequest.id())
                .orElseThrow(() -> new ResourceNotFoundException("Sual tapılmadı"));
        Question question = QuestionMapper.updateRequestTo(byId, updateRequest);

        if (updateRequest.isTitlePicture() && (updateRequest.title() == null || updateRequest.title().isEmpty())) {
            fileService.deleteFile(IMAGE_TITLE_PATH, byId.getTitle());
            String titleUrl = fileService.uploadFile(IMAGE_TITLE_PATH, titles.getFirst());
            titles.removeFirst();
            question.setTitle(titleUrl);
        }
        log.info("Sualın başlığı hazırdır");

        if (QuestionType.LISTENING.equals(updateRequest.type()) &&
                (updateRequest.soundUrl() == null || updateRequest.soundUrl().isEmpty())) {
            if (question.getSoundUrl() != null)
                fileService.deleteFile(SOUND_PATH, byId.getSoundUrl());
            String soundUrl = fileService.uploadFile(SOUND_PATH, sounds.getFirst());
            sounds.removeFirst();
            question.setSoundUrl(soundUrl);
        }
        log.info("Sualın səsi hazırdır");

        List<Question> questions = new ArrayList<>();
        if (updateRequest.questions() != null)
            for (QuestionUpdateRequest request : updateRequest.questions())
                questions.add(update(request, titles, variantPictures, numberPictures, sounds));

        question.setQuestions(questions);

        log.info("Suallar uğurla əlavə edildi");

        createQuestionDetails(updateRequest, variantPictures, numberPictures, question);

        log.info("Sual təfərrüatları uğurla yaradıldı");
        if (updateRequest.topicId() != null) {
            Topic topic = topicService.getById(updateRequest.topicId());
            question.setTopic(topic);
        }
        log.info("Mövzu uğurla quruldu");

        Question savedQuestion = questionRepository.save(question);
        logService.save("Suallar uğurla əlavə edildi", userService.getCurrentUserOrNull());
        return savedQuestion;
    }

    private void createQuestionDetails(
            QuestionUpdateRequest request,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            Question question) {
        log.info("Sual detalları yaradılır");
        QuestionDetails questionDetails = request.questionDetails();
        QuestionDetails oldQuestionDetails = question.getQuestionDetails();

        log.info("Sual detalları xəritələndi");

        Map<Character, String> variantToContentMap = null;
        try {
            if (!request.type().equals(QuestionType.LISTENING) &&
                    !request.type().equals(QuestionType.TEXT_BASED) &&
                    !request.type().equals(QuestionType.OPEN_ENDED))
                variantToContentMap =
                        getCharacterStringMap(
                                oldQuestionDetails.variantToContentMap(),
                                oldQuestionDetails.variantToIsPictureMap(),
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
                                oldQuestionDetails.numberToContentMap(),
                                oldQuestionDetails.numberToIsPictureMap(),
                                questionDetails.numberToContentMap(),
                                questionDetails.numberToIsPictureMap(),
                                IMAGE_NUMBER_PATH,
                                numberPictures);
        } catch (NoSuchElementException e) {
            throw new ResourceNotFoundException("Yüklənmiş şəkillərin sayı kifayət deyil");
        }
        log.info("Nömrə şəkillər uğurla yükləndi");
        log.info("variantToContentMap: {}", variantToContentMap);
        question.setQuestionDetails(
                new QuestionDetails(
                        variantToContentMap,
                        questionDetails.variantToIsPictureMap(),
                        questionDetails.variantToHasMathContentMap(),
                        questionDetails.correctVariants(),
                        numberToContentMap,
                        questionDetails.numberToIsPictureMap(),
                        questionDetails.numberToHasMathContentMap(),
                        questionDetails.numberToCorrectVariantsMap(),
                        questionDetails.isAuto(),
                        questionDetails.listeningTime(),
                        questionDetails.answer()));
    }

    private Map<Character, String> getCharacterStringMap(
            Map<Character, String> oldIntCharToContentMap,
            Map<Character, Boolean> oldCharacterIsPictureMap,
            Map<Character, String> intCharToContentMap,
            Map<Character, Boolean> characterIsPictureMap,
            String imagePath,
            List<MultipartFile> variantPictures) {
        Map<Character, String> charToContentMap =
                intCharToContentMap == null ? new HashMap<>() : new HashMap<>(intCharToContentMap);
        log.info("Simvol sətir xəritəsi uğurla yaradıldı deyesen");
        if (characterIsPictureMap != null && !charToContentMap.isEmpty()) {
            for (Map.Entry<Character, Boolean> characterBooleanEntry : characterIsPictureMap.entrySet()) {
                if (characterBooleanEntry.getValue()) {
                    Character key = characterBooleanEntry.getKey();
                    if (!(charToContentMap.containsKey(key) &&
                            charToContentMap.get(key) != null &&
                            charToContentMap.get(key).isEmpty())) {
                        String imageUrl = fileService.uploadFile(imagePath, variantPictures.getFirst());
                        charToContentMap.put(key, imageUrl);
                        log.info("New image: {}", imageUrl);
                        if (oldCharacterIsPictureMap != null && oldCharacterIsPictureMap.containsKey(key) &&
                                oldCharacterIsPictureMap.get(key) && oldIntCharToContentMap != null &&
                                oldIntCharToContentMap.containsKey(key) && oldIntCharToContentMap.get(key) != null)
                            fileService.deleteFile(imagePath, oldIntCharToContentMap.get(key));
                    }
                }
            }
        }
        log.info("Xarakter şəkilləri uğurla yükləndi: {}", charToContentMap);
        return charToContentMap;
    }
}
