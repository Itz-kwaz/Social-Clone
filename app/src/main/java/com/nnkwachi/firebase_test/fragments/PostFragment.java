package com.nnkwachi.firebase_test.fragments;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nnkwachi.firebase_test.R;
import com.nnkwachi.firebase_test.models.ChangePhotoDialog;
import com.nnkwachi.firebase_test.models.Post;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class PostFragment extends Fragment implements ChangePhotoDialog.OnPhotoReceivedListener {
    private static final int REQUEST_CODE = 300;
    private static final double MB_THRESHOLD = 5.0;
    private static final double MB = 1000000.0;
    private FirebaseAuth mAuth;
    private EditText postText;
    private ImageView send, photo;
    private ProgressBar progressBar;

    //vars
    private boolean mStoragePermissions;
    private Uri mSelectedImageUri;
    private Bitmap mSelectedImageBitmap;
    private byte[] mBytes;
    private long progress;

    private static final String TAG = "PostFragment";
    private DatabaseReference myRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_post, container, false);
        View view1 = inflater.inflate(R.layout.activity_home,container,false);
        progressBar = view.findViewById(R.id.progressBar);
        hideDialog();
        send = view.findViewById(R.id.send_image);
        postText = view.findViewById(R.id.write_post_edit_txt);
        photo = view.findViewById(R.id.photo);
        ImageView camera = view.findViewById(R.id.camera);

        BottomNavigationView bottomNavigationView = view1.findViewById(R.id.bottom_nav);
        bottomNavigationView.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));
        mStoragePermissions = true;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: camera");
                if(mStoragePermissions){
                    ChangePhotoDialog dialog = new ChangePhotoDialog(getActivity());
                    dialog.setTargetFragment(PostFragment.this,1);
                   dialog.show(getParentFragmentManager(),getString(R.string.dialog_tag));
                }else{
                    verifyStoragePermissions();
                }
