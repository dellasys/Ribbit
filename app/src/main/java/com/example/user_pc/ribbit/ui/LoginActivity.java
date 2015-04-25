package com.example.user_pc.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.user_pc.ribbit.R;
import com.example.user_pc.ribbit.RibbitApplication;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class LoginActivity extends ActionBarActivity {

   protected TextView mSignUpTextView;

    @InjectView(R.id.loginButton) Button mLoginButton;
    @InjectView(R.id.passwordField) EditText mPassword;
    @InjectView(R.id.usernameField) EditText mUsername;
    @InjectView(R.id.loginProgressBar) ProgressBar mLoginProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar(); actionBar.hide();
       // ActionBar actionBar = getSupportActionBar();
       // actionBar.hide();

        ButterKnife.inject(this);

        mSignUpTextView = (TextView)findViewById(R.id.signUpText);
        mSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();


                username = username.trim();
                password = password.trim();


                if (username.isEmpty() || password.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage(R.string.login_error_message)
                            .setTitle(R.string.login_error_title)
                            .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else{
                   //Login
                   // setProgressBarIndeterminateVisibility(true);
                    mLoginProgressBar.setVisibility(view.VISIBLE);
                    ParseUser.logInInBackground(username,password,new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                           // setProgressBarIndeterminateVisibility(false);
                            mLoginProgressBar.setVisibility(view.VISIBLE);
                            if (e == null){
                                //Success!
                                RibbitApplication.updateParseInstallation(ParseUser.getCurrentUser());

                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.login_error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                mLoginProgressBar.setVisibility(view.INVISIBLE);
                            }

                        }
                    });
                }
            }
        });

    }


/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
