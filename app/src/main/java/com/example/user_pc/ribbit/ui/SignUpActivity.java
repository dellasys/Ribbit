package com.example.user_pc.ribbit.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.user_pc.ribbit.R;
import com.example.user_pc.ribbit.RibbitApplication;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class SignUpActivity extends ActionBarActivity {


    @InjectView(R.id.signUpButton) Button mSignUpButton;
    @InjectView(R.id.emailField) EditText mEmail;
    @InjectView(R.id.passwordField) EditText mPassword;
    @InjectView(R.id.usernameField) EditText mUsername;
    @InjectView(R.id.signUpProgressBar) ProgressBar signUpProgressBar;
    @InjectView(R.id.cancelButton) Button mCancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        ButterKnife.inject(this);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                String email = mEmail.getText().toString();

                username = username.trim();
                password = password.trim();
                email = email.trim();

                if (username.isEmpty() || password.isEmpty() || email.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setMessage(R.string.signup_error_message)
                    .setTitle(R.string.signup_error_title)
                    .setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else{
                    //create the new user!
                    ParseUser newUser = new ParseUser();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmail(email);
                    signUpProgressBar.setVisibility(view.VISIBLE);
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            signUpProgressBar.setVisibility(view.VISIBLE);
                            if (e == null){
                                //Success !
                                RibbitApplication.updateParseInstallation(ParseUser.getCurrentUser());

                                Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                                builder.setMessage(e.getMessage())
                                        .setTitle(R.string.signup_error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                signUpProgressBar.setVisibility(view.INVISIBLE);
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
