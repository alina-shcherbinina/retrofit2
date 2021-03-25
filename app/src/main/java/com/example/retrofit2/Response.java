package com.example.retrofit2;

public class Response {
    int total, totalHits;
    Hit[] hits;

    @Override
    public String toString() {

        return "totalHits = " + totalHits;
    }
}
