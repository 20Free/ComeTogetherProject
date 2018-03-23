package com.example.jonathan.cometogetherproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

public class RegisterPage1 extends AppCompatActivity {

    private EditText usernameText;
    private EditText passwordText;
    private EditText emailText;
    private EditText firstNameText;
    private EditText lastNameText;
    private EditText phoneNumberText;

    private Intent registerPage2Intent;

    /**
     * Page 1 of the registration initialization
     *
     * @param savedInstanceState the state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_page_1_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.title_activity_register));

        usernameText = (EditText) findViewById(R.id.username_text);
        passwordText = (EditText) findViewById(R.id.password_text);
        emailText = (EditText) findViewById(R.id.email_text);
        firstNameText = (EditText) findViewById(R.id.first_name_text);
        lastNameText = (EditText) findViewById(R.id.last_name_text);
        phoneNumberText = (EditText) findViewById(R.id.phone_number_text);

        usernameText.setError(getString(R.string.username_must_be_unique));
        passwordText.setError(getString(R.string.password_must_be_long_enough));

        registerPage2Intent = new Intent(this, RegisterPage2.class);

        Bundle extras = getIntent().getExtras();
        //displays error if user already exists in database
        if(extras != null) {
            if (extras.getString(getString(R.string.map_username_key)).equals(getString(R.string.username_equal))) {
                usernameText.setError(getString(R.string.username_already_exists_error));
                usernameText.requestFocus();
            }
        }
    }

    /**
     * Grabs the texts in the TextViews and moves them on to the
     * next activity if the password is valid.
     *
     * @param view the fab clicked.
     */
    public void saveAndRegisterPage2(View view){
        String username = usernameText.getText().toString();
        String password = passwordText.getText().toString();
        String email = emailText.getText().toString();
        String firstName = firstNameText.getText().toString();
        String lastName = lastNameText.getText().toString();
        String phoneNumber = phoneNumberText.getText().toString();


        if(isPasswordValid(password)) {
            registerPage2Intent.putExtra(getString(R.string.map_username_key), username);
            registerPage2Intent.putExtra(getString(R.string.map_password_key), password);
            registerPage2Intent.putExtra(getString(R.string.map_email_key), email);
            registerPage2Intent.putExtra(getString(R.string.map_first_name_key), firstName);
            registerPage2Intent.putExtra(getString(R.string.map_last_name_key), lastName);
            registerPage2Intent.putExtra(getString(R.string.map_phone_number_key), phoneNumber);
            startActivity(registerPage2Intent);
        } else {
            passwordText.setError(getString(R.string.password_invalid_error));
            passwordText.requestFocus();
        }
    }

    /**
     * Checks to see if the password is long enough.
     *
     * @param password the password entered
     * @return true, if the password is long enough
     *         false, if the password is too short
     */
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}