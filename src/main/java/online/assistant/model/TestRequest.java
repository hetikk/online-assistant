package online.assistant.model;

import java.util.Map;

public class TestRequest {

    private String student;
    private String subject;
    private Map<String, Map<String, String>> questions; // Map<quest, Map<ans, ansID>>

    public TestRequest() {
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, Map<String, String>> getQuestions() {
        return questions;
    }

    public void setQuestions(Map<String, Map<String, String>> questions) {
        this.questions = questions;
    }
}
