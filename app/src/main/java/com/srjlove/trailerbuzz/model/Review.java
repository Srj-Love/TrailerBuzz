package com.srjlove.trailerbuzz.model;

/**
 * Created by Suraj on 11/15/2017.
 */

public class Review {

    private String Author, ReviewContent;

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getReviewContent() {
        return ReviewContent;
    }

    public void setReviewContent(String reviewContent) {
        ReviewContent = reviewContent;
    }

    public Review() {
    }

    public Review(String author, String reviewContent) {
        Author = author;
        ReviewContent = reviewContent;
    }


}

