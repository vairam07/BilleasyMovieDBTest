package com.billeasy.billeasymoviedb.app.main;

import com.billeasy.billeasymoviedb.api.model.Images;
import com.billeasy.billeasymoviedb.api.model.Movie;

import java.util.List;



public interface MainContract {

    interface View {

        void showLoading(boolean isRefresh);

        void showContent(List<Movie> movies, boolean isRefresh);

        void showError();

        void onConfigurationSet(Images images);

    }

    interface Presenter {

        void start();

        void onPullToRefresh();

        void onScrollToBottom();

        void finish();

    }

}