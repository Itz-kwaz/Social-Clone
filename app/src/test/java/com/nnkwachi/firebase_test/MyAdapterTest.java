package com.nnkwachi.firebase_test;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.nnkwachi.firebase_test.models.Post;

import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;

public class MyAdapterTest {

    private List<Post> postList;

    @Test
    public void getItemCount() throws Exception {
        int count = 0;
        for (Post post : postList) {
            count += 1;
        };
        assertEquals(postList.size(), count);
    }

  /*  @Before
    public void populateList() throws Exception{
        postList = new ArrayList<>();
        postList.add(new Post("hello",2,"well","Skepta"));
    }*/

}

