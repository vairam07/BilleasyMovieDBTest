package com.billeasy.billeasymoviedb.app.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.billeasy.billeasymoviedb.R;
import com.billeasy.billeasymoviedb.api.model.Images;
import com.billeasy.billeasymoviedb.api.model.Movie;
import com.billeasy.billeasymoviedb.api.model.Movies;
import com.billeasy.billeasymoviedb.app.App;
import com.billeasy.billeasymoviedb.app.DatabaseHelper;
import com.billeasy.billeasymoviedb.app.detail.DetailActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.billeasy.billeasymoviedb.app.detail.DetailActivity.MOVIE_ID;
import static com.billeasy.billeasymoviedb.app.detail.DetailActivity.MOVIE_TITLE;

public class MainActivity extends AppCompatActivity implements
        MainContract.View,
        SwipeRefreshLayout.OnRefreshListener, EndlessScrollListener.ScrollToBottomListener, MoviesAdapter.ItemClickListener {
    private static final String TAG = "Main";
    DatabaseHelper databaseHelper;
    @Inject
    MainPresenter presenter;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView contentView;
    @BindView(R.id.textView)
    View errorView;
    @BindView(R.id.progressBar)
    View loadingView;

    private MoviesAdapter moviesAdapter;
    private EndlessScrollListener endlessScrollListener;
    private Images images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        databaseHelper=new DatabaseHelper(this);
        setupContentView();
        DaggerMainComponent.builder()
                .appComponent(App.getAppComponent(getApplication()))
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
    }
    private void setupContentView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        endlessScrollListener = new EndlessScrollListener(linearLayoutManager, this);
        contentView.setLayoutManager(linearLayoutManager);
        contentView.addOnScrollListener(endlessScrollListener);
    }

    @Override
    public void onRefresh() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            endlessScrollListener.onRefresh();
            presenter.onPullToRefresh();
        }
        else
        {
            Toast.makeText(this,"No network Connection",Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onScrollToBottom() {
        presenter.onScrollToBottom();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            presenter.start();
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
            if (moviesAdapter == null) {
                moviesAdapter = new MoviesAdapter(offmovies, this, images, this);
                contentView.setAdapter(moviesAdapter);
            }
            loadingView.setVisibility(View.GONE);
            contentView.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.GONE);
        }



    }

    @Override
    public void showLoading(boolean isRefresh) {
        if (isRefresh) {
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
        } else {
            loadingView.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);
            errorView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showContent(List<Movie> movies, boolean isRefresh) {
        if (moviesAdapter == null) {
            moviesAdapter = new MoviesAdapter(movies, this, images, this);
            contentView.setAdapter(moviesAdapter);
        } else {
            if (isRefresh) {
                moviesAdapter.clear();
            }
            moviesAdapter.addAll(movies);
            moviesAdapter.notifyDataSetChanged();
        }

        // Delay SwipeRefreshLayout animation by 1.5 seconds
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1500);

        loadingView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void showError() {
        swipeRefreshLayout.setRefreshing(false);
        loadingView.setVisibility(View.GONE);
        contentView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationSet(Images images) {
        this.images = images;

        if (moviesAdapter != null) {
            moviesAdapter.setImages(images);
        }
    }

    @Override
    public void onItemClick(int movieId, String movieTitle) {
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(MOVIE_ID, movieId);
        i.putExtra(MOVIE_TITLE, movieTitle);
        startActivity(i);
    }

    @OnClick(R.id.textView)
    void onClickErrorView() {
        presenter.start();
    }

}
