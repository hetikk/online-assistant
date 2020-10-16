package online.assistant.service;

import com.google.gson.Gson;
import online.assistant.controller.IndexController;
import online.assistant.model.TestRequest;
import online.assistant.repository.QuestionRepository;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class TestService {

    private static final Logger LOG = LoggerFactory.getLogger(TestService.class);

    @Autowired
    private Gson gson;

    @Autowired
    private QuestionRepository repository;

    @Value("file:*/**/resources/access-allowed.json")
    private Resource[] accessAllowed;
    private List<String> GOOD_MANS;

    private static final double PERMITTED_PERCENT = 0.60;

    /**
     * Метод, в котором считывается файл, содержащий список пользователей,
     * которые получают полный список ответов
     */
    @PostConstruct
    public void init() {
        LOG.info(getClass().getSimpleName() + " init");

        try (InputStream inputStream = accessAllowed[0].getInputStream()) {
            String json = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            GOOD_MANS = Arrays.asList(gson.fromJson(json, String[].class));
            LOG.info("Scanned file: " + accessAllowed[0].getFilename());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String solveTest(String body) {
        TestRequest request = parse(body);

        Map<String, Set<String>> response = repository.getAnswers(request);
        int originalSize = request.getQuestions().size();
        int permitted = (int) (response.size() * PERMITTED_PERCENT);

        if (IndexController.active) {
            if (!GOOD_MANS.contains(request.getStudent())) {
                for (int i = 0; i < permitted; i++) {
                    int r = (int)(Math.random() * originalSize);
                    response.remove(r + "");
                }
            }
        } else {
            response = new HashMap<>();
            response.put("state", Collections.singleton("off"));
        }

        LOG.info("Студент: " + request.getStudent());
        LOG.info("Дисциплина: " + request.getSubject());
        LOG.info(String.format("Разрешено: %s", GOOD_MANS.contains(request.getStudent()) ? "100%" : ((int)(PERMITTED_PERCENT * 100)) + "%"));
        LOG.info(IndexController.active ? ("Отправлено: " + response.size() + "/" + originalSize) : "Обработка отключена");

        return gson.toJson(response);
    }

    /**
     * Метод парсит HTML пользователя
     *
     * Нужные данные:
     * - ID пользователся или ФИО
     * - предмет, который сдает пользователь
     * - вопросы вместе с ответами и их идентифекаторами
     *
     * @param body HTML документ, который нужно распарсить
     * @return объект, который содержит в себе нужные данные
     */
    private TestRequest parse(String body) {
        Element doc = Jsoup.parse(body);
        TestRequest request = new TestRequest();
        Map<String, Map<String, String>> questions = new LinkedHashMap<>();

        Element _student = doc.select("#LoginTB, #studName2").first();
        String student;
        if (_student.text().isEmpty())
            student = _student.attr("value");
        else
            student = _student.text();
        request.setStudent(student);

        String subject = doc.select("#disDDL option[selected=\"selected\"], #disName").first().text(); // TODO: add normalize(), if want
        request.setSubject(subject);

        Elements rows = doc.select("#mainPanel > table > tbody > tr:nth-child(odd)");
        for (Element row : rows) {
            String question = normalize(row.select("*[id~=^taskRep_ctl[0-9]{2}_task1Label$]").first().text()); // TODO: add normalize(), if want
            Elements answerBlocks = row.select("*[id~=(^taskRep_ctl[0-9]{2}_RadioButtonList1$)|(^taskRep_ctl[0-9]{2}_CheckBoxList1$)]");
            Map<String, String> answers = new LinkedHashMap<>();
            for (Element answerBlock : answerBlocks) {
                Elements answerRows = answerBlock.select("td");
                for (Element answerRow : answerRows) {
                    Element input = answerRow.child(0);
                    Element label = answerRow.child(1);
                    answers.put(
                            normalize(label.text().trim()),
                            input.attr("id") // TODO: add normalize(), if want
                    );
                }
            }

            questions.put(question, answers);
        }
        request.setQuestions(questions);

        return request;
    }

    /**
     * Метод, нормализующий данные
     *
     * Полученные от пользователя данные могут немного отличаться от тех, которые у нас имеются,
     * поэтому нужно их как-то нормализовать
     *
     * - пробельные символы (с кодами 32 и 160) удаляются
     * - '&#171;' заменяется на '«'
     * - '&#187;' заменяется на '»'
     *
     * @param str строка, которую нужно нормализовать
     * @return нормализованная строка
     */
    public String normalize(String str) {
        StringBuilder builder = new StringBuilder(
                str
                        .replaceAll("\u0026#171;", "«")
                        .replaceAll("\u0026#187;", "»")
        );

        int i = 0;
        while (i < builder.length()) {
            if (builder.charAt(i) == 160 || builder.charAt(i) == 32) {
                builder.deleteCharAt(i);
            } else {
                i++;
            }
        }

        return builder.toString();
    }

}
