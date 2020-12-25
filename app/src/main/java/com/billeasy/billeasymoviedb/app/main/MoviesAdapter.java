package com.billeasy.billeasymoviedb.app.main;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.billeasy.billeasymoviedb.R;
import com.billeasy.billeasymoviedb.api.model.Images;
import com.billeasy.billeasymoviedb.api.model.Movie;
import com.billeasy.billeasymoviedb.app.DatabaseHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;



public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {
    private List<Movie> movies;
    private AppCompatActivity activity;
    private Images images;
    private ItemClickListener itemClickListener;
    DatabaseHelper databaseHelper;
    public MoviesAdapter(List<Movie> movies, AppCompatActivity activity, Images images, ItemClickListener itemClickListener) {
        this.movies = movies;
        this.activity = activity;
        this.images = images;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        databaseHelper=new DatabaseHelper(activity.getApplicationContext());
        return new ViewHolder(itemView);

    }
public void AddData(int item1,String item2,String item3,String item4,String item5,String item6)
{
    databaseHelper.addData(item1,item2,item3,item4,item5,item6);

}
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
         Movie movie = movies.get(position);
         AddData(movie.id,movie.title,movie.backdropPath,movie.overview,movie.runtime+"",""+movie.genres);
        String fullImageUrl = getFullImageUrl(movie);
        if (!fullImageUrl.isEmpty()) {
            Glide.with(activity)
                    .load(fullImageUrl)
                    .apply(RequestOptions.centerCropTransform())
                    .transition(withCrossFade())
                    .into(holder.imageView);
        }

        String popularity = getPopularityString(movie.popularity);
        holder.popularityTextView.setText(popularity);
        holder.titleTextView.setText(movie.title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    itemClickListener.onItemClick(movie.id, movie.title);
                }
                else
                {
                    itemClickListener.onItemClick(holder.getAdapterPosition(),movie.title);
                }

            }
        });
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

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void clear() {
        movies.clear();
    }

    public void addAll(List<Movie> movies) {
        this.movies.addAll(movies);
    }

    public void setImages(Images images) {
        this.images = images;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.popularityTextView)
        TextView popularityTextView;
        @BindView(R.id.titleTextView)
        TextView titleTextView;

        ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, itemView);
        }

    }

    private String getPopularityString(float popularity) {
        java.text.DecimalFormat decimalFormat = new java.text.DecimalFormat("#.#");
        return decimalFormat.format(popularity);
    }

    public interface ItemClickListener {

        void onItemClick(int movieId, String title);

    }

}