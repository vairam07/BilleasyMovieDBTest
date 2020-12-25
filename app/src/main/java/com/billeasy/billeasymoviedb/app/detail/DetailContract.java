package com.billeasy.billeasymoviedb.app.detail;



import com.billeasy.billeasymoviedb.api.model.Images;
import com.billeasy.billeasymoviedb.api.model.Movie;

import java.util.List;



public interface DetailContract {

    interface View {

        void showLoading();

        void showContent(Movie movie);

        void showError();

        void onConfigurationSet(Images images);

    }

    interface Presenter {

        void start(int movieId);

        void finish();

    }

}