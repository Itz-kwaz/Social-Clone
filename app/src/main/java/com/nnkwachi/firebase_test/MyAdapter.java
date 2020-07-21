package com.nnkwachi.firebase_test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nnkwachi.firebase_test.models.Post;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private static final String TAG = "MyAdapter";

    //vars
    private static ArrayList<Post> mPosts;
    private Context mContext;

    public MyAdapter(Context context,ArrayList<Post> posts) {
        mPosts = posts;
        mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        String likesCount = "" + mPosts.get(position).getLike_count() +" likes";
        holder.description.setText(mPosts.get(position).getDescription());
        holder.likesCount.setText(likesCount);
        formatDate(mPosts.get(position).getTime_of_post());
        holder.timeOfPost.setText(mPosts.get(position).getTime_of_post());
    }

    private void formatDate(String time_of_post)
    {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        Calendar currentTime = Calendar.getInstance();
        if(time_of_post != null) {
            try {
                calendar.setTime(df.parse(time_of_post));

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }


    public static class  MyViewHolder extends RecyclerView.ViewHolder{
         CircleImageView profilePhoto;
         TextView userName;
         TextView timeOfPost;
         LikeButton likeButton;
         TextView likesCount;
         ImageView postPhoto;
         TextView description;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.profile_photo);
            userName = itemView.findViewById(R.id.user_name);
            timeOfPost = itemView.findViewById(R.id.time_of_post);
            likeButton = itemView.findViewById(R.id.heart_button);
            likesCount = itemView.findViewById(R.id.likes_count);
            postPhoto = itemView.findViewById(R.id.post_photo);
            description = itemView.findViewById(R.id.description);

            likeButton.setOnLikeListener(new OnLikeListener()
            {
                @Override
                public void liked(LikeButton likeButton)
                {
                    int like = Integer.parseInt(mPosts.get(getAdapterPosition()).getLike_count()) + 1;
                    mPosts.get(getAdapterPosition()).setLike_count(String.valueOf(like));

                    String concatString = mPosts.get(getAdapterPosition()).getLike_count() + " likes";
                    likesCount.setText(concatString);
                }

                @Override
                public void unLiked(LikeButton likeButton)
                {
                    Log.d(TAG, "unLiked: ");

                    int like =Integer.parseInt(mPosts.get(getAdapterPosition()).getLike_count()) - 1 ;
                    mPosts.get(getAdapterPosition()).setLike_count(String.valueOf(like));

                    String concatString = mPosts.get(getAdapterPosition()).getLike_count() +" likes";
                    likesCount.setText(concatString);
                }
            });

        }

    }
}


