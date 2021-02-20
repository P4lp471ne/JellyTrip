package com.example.jellytrip.tasks;

public interface iOnDataFetched{
    void showProgressBar();
    void hideProgressBar();
    void setDataInPageWithResult(Object result);
}