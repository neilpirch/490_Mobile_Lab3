package com.neilpirch.socialloginapp;

import com.neilpirch.socialloginapp.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.concurrent.Callable;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    // UI references.
    private SignInButton myGoogleSignInButton;
    private LoginButton myFacebookSignInButton;
    private TwitterLoginButton myTwitterSignInButton;

    // Vars
    private GoogleApiClient myGoogleApiClient;

    private CallbackManager myFacebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        myFacebookCallbackManager = CallbackManager.Factory.create();

        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        myFacebookSignInButton = (LoginButton)findViewById(R.id.facebook_login_button);
        myFacebookSignInButton.registerCallback(myFacebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        handleSignInResult(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                LoginManager.getInstance().logOut();
                                return null;
                            }
                        });
                    }

                    @Override
                    public void onCancel() {
                        handleSignInResult(null);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d(LoginActivity.class.getCanonicalName(), error.getMessage());
                        handleSignInResult(null);
                    }
                }
        );
        myGoogleSignInButton = (SignInButton)findViewById(R.id.google_sign_in_button);
        myGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        TwitterAuthConfig authConfig = new TwitterAuthConfig("Ag0J7HdOaIEnNJVvTpxRRyI14",
                "rpGC6PtxLCIuzOpUP4qnkI7EWMM4cBbzdlL5bPNPSbCSoJjk1M");
        Fabric.with(this, new TwitterCore(authConfig));

        myTwitterSignInButton = (TwitterLoginButton)findViewById(R.id.twitter_sign_in_button);
        myTwitterSignInButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(final Result<TwitterSession> result) {
                // handleSignInResult(...);
            }

            @Override
            public void failure(TwitterException e) {
                // handleSignInResult(...);
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if(result.isSuccess()) {
                final GoogleApiClient client = myGoogleApiClient;

                handleSignInResult(new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        if (client != null) {

                            Auth.GoogleSignInApi.signOut(client).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                            Log.d(LoginActivity.class.getCanonicalName(),
                                                    status.getStatusMessage());
                                        }
                                    }
                            );
                        }
                        return null;
                    }
                });
            }else {
                handleSignInResult(null);
            }
        } else if(TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE == requestCode) {
            myTwitterSignInButton.onActivityResult(requestCode, resultCode, data);
        } else {
            myFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }
    private void handleSignInResult(Callable<Void> logout) {
        if(logout == null) {
            /* Login error */
            Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
        } else {
            /* Login success */
            Application.getInstance().setLogoutCallable(logout);
            //result.getSignInAccount();
            startActivity(new Intent(this, LoggedInActivity.class));
        }
    }
    private void signInWithGoogle() {
        if(myGoogleApiClient != null) {
            myGoogleApiClient.disconnect();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        myGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(myGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
