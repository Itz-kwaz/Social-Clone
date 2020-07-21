package com.nnkwachi.firebase_test;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Login extends Fragment {


    private static final String TAG = "Login";
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //Views
    private TextView resendEmail;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private TextInputEditText password;
    private EditText email;
    private Button signInButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        email = view.findViewById(R.id.email_edit_tv);
        resendEmail = view.findViewById(R.id.resend_email);
        password = view.findViewById(R.id.etPassword);
        signInButton = view.findViewById(R.id.sign_in_button);
        progressBar = view.findViewById(R.id.progress);
        errorMessage = view.findViewById(R.id.error_message);
        mAuth = FirebaseAuth.getInstance();

        TextView register = view.findViewById(R.id.register_tv);

        assert getArguments() != null;
        Log.d(TAG, "onCreateView: "+ getArguments().getString("email"));


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Navigation.findNavController(view).navigate(R.id.action_login_to_register);
            }
        });


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if(validate()){
                    progressBar.setVisibility(View.VISIBLE);
                    hideSoftKeyboard();
                    mAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if(task.isSuccessful()){
                                        Log.d(TAG, "onComplete: "+ mAuth.getCurrentUser().getUid());
                                    }else {
                                        errorMessage.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                    );
                }
            }
        });

        resendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String email = LoginArgs.fromBundle(getArguments().)
                assert getArguments() != null;
                String email = getArguments().getString("email");
                String password = getArguments().getString("password");
                Log.d(TAG, "onClick: "+ email + " " + password);

                if(email != null && password != null){
                    AuthCredential authCredential = EmailAuthProvider.getCredential(email,password);

                    mAuth.signInWithCredential(authCredential).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        if(user != null){
                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getActivity(), "Sent Verification Email", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getActivity(), "Couldn't Send Verification Email", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                    );
                }
            }
        });

        allowButtonClick();
        setUpFirebaseAuth();

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedViewModel model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        model.getSelected().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        resendEmail.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    //This method checks if the email and password editText are empty'
    private void allowButtonClick(){

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                GradientDrawable gradientDrawable = (GradientDrawable) signInButton.getBackground().mutate();
                if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty())
                    gradientDrawable.setColor(Color.parseColor("#1E88E5"));
                else
                    gradientDrawable.setColor(Color.parseColor("#A19BCFE8"));

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    GradientDrawable gradientDrawable = (GradientDrawable) signInButton.getBackground().mutate();
                    gradientDrawable.setColor(Color.parseColor("#1E88E5"));
                }else{
                    GradientDrawable gradientDrawable = (GradientDrawable) signInButton.getBackground().mutate();
                    gradientDrawable.setColor(Color.parseColor("#A19BCFE8"));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //This method returns true only if the email is valid,the passwords match and no field is empty.
    private boolean validate() {
        Log.d(TAG, "validate: Validating Fields.");
        if (!password.getText().toString().isEmpty() && !email.getText().toString().isEmpty()) {

            if (email.getText().toString().contains("@")) {
                return true;
            } else {
                Toast.makeText(getActivity(), "Invalid Email", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {

            Toast.makeText(getActivity(), "Please fill empty fields", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /*
          ------------------- FireBase Setup ------------------
     */

    private void setUpFirebaseAuth(){
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser  firebaseUser = mAuth.getCurrentUser();

                if(firebaseUser != null){
                    Log.d(TAG, "onAuthStateChanged: "+ firebaseUser.getUid());
                    if(firebaseUser.isEmailVerified()){
                        Log.d(TAG, "onAuthStateChanged: Email Verified");
                        NavHostFragment.findNavController(Login.this).navigate(R.id.action_login_to_homeActivity);
                    }else {
                        Toast.makeText(getActivity(),"Check email for verification email.",Toast.LENGTH_SHORT).show();
                        resendEmail.setVisibility(View.VISIBLE);
                    }
                }else {
                    Log.d(TAG, "onAuthStateChanged: signed Out");
                }
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mAuthStateListener != null){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }


}
