package com.nnkwachi.firebase_test;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;


public class Login extends Fragment {


/*    private IMainActivity mInterface;*/
    private TextInputEditText password;
    private EditText email;
    private Button signInButton;


    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mInterface = (IMainActivity) getActivity();
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Login login = new Login();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        TextView register = view.findViewById(R.id.register_tv);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_login_to_register);
            }
        });

        email = view.findViewById(R.id.email_edit_tv);
        password = view.findViewById(R.id.etPassword);
        signInButton = view.findViewById(R.id.sign_in_button);



        allowButtonClick();

        return view;
    }

    //This method checks if the email and password editText are empty'
    private void allowButtonClick(){

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
                    GradientDrawable gradientDrawable = (GradientDrawable) signInButton.getBackground().mutate();
                    gradientDrawable.setColor(Color.BLUE);
                }else{
                    GradientDrawable gradientDrawable = (GradientDrawable) signInButton.getBackground().mutate();
                    gradientDrawable.setColor(Color.GRAY);
                }
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
                    gradientDrawable.setColor(Color.BLUE);
                }else{
                    GradientDrawable gradientDrawable = (GradientDrawable) signInButton.getBackground().mutate();
                    gradientDrawable.setColor(Color.GRAY);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }
}
