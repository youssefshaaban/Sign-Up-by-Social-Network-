package com.snconnection;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.provider.ContactsContract;
import android.support.annotation.RestrictTo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView imageprofile;
    TextView textViewdetail;
    ImageView imageView;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialControls();
    }


    public void initialControls(){
        imageView=(ImageView)findViewById(R.id.linkedIn);
        imageView.setOnClickListener(this);
        button=(Button)findViewById(R.id.logoutbtn);
        button.setOnClickListener(this);
        imageprofile=(ImageView)findViewById(R.id.imageprofile);
        textViewdetail=(TextView)findViewById(R.id.txtDetail);
        imageView.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);
        imageprofile.setVisibility(View.GONE);
        textViewdetail.setVisibility(View.GONE);
        computePakageHash();
    }
    private void computePakageHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.snconnection",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.e("TAG",e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.linkedIn:
                handleLogin();
                break;
            case R.id.logoutbtn:
                handleLogout();
                break;
        }
    }
    private static Scope buildScope(){
        return Scope.build(Scope.R_BASICPROFILE,Scope.W_SHARE,Scope.R_EMAILADDRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Add this line to your existing onActivityResult() method
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
    }

    private void handleLogout(){
        LISessionManager.getInstance(getApplicationContext()).clearSession();
        imageView.setVisibility(View.VISIBLE);
        button.setVisibility(View.GONE);
        imageprofile.setVisibility(View.GONE);
        textViewdetail.setVisibility(View.GONE);
    }
    private void handleLogin(){
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
                imageView.setVisibility(View.GONE);
                button.setVisibility(View.VISIBLE);
                imageprofile.setVisibility(View.VISIBLE);
                textViewdetail.setVisibility(View.VISIBLE);
                fetchPersonalInfo();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
                Log.e("NIKHIL",error.toString());
            }
        }, true);
    }



    private void fetchPersonalInfo(){
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,public-profile-url,picture-url,email-address,picture-urls::(original))";
        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                // Success!
                try {
                    JSONObject jsonObject = apiResponse.getResponseDataAsJson();
                    SocialPerson socialPerson=new SocialPerson(jsonObject);
                    Picasso.with(getApplicationContext()).load(socialPerson.getAvatarURL()).into(imageprofile);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Name: "+socialPerson.getName());
                    sb.append("\n\n");
                    sb.append("Email: "+socialPerson.getEmail());
                    textViewdetail.setText(sb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onApiError(LIApiError liApiError) {
                // Error making GET request!
                Log.e("NIKHIL",liApiError.getMessage());
            }
        });
    }
}
