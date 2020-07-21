package com.nnkwachi.firebase_test.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nnkwachi.firebase_test.MyAdapter;
import com.nnkwachi.firebase_test.R;
import com.nnkwachi.firebase_test.models.Post;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class HomeFragment extends Fragment {


    private RecyclerView mRecyclerView;
    private ArrayList<Post> postsList = new ArrayList<>();
    private static final String TAG = "HomeFragment";
    private MyAdapter myAdapter;
    private Query query;
    private ChildEventListener eventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);


        TextView writeSomethingTv = view.findViewById(R.id.textView2);
        writeSomethingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: TextView");
               /* Intent intent = new Intent(getActivity(),SettingsActivity.class);
                startActivity(intent);*/
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_postFragment);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = view.findViewById(R.id.recyler_view);
        myAdapter = new MyAdapter(getActivity(), postsList);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(myAdapter);
        getUserData();

        return  view;
    }

    private void getUserData(){
        Log.d(TAG, "getUserData: Getting the firebase data");
        eventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post post = dataSnapshot.getValue(Post.class);
                assert post != null;
                post.setId(dataSnapshot.getKey());
                postsList.add(post);
                Log.d(TAG, "onDataChange: Post ID:" + post.getId());
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        query = reference.child(getString(R.string.node_posts)).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByKey();

        query.addChildEventListener(eventListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(eventListener != null)
            query.removeEventListener(eventListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        query.removeEventListener(eventListener);
    }
}