//             startGallery();
            }
        });
       allowImageClick();
       sendPost();
        return  view;
    }

    private void sendPost() {

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                if(!postText.getText().toString().isEmpty()){
                    if(postText.getText().toString().length() < 200 ){

                        if(mSelectedImageUri != null){
                            uploadNewPhoto(mSelectedImageUri);
                        }else if(mSelectedImageBitmap  != null){
                            uploadNewPhoto(mSelectedImageBitmap);
                        }

                    }else
                        Toast.makeText(getActivity(),"Characters must be less than 200",Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(getActivity(),"Can't send empty message",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private void uploadNewPhoto(Uri imageUri){
        Log.d(TAG, "uploadNewPhoto: uploading new profile photo to firebase storage.");
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(imageUri);
    }


    private void uploadNewPhoto(Bitmap imageBitmap){

        Log.d(TAG, "uploadNewPhoto: uploading new profile photo to firebase storage.");

        //Only accept image sizes that are compressed to under 5MB. If thats not possible
        //then do not allow image to be uploaded
        BackgroundImageResize resize = new BackgroundImageResize(imageBitmap);
        Uri uri = null;
        resize.execute(uri);
    }

    @Override
    public void getImagePath(Uri imagePath) {
        if( !imagePath.toString().equals("")){
            mSelectedImageBitmap = null;
            mSelectedImageUri = imagePath;
            Log.d(TAG, "getImagePath: got the image uri: " + mSelectedImageUri);
            ImageLoader.getInstance().displayImage(imagePath.toString(), photo);

        }

    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        if(bitmap != null){
            mSelectedImageUri = null;
            mSelectedImageBitmap = bitmap;
            Log.d(TAG, "getImageBitmap: got the image bitmap: " + mSelectedImageBitmap);
            photo.setImageBitmap(bitmap);
        }
    }

    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {

        Bitmap mBitmap;
        BackgroundImageResize(Bitmap bm) {
            if(bm != null){
                mBitmap = bm;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();
            Toast.makeText(getActivity(), "compressing image", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected byte[] doInBackground(Uri... params ) {

            if(mBitmap == null)
            {
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), params[0]);
                    Log.d(TAG, "doInBackground: bitmap size: megabytes: " + mBitmap.getByteCount()/MB + " MB"); }
                catch (IOException e) {
                    Log.e(TAG, "doInBackground: IOException: ", e.getCause()); }
            }

            byte[] bytes = null;
            for (int i = 1; i < 11; i++){
                if(i == 10){
                    Toast.makeText(getActivity(), "That image is too large.", Toast.LENGTH_SHORT).show();
                    break;
                }
                bytes = getBytesFromBitmap(mBitmap,100/i);
                Log.d(TAG, "doInBackground: megabytes: (" + (11-i) + "0%) "  + bytes.length/MB + " MB");
                if(bytes.length/MB  < MB_THRESHOLD){
                    return bytes;
                }
            }
            return bytes;
        }


        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            hideDialog();
            mBytes = bytes;
            //execute the upload
            executeUploadTask();
        }
    }

    private void executeUploadTask(){
        showDialog();

        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child( "images/users/" + mAuth.getCurrentUser().getUid());

        if(mBytes.length/MB < MB_THRESHOLD) {
            UploadTask uploadTask;
            uploadTask = storageReference.putBytes(mBytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //Now insert the download url into the firebase database
                    Uri firebaseURL = taskSnapshot.getUploadSessionUri();

                    Calendar c = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());
                    String userID = "";
                    if(mAuth.getCurrentUser() != null){
                       userID = mAuth.getCurrentUser().getUid();
                    }

                    assert firebaseURL != null;
                    Post post = new Post(formattedDate ,"0",postText.getText().toString(),firebaseURL.toString(),userID);

                    Log.d(TAG, "onSuccess: firebase download url : " + firebaseURL.toString());
                   myRef.child(getString(R.string.node_posts)).child(mAuth.getCurrentUser().getUid()).push().setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               NavHostFragment.findNavController(PostFragment.this).navigate(R.id.homeFragment);
                               Toast.makeText(getActivity(), "Upload Success", Toast.LENGTH_SHORT).show();
                               hideDialog();
                           }else{
                               Toast.makeText(getActivity(), "Upload unsuccessful", Toast.LENGTH_SHORT).show();
                           }
                       }
                   });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getActivity(), "could not upload photo", Toast.LENGTH_SHORT).show();
                    hideDialog();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    long currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    if(currentProgress > (progress + 15)){
                        progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Log.d(TAG, "onProgress: Upload is " + progress + "% done");

                    }

                }
            });
        }else{
            Toast.makeText(getActivity(), "Image is too Large", Toast.LENGTH_SHORT).show();
        }

    }

    private void hideDialog() {
        progressBar.setVisibility(View.GONE);
    }

    private void showDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    // convert from bitmap to byte array
    public static byte[] getBytesFromBitmap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }


    private void verifyStoragePermissions(){
        Log.d(TAG, "verifyPermissions: asking user for permissions.");
        String[] permissions = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(getContext(),
                permissions[0] ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(),
                permissions[1] ) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getContext(),
                permissions[2] ) == PackageManager.PERMISSION_GRANTED)
                    {
                        mStoragePermissions = true;
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_CODE);
                    }
    }


    private void allowImageClick(){
        postText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                GradientDrawable gradientDrawable = (GradientDrawable) postText.getBackground().mutate();
                if(!postText.getText().toString().isEmpty()) {
                    send.setImageResource(R.drawable.send_clickable);
                    gradientDrawable.setStroke(2,Color.BLACK); }
                else {
                    send.setImageResource(R.drawable.send);
                    gradientDrawable.setStroke(2,Color.GRAY); }
            }

            @Override public void afterTextChanged(Editable editable) { }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult: requestCode: " + requestCode);
        switch(requestCode){
            case REQUEST_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "onRequestPermissionsResult: User has allowed permission to access: " + permissions[0]);

                }
                break;
        }
    }

}
