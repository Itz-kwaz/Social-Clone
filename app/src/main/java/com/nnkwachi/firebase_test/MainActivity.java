package com.nnkwachi.firebase_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements IMainActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private  void  init(){
        Login loginFragment = new Login();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content_frame,loginFragment,getString(R.string.login_fragment_tag));
        transaction.addToBackStack(getString(R.string.login_fragment_tag));
        transaction.commit();
    }

    @Override
    public void inflateRegisterFragment() {
        Register RegisterFragment = new Register();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content_frame,RegisterFragment,getString(R.string.register_fragment_tag));
        transaction.addToBackStack(getString(R.string.register_fragment_tag));
        transaction.commit();
    }
}
