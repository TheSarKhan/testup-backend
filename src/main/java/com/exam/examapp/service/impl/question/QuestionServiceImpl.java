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

    @Override
    @Transactional
    public Question save(
            QuestionRequest request,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        log.info("Saving question");
        return getQuestion(request, titles, variantPictures, numberPictures, sounds);
    }

    private Question getQuestion(QuestionRequest request, List<MultipartFile> titles, List<MultipartFile> variantPictures, List<MultipartFile> numberPictures, List<MultipartFile> sounds) {
        Question question = QuestionMapper.requestTo(request);
        log.info("Question mapped successfully");

        try {
            if (request.isTitlePicture()) {
                String titleUrl = fileService.uploadFile(IMAGE_TITLE_PATH, titles.getFirst());
                titles.removeFirst();
                question.setTitle(titleUrl);
            }
        }catch (NoSuchElementException e){
            log.error("No title uploaded");
            throw new ResourceNotFoundException("No title uploaded");
        }
        log.info("Title uploaded successfully");

        try {
            if (QuestionType.LISTENING.equals(request.questionType())) {
                String soundUrl = fileService.uploadFile(SOUND_PATH, sounds.getFirst());
                sounds.removeFirst();
                question.setSoundUrl(soundUrl);
            }
        }catch (NoSuchElementException e){
            log.error("No sound uploaded");
            throw new ResourceNotFoundException("No sound uploaded");
        }
        log.info("Sound uploaded successfully");

        return finishEdit(titles, variantPictures, numberPictures, request, question, sounds);
    }

    private void createQuestionDetails(
            QuestionRequest request,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            Question question) {
        QuestionDetails questionDetails = request.questionDetails();

        Map<Character, String> variantToContentMap = null;
        try {
            if (!request.questionType().equals(QuestionType.LISTENING) &&
                    !request.questionType().equals(QuestionType.TEXT_BASED) &&
                    !request.questionType().equals(QuestionType.OPEN_ENDED))
                variantToContentMap =
                        getCharacterStringMap(
                                questionDetails.variantToContentMap(),
                                questionDetails.variantToIsPictureMap(),
                                IMAGE_VARIANT_PATH,
                                variantPictures);
        }catch (NoSuchElementException e){
            log.error("Not enough variant pictures uploaded");
            throw new ResourceNotFoundException("Not enough variant pictures uploaded");
        }
        log.info("Variant pictures uploaded successfully");

        Map<Character, String> numberToContentMap = null;
        try {
            if (request.questionType().equals(QuestionType.MATCH))
                numberToContentMap =
                        getCharacterStringMap(
                                questionDetails.numberToContentMap(),
                                questionDetails.numberToIsPictureMap(),
                                IMAGE_NUMBER_PATH,
                                numberPictures);
        }catch (NoSuchElementException e){
            log.error("Not enough number pictures uploaded");
            throw new ResourceNotFoundException("Not enough number pictures uploaded");
        }
        log.info("Number pictures uploaded successfully");

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
            Map<Character, String> intCharToContentMap,
            Map<Character, Boolean> characterIsPictureMap,
            String imageNumberPath,
            List<MultipartFile> variantPictures) {
        Map<Character, String> charToContentMap =
                intCharToContentMap == null ? new HashMap<>() : new HashMap<>(intCharToContentMap);
        log.info("Character string map created successfully");
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
        log.info("Number pictures uploaded successfully");
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
                .orElseThrow(() -> new ResourceNotFoundException("Question not found."));
    }

    @Override
    public Question update(
            QuestionUpdateRequest updateRequest,
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            List<MultipartFile> sounds) {
        Question byId = getQuestionById(updateRequest.id());
        QuestionRequest request = updateRequest.question();
        Question question = QuestionMapper.updateRequestTo(byId, request);

        if (request.isTitlePicture()) {
            fileService.deleteFile(IMAGE_TITLE_PATH, byId.getTitle());
            String titleUrl = fileService.uploadFile(IMAGE_TITLE_PATH, titles.getFirst());
            titles.removeFirst();
            question.setTitle(titleUrl);
        }

        if (QuestionType.LISTENING.equals(request.questionType())) {
            if (question.getSoundUrl() != null)
                fileService.deleteFile(IMAGE_NUMBER_PATH, question.getSoundUrl());
            String soundUrl = fileService.uploadFile(SOUND_PATH, sounds.getFirst());
            sounds.removeFirst();
            question.setSoundUrl(soundUrl);
        }

        for (Question byIdQuestion : byId.getQuestions()) {
            delete(byIdQuestion.getId());
        }

        deleteQuestionPictures(byId);

        return finishEdit(titles, variantPictures, numberPictures, request, question, sounds);
    }


    private Question finishEdit(
            List<MultipartFile> titles,
            List<MultipartFile> variantPictures,
            List<MultipartFile> numberPictures,
            QuestionRequest request,
            Question question,
            List<MultipartFile> sounds) {
        createQuestionDetails(request, variantPictures, numberPictures, question);

        log.info("Question details created successfully");
        if (request.topicId() != null) {
            Topic topic = topicService.getById(request.topicId());
            question.setTopic(topic);
        }
        log.info("Topic set successfully");

        List<Question> questions = new ArrayList<>();
        if (request.questions() != null)
            for (QuestionRequest questionRequest : request.questions()) {
                questions.add(getQuestion(questionRequest, titles, variantPictures, numberPictures, sounds));
            }
        question.setQuestions(questions);

        log.info("Questions added successfully");
        return questionRepository.save(question);
    }

    @Override
    public void delete(UUID id) {
        Question byId = getQuestionById(id);
        if (byId.isTitlePicture()) fileService.deleteFile(IMAGE_TITLE_PATH, byId.getTitle());

        for (Question question : byId.getQuestions()) delete(question.getId());

        deleteQuestionPictures(byId);

        questionRepository.delete(byId);
    }

    private void deleteQuestionPictures(Question byId) {
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
    }
}
