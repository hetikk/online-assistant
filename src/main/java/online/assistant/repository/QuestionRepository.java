package online.assistant.repository;

import com.google.gson.Gson;
import online.assistant.model.Question;
import online.assistant.model.TestRequest;
import online.assistant.service.TestService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Repository
public class QuestionRepository {

    private static final Logger LOG = LoggerFactory.getLogger(QuestionRepository.class);

    @Autowired
    private Gson gson;

    @Autowired
    private TestService service;

    private Map<String, Question[]> allSubjects;

    private static final String PREFIX = "subjects/";
    private static final String POSTFIX = ".json";

    @Value("file:*/**/resources/**/" + PREFIX + "/*.json")
    private Resource[] resources;

    /**
     * Инициализация данных
     * Считывает имеющиеся в папке 'subjects' файлы *.json
     * и хранит их распаршенное значение
     */
    @PostConstruct
    public void init() {
        LOG.info(getClass().getSimpleName() + " init");

        allSubjects = new HashMap<>();
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            LOG.info("Scanned file: " + filename);
            allSubjects.put(filename, parseJson(resource));
        }
    }

    /**
     * Метод, парсящий .json файлы
     *
     * @param resource файл, который нужно распарсить
     * @return массив всех вопросов и ответов на них
     */
    private Question[] parseJson(Resource resource) {
        try (InputStream inputStream = resource.getInputStream()) {
            String json = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            return gson.fromJson(service.normalize(json), Question[].class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Метод, формирующий ответ пользователю
     *
     * @param request данные, пришедшие от пользователя
     * @return часть ответов, ключем у который является порядковый номер вопроса начиная с 1
     */
    public Map<String, Set<String>> getAnswers(TestRequest request) {
        Map<String, Set<String>> result = new LinkedHashMap<>();
        Question[] allQuestions = allSubjects.get(request.getSubject() + POSTFIX);
        if (allQuestions == null) {
            allQuestions = allSubjects.get(request.getSubject().replaceAll("C", "С") + POSTFIX); // replace C(eng) with С(ru)
            if (allQuestions == null) {
                LOG.info("Requested resource: " + request.getSubject() + POSTFIX);
                LOG.info("Existing resources: " + allSubjects.keySet());
                result.put("#error#", Collections.singleton("'" + request.getSubject() + "' not found"));
                return result;
            }
        }

        Map<String, Map<String, String>> userQuestions = request.getQuestions();
        int questSeqNum = 1;
        for (Map.Entry<String, Map<String, String>> questionEntry : userQuestions.entrySet()) {
            String questSeqNumStr = String.valueOf(questSeqNum);
            String question = questionEntry.getKey().trim();

            Map<String, String> answers = questionEntry.getValue();
            for (Question allQuestion : allQuestions) {
                // среди всех вопросов нашел текущий (который прислал студент)
                if (question.equals(allQuestion.getText())) {
                    Set<String> allQuestionAnswers = allQuestion.getAnswers();

                    for (Map.Entry<String, String> answerEntry : answers.entrySet()) {
                        // среди всех ответов на данный вопрос содержится текущий ответ (который прислал студент)
                        if (allQuestionAnswers.contains(answerEntry.getKey().trim())) {
                            if (result.containsKey(questSeqNumStr)) {
                                try {
                                    Set<String> rightAnswers = result.get(questSeqNumStr);
                                    rightAnswers.add(answerEntry.getValue());
                                    result.put(questSeqNumStr, rightAnswers);
                                } catch (java.lang.UnsupportedOperationException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                result.put(questSeqNumStr, new TreeSet<>(Collections.singletonList(answerEntry.getValue())));
                            }
                        }
                    }
                }
            }
            questSeqNum++;
        }

        return result;
    }

}
