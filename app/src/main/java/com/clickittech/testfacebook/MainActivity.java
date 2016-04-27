package com.clickittech.testfacebook;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity  {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    /*Twitter*/
    private static final String TWITTER_KEY = "FjmyHBKStmqwU2SW1xa0ImsAj";
    private static final String TWITTER_SECRET = "xvtJSd32LrgE9QGRhRPUG1b8YAEvPcs9i31k7lVol5u5tIaaxN ";
    private TwitterLoginButton loginButtonTwitter;


    /*Facebook*/
    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;

    /*Linkedin*/
    private Button login_button_linkedin;


    /*
    * Variables de google +
    * */

    /*GoogleApiClient googleApiClient;
    GoogleApiAvailability googleApiAvailability;
    SignInButton btnSing;
    private Button button_revoke,button_logout;
    private TextView textView_name,textView_email;
    private RelativeLayout profil_layout;
    private ImageView imageView_profile_image;
    boolean mIntentInProgress;
    boolean mSingleInClicked;
    private ConnectionResult mConnectionResult;
    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 120;
    private int requestCode;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;
    ProgressDialog progressDialog;
    GoogleSignInOptions gso;
    private Object profileInfo;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        /*Facebook*/
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        /*Google plus*/
        /*gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        buidNewGoogleApiClient();
        */
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /*Facebook*/
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("TAG","Success" + loginResult);
                info.setText("User ID:" + loginResult.getAccessToken().getUserId() + "\n" + "Aut token:" + loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceld.");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("TAG","error" + error);
                info.setText("Login attempt fail");
            }
        });

        /*try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.clickittech.testfacebook",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.clickittech.testfacebook",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());

                Log.d("KeyHash: ",Base64.encodeToString(md.digest(),
                        Base64.NO_WRAP));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("TAG", e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            Log.d("TAG", e.getMessage(), e);
        }
        /*Twitter*/
        loginButtonTwitter = (TwitterLoginButton)findViewById(R.id.twitter_login_button);
        loginButtonTwitter.setCallback(new LoginHandler());

        /*Linkedin*/
        login_button_linkedin = (Button)findViewById(R.id.login_button_linkedin);
        login_button_linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_linkedin();
            }
        });

        /*Google plus
        custimizeSignBtn();
        setBtnClickListeners();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing in...");*/

    }

    /*
      Login de linkedin test
    * */
    private void login_linkedin() {
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                info.setText(LISessionManager.getInstance(getApplicationContext()).getSession().getAccessToken().toString());
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Toast.makeText(getApplicationContext(),"Faild" + error.toString(),Toast.LENGTH_LONG).show();
            }
        },true);
    }

    private Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE,Scope.R_EMAILADDRESS);
    }


    /*
    * create and initialize GoogleApiClient object to use Google Plus Api.
    * While initializing the GoogleApiClient object, request the Plus.SCOPE_PLUS_LOGIN scope.

    private void buidNewGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
    }*/

    /*
      Customize sign-in button. The sign-in button can be displayed in
      multiple sizes and color schemes. It can also be contextually
      rendered based on the requested scopes. For example. a red button may
      be displayed when Google+ scopes are requested, but a white button
      may be displayed when only basic profile is requested. Try adding the
      Plus.SCOPE_PLUS_LOGIN scope to see the  difference.

    private void custimizeSignBtn() {
        btnSing = (SignInButton)findViewById(R.id.btn_sig_in);
        btnSing.setSize(SignInButton.SIZE_STANDARD);
        button_logout = (Button)findViewById(R.id.button_logout);
        button_revoke = (Button)findViewById(R.id.button_revoke);
    }*/

    /*
      Set on click Listeners on the sign-in sign-out and disconnect buttons

    private void setBtnClickListeners() {
        btnSing.setOnClickListener(this);
        button_logout.setOnClickListener(this);
        button_revoke.setOnClickListener(this);
    }

    protected void onStart(){
        super.onStart();
        googleApiClient.connect();
    }

    protected void onStop(){
        super.onStop();
        if (googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }

    protected void onResume(){
        super.onResume();
        if (googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
    }

    /*@Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()){
            googleApiAvailability.getErrorDialog(this,connectionResult.getErrorCode(),requestCode).show();
            return;
        }

        if (!is_intent_inprogress){
            mConnectionResult = connectionResult;

            if (is_signInBtn_clicked)
            {
                resolveSigInError();
            }
        }
    }*/


    /*
      Will receive the activity result and check which request we are responding to
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*Facebook result*/
        callbackManager.onActivityResult(requestCode, resultCode, data);
        /*Twitter result*/
        loginButtonTwitter.onActivityResult(requestCode, resultCode, data);
        /*Linkedin result*/
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this,requestCode,resultCode,data);
        /*if (requestCode == SIGN_IN_CODE)
        {
            this.requestCode = requestCode;
            if (requestCode != RESULT_OK)
            {
                is_signInBtn_clicked = false;
            }
            is_intent_inprogress = false;
            if (!googleApiClient.isConnecting()){
                googleApiClient.connect();
            }
        }*/
    }

    /*Twitter*/
    private class LoginHandler extends Callback<TwitterSession> {
        @Override
        public void success(Result<TwitterSession> twitterSessionResult) {
            String output = "Status: " +
                    "Your login was successful " +
                    twitterSessionResult.data.getUserName() +
                    "\nAuth Token Received: " +
                    twitterSessionResult.data.getAuthToken().token;

            info.setText(output);
        }

        @Override
        public void failure(TwitterException e) {
            info.setText("Status: Login Failed");
        }
    }
    /*
    @Override
    public void onConnected(Bundle bundle) {
        is_signInBtn_clicked = false;
        //getProfileInfo();
        changeUI(true);
    }


    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
        changeUI(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sig_in:
                gPlusSignIn();
                break;
            case R.id.button_logout:
                gPlusSignOut();
                break;
            case R.id.button_revoke:
                gPlusRevokeAccess();
                break;
        }
    }*/

    /*
      Sign-in into the Google + account

    private void gPlusSignIn() {
        if (!googleApiClient.isConnecting()){
            is_signInBtn_clicked = true;
            resolveSigInError();
        }
    }*/

    /*
      Method to resolve any sign in errors

    private void resolveSigInError() {
        if (mConnectionResult.hasResolution())
        {
            try {
                is_intent_inprogress = true;
                mConnectionResult.startResolutionForResult(this,SIGN_IN_CODE);
            }catch (IntentSender.SendIntentException e){
                is_intent_inprogress = false;
                googleApiClient.connect();
            }
        }
    }*/

    /*
      Sign-out from Google+ account

    private void gPlusSignOut() {
        if (googleApiClient.isConnected())
        {
            googleApiClient.disconnect();
            googleApiClient.connect();
            changeUI(false);
        }
    }*/

    /*
     Revoking access from Google+ account

    private void gPlusRevokeAccess() {
        if (googleApiClient.isConnected())
        {
        }
    }*/

    /*private void getProfileInfo() {
        try {

            if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
                setPersonalInfo(currentPerson);

            } else {
                Toast.makeText(getApplicationContext(),
                        "No Personal info mention", Toast.LENGTH_LONG).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    /*private void changeUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.btn_sig_in).setVisibility(View.GONE);
            findViewById(R.id.profile_layout).setVisibility(View.VISIBLE);
        } else {

            findViewById(R.id.btn_sig_in).setVisibility(View.VISIBLE);
            findViewById(R.id.profile_layout).setVisibility(View.GONE);
        }
    }*/
}
