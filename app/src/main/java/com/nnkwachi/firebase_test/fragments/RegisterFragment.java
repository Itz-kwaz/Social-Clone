package com.nnkwachi.firebase_test.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nnkwachi.firebase_test.R;
import com.nnkwachi.firebase_test.SharedViewModel;
import com.nnkwachi.firebase_test.iMainActivity;
import com.nnkwachi.firebase_test.models.User;


public class RegisterFragment extends Fragment {

    private static final String TAG = "Register";

    private FirebaseAuth mAuth;

    private com.nnkwachi.firebase_test.iMainActivity iMainActivity;
    private DatabaseReference myRef;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iMainActivity = (iMainActivity) context;
    }

    //Views
    private Button registerButton;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private EditText name;
    private ProgressBar progressBar;
    private SharedViewModel model;


    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mAuth = FirebaseAuth.getInstance();
        registerButton = view.findViewById(R.id.register_button);
        email = view.findViewById(R.id.email_edit_tv);
        name = view.findViewById(R.id.name_edit_tv);
        password = view.findViewById(R.id.password_edit_text);
        confirmPassword = view.findViewById(R.id.confirm_password);
        progressBar = view.findViewById(R.id.progress);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();


        registerUser();
        return view;
    }

    private void registerUser() {
        Log.d(TAG, "registerUser: Registering User.");
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (validate()) {
                    hideSoftKeyboard();
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).
                            addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: Create User with email " + task.isSuccessful());
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        User newUser = new User(name.getText().toString(),"",email.getText().toString());
                                        if (user != null) {
                                            myRef.child("users").child(user.getUid()).setValue(newUser);
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

                                        mAuth.signOut();
                                        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                                        model.select(true);
                                        iMainActivity.sendUserDetails(email.getText().toString(),password.getText().toString());
                                        NavHostFragment.findNavController(RegisterFragment.this).
                                                navigate(R.id.action_register_to_login);

                                    } else {
                                        Log.d(TAG, "onComplete: Failed to create user.");
                                        Toast.makeText(getActivity(), "Unable to register", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    //This method returns true only if the email is valid,the passwords match and no field is empty.
    private boolean validate() {
        Log.d(TAG, "validate: Validating Fields.");
        if (!name.getText().toString().isEmpty() && !password.getText().toString().isEmpty() &&
                !email.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty()) {

            if (password.getText().toString().equals(confirmPassword.getText().toString())) {

                if (email.getText().toString().contains("@")) {
                    return true;
                } else {
                    Toast.makeText(getActivity(), "Invalid Email", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                Toast.makeText(getActivity(), "Passwords do not Match", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {

            Toast.makeText(getActivity(), "Please fill empty fields", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    private void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


}

