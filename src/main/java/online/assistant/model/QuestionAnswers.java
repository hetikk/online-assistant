package online.assistant.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class QuestionAnswers implements Serializable, Comparable<QuestionAnswers> {

    private QAItem question;
    private List<QAItem> answers;
    @SerializedName("right_answer")
    private QAItem rightAnswer;
    @SerializedName("multiple_choice")
    private boolean multipleChoice;

    public QuestionAnswers() {
    }

    public QuestionAnswers(QAItem question, List<QAItem> answers, QAItem rightAnswer, boolean multipleChoice) {
        this.question = question;
        setAnswers(answers);
        this.rightAnswer = rightAnswer;
        this.multipleChoice = multipleChoice;
    }

    public QAItem getQuestion() {
        return question;
    }

    public void setQuestion(QAItem question) {
        this.question = question;
    }

    public List<QAItem> getAnswers() {
        return answers;
    }

    public void setAnswers(List<QAItem> answers) {
        Collections.sort(answers);
        this.answers = answers;
    }

    public QAItem getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(QAItem rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public boolean isMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    @Override
    public int compareTo(QuestionAnswers o) {
        return question.compareTo(o.question);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionAnswers that = (QuestionAnswers) o;
        return question.equals(that.question);
    }

    @Override
    public int hashCode() {
        return question.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("Вопрос: %s (id=%d, multiple_choice=%b)\n",
                question.getText(), question.getId(), multipleChoice));

        for (QAItem answer : answers) {
            builder.append("  · ").append(answer.toString()).append("\n");
        }

        builder.append("Правильный ответ: ").append(rightAnswer == null ? null : rightAnswer.toString());

        return builder.toString();
    }
}
