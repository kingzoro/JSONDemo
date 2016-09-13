package com.example.kingz.parsejson.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.kingz.parsejson.R;
import com.example.kingz.parsejson.models.MovieModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by kingz on 9/13/2016.
 */
public class MovieAdapter extends ArrayAdapter{
    public List<MovieModel> movieModelList;
    private int resource;
    private Context mContext;
    private LayoutInflater inflater;

    public MovieAdapter(Context context, int resource, List<MovieModel> objects) {
        super(context, resource, objects);
        movieModelList = objects;
        this.resource = resource;
        mContext = context;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ViewHolfer holfer = null;

        if (convertView == null){
            holfer = new ViewHolfer();
            convertView = inflater.inflate(resource, null);
            holfer.ivMovieIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            holfer.tvMovie = (TextView) convertView.findViewById(R.id.txtMovieName);
            holfer.tvTagline = (TextView) convertView.findViewById(R.id.txtTagline);
            holfer.tvYear = (TextView) convertView.findViewById(R.id.txtYear);
            holfer.tvDuration = (TextView) convertView.findViewById(R.id.txtDuration);
            holfer.tvDirector = (TextView) convertView.findViewById(R.id.txtDirector);
            holfer.rbMovieRating = (RatingBar) convertView.findViewById(R.id.rbMovie);
            holfer.tvCast = (TextView) convertView.findViewById(R.id.txtCast);
            holfer.tvStory = (TextView) convertView.findViewById(R.id.txtStory);
            convertView.setTag(holfer);
        }else {
            holfer = (ViewHolfer) convertView.getTag();
        }




        final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

        ImageLoader.getInstance().displayImage(movieModelList.get(position).getImage(), holfer.ivMovieIcon, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                progressBar.setVisibility(View.GONE);
            }
        });

        holfer.tvMovie.setText(movieModelList.get(position).getMovie());
        holfer.tvTagline.setText(movieModelList.get(position).getTagline());
        holfer.tvYear.setText("Year: "+String.valueOf(movieModelList.get(position).getYear()));
        holfer.tvDuration.setText("Duration: "+movieModelList.get(position).getDuration());
        holfer.tvDirector.setText("Director: "+movieModelList.get(position).getDirector());

        holfer.rbMovieRating.setRating(movieModelList.get(position).getRating()/2);

        StringBuilder stringBuilder = new StringBuilder();
        for (MovieModel.Cast cast : movieModelList.get(position).getCastList()){
            stringBuilder.append(cast.getName()+", ");
        }
        holfer.tvCast.setText("Cast: "+stringBuilder);

        holfer.tvStory.setText(movieModelList.get(position).getStory());

        return convertView;
    }

    class ViewHolfer{
        private ImageView ivMovieIcon;
        private TextView tvMovie;
        private TextView tvTagline;
        private TextView tvYear;
        private TextView tvDuration;
        private TextView tvDirector;
        private RatingBar rbMovieRating;
        private TextView tvCast;
        private TextView tvStory;

    }
}
