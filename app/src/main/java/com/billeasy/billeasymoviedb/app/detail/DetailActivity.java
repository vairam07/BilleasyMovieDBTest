package com.billeasy.billeasymoviedb.app.detail;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.billeasy.billeasymoviedb.R;
import com.billeasy.billeasymoviedb.api.model.Genre;
import com.billeasy.billeasymoviedb.api.model.Images;
import com.billeasy.billeasymoviedb.api.model.Movie;
import com.billeasy.billeasymoviedb.api.model.SpokenLanguage;
import com.billeasy.billeasymoviedb.app.App;
import com.billeasy.billeasymoviedb.app.AppComponent;
import com.billeasy.billeasymoviedb.app.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class DetailActivity extends AppCompatActivity implements DetailContract.View {
    public static final String MOVIE_ID = "movie_id";
    public static final String MOVIE_TITLE = "movie_title";

    @Inject
    DetailPresenter detailPresenter;

    @BindView(R.id.container)
    View contentView;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.overviewHeader)
    View overviewHeader;
    @BindView(R.id.overviewTextView)
    TextView overviewTextView;
    @BindView(R.id.genresTextView)
    TextView genresTextView;
    @BindView(R.id.durationTextView)
    TextView durationTextView;
    @BindView(R.id.languageTextView)
    TextView languageTextView;
    @BindView(R.id.bookButton)
    Button bookButton;
    @BindView(R.id.textView)
    View errorView;
    @BindView(R.id.progressBar)
    View loadingView;

    private int movieId = -1;
    private Images images;
DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        databaseHelper=new DatabaseHelper(this);
        DaggerDetailComponent.builder()
                .appComponent(App.getAppComponent(getApplication()))
                .detailModule(new DetailModule(this))
                .build()
                .inject(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            movieId = extras.getInt(MOVIE_ID);
            String movieTitle = extras.getString(MOVIE_TITLE);

            setTitle(movieTitle);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            detailPresenter.start(movieId);
        }
        else
        {
            Cursor data=databaseHelper.getData();
            List<Movie> offmovies=new ArrayList<>();
            while (data.moveToNext())
            {
                Movie movies=new Movie(data.getString(2),data.getString(5),data.getInt(0),data.getString(3),Integer.parseInt(data.getString(4)),data.getString(1));
                offmovies.add(movies);

            }
            if (offmovies.get(movieId)!=null)
            {
                overviewTextView.setText(offmovies.get(movieId).overview);
                genresTextView.setText((CharSequence) offmovies.get(movieId).genre);
                durationTextView.setText(String.valueOf(offmovies.get(movieId).runtime));
                languageTextView.setText(offmovies.get(movieId).originalLanguage);

                loadingView.setVisibility(View.GONE);
                showContent(true);
                errorView.setVisibility(View.GONE);
            }
            else
            {
                Toast.makeText(this,"Content is not Saved.. no network Connection",Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        showContent(false);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void showContent(Movie movie) {
        String fullImageUrl = getFullImageUrl(movie);

        if (!fullImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(fullImageUrl)
                    .apply(RequestOptions.centerCropTransform())
                    .transition(withCrossFade())
                    .into(imageView);
        }

        overviewTextView.setText(getOverview(movie.overview));
        genresTextView.setText(getGenres(movie));
        durationTextView.setText(getDuration(movie));
        languageTextView.setText(getLanguages(movie));

        loadingView.setVisibility(View.GONE);
        showContent(true);
        errorView.setVisibility(View.GONE);
    }

    private String getDuration(Movie movie) {
        int runtime = movie.runtime;
        return runtime <= 0 ? "-" : getResources().getQuantityString(R.plurals.duration, runtime, runtime);
    }

    private String getOverview(String overview) {
        return TextUtils.isEmpty(overview) ? "-" : overview;
    }

    @NonNull
    private String getFullImageUrl(Movie movie) {
        String imagePath;

        if (movie.posterPath != null && !movie.posterPath.isEmpty()) {
            imagePath = movie.posterPath;
        } else {
            imagePath = movie.backdropPath;
        }

        if (images != null && images.baseUrl != null && !images.baseUrl.isEmpty()) {
            if (images.posterSizes != null) {
                if (images.posterSizes.size() > 4) {
                    // usually equal to 'w500'
                    return images.baseUrl + images.posterSizes.get(4) + imagePath;
                } else {
                    // back-off to hard-coded value
                    return images.baseUrl + "w500" + imagePath;
                }
            }
        }

        return "";
    }

    private String getGenres(Movie movie) {
        String genres = "";
        for (int i = 0; i < movie.genres.size(); i++) {
            Genre genre = movie.genres.get(i);
            genres += genre.name + ", ";
        }

        genres = removeTrailingComma(genres);

        return genres.isEmpty() ? "-" : genres;
    }

    private String getLanguages(Movie movie) {
        String languages = "";
        for (int i = 0; i < movie.spokenLanguages.size(); i++) {
            SpokenLanguage language = movie.spokenLanguages.get(i);
            languages += language.name + ", ";
        }

        languages = removeTrailingComma(languages);

        return languages.isEmpty() ? "-" : languages;
    }

    @NonNull
    private String removeTrailingComma(String text) {
        text = text.trim();
        if (text.endsWith(",")) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    @Override
    public void showError() {
        loadingView.setVisibility(View.GONE);
        showContent(false);
        errorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationSet(Images images) {
        this.images = images;
    }

    private void showContent(boolean show) {
        int visibility = show ? View.VISIBLE : View.INVISIBLE;

        contentView.setVisibility(visibility);
        overviewHeader.setVisibility(visibility);
        overviewTextView.setVisibility(visibility);
        bookButton.setVisibility(visibility);
    }

    @OnClick(R.id.bookButton)
    void onBookButtonClick() {
        String url = getString(R.string.web_url) + movieId;

        if (Build.VERSION.SDK_INT >= 16) {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(url));
        } else {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        }
    }

}