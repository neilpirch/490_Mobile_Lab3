package com.neilpirch.socialloginapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.widget.ProfilePictureView;

import org.json.JSONException;

public class LoggedInActivity extends AppCompatActivity {

    String id;
    String email;
    String name;
    String gender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EditText editTextUser = findViewById(R.id.txt_username);
        EditText editTextEmail = findViewById(R.id.txt_email);
        EditText editTextGender = findViewById(R.id.txt_gender);



        GraphRequest request = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        try {
                            id = response.getJSONObject().getString("id");
                            name = response.getJSONObject().getString("name");
                            email = response.getJSONObject().getString("email");
                            gender = response.getJSONObject().getString("gender");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        );

        editTextUser.setText(name);
        editTextEmail.setText(email);
        editTextGender.setText(gender);


        ProfilePictureView profilePictureView;

        profilePictureView = (ProfilePictureView) findViewById(R.id.friendProfilePicture);

        profilePictureView.setProfileId(id);

        setContentView(R.layout.activity_logged_in);
    }


}
