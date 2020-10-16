package online.assistant.model;

import java.io.Serializable;
import java.util.Objects;

public class QAItem implements Serializable, Comparable<QAItem> {

    private int id;
    private String text;

    public QAItem() {
    }

    public QAItem(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int compareTo(QAItem o) {
        return text.compareTo(o.text);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QAItem qaItem = (QAItem) o;
        return id == qaItem.id &&
                text.equals(qaItem.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }

    @Override
    public String toString() {
        return String.format("%s (id=%d)", text, id);
    }
}
