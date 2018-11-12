package com.sumin.movies.pojo;

public class Review {

    private String authorName;
    private String textReview;

    public Review(String authorName, String textReview) {
        this.authorName = authorName;
        this.textReview = textReview;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getTextReview() {
        return textReview;
    }
}
