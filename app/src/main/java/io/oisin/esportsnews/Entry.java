package io.oisin.esportsnews;

/**
 * Created by Oisin Quinn (@oisin1001) on 21/05/2018.
 */
public class Entry {
    private String title;
    private String section;
    private String author;
    private String date;
    private String url;

    Entry(String title, String section, String author, String date, String url) {
        this.title = title;
        this.section = section;
        this.author = author;
        this.date = date;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }
}
