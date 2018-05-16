package christopher.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import christopher.popularmovies.Model.Movie;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.NumberViewHolder> {



private Context mContext;


        private List<Movie> movieList;

    public MovieAdapter(Context mContext, List<Movie> movieList) {

        this.mContext = mContext;
        this.movieList = movieList;
    }



        @Override
        public MovieAdapter.NumberViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            Context context = viewGroup.getContext();
            int layoutIdForListItem = R.layout.list_item;
            LayoutInflater inflater = LayoutInflater.from(context);
            boolean shouldAttachToParentImmediately = false;

            View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
            NumberViewHolder viewHolder = new NumberViewHolder(view);


            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final MovieAdapter.NumberViewHolder holder, int position) {
            holder.title.setText(movieList.get(position).getTitle());
            String rating = Double.toString(movieList.get(position).getVoteAverage());
holder.rating.setText(rating);

        String poster = "https://image.tmdb.org/t/p/w500" + movieList.get(position);


            Glide.with(mContext)
                    .load(movieList.get(position).getPosterPath())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.posterthumbnail);
        }


        @Override
        public int getItemCount() {
            return  movieList.size();
        }


        class NumberViewHolder extends RecyclerView.ViewHolder
                {

            // Will display the position in the list, ie 0 through getItemCount() - 1

          TextView title;
          TextView rating;
           ImageView posterthumbnail;



            public NumberViewHolder(View itemView) {
                super(itemView);
title = (TextView) itemView.findViewById(R.id.movie_title);
rating = (TextView) itemView.findViewById(R.id.rating);
                posterthumbnail = (ImageView) itemView.findViewById(R.id.tv_item_number);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int clickedPosition = getAdapterPosition();
                        Movie clickedItem =  movieList.get(clickedPosition);

                        Intent intent = new Intent(mContext, DetailActivity.class);
mContext.startActivity(intent);
                        Toast.makeText(view.getContext(), "You clicked " + clickedPosition, Toast.LENGTH_LONG).show();

                    }
                });
            }

        }
    }
