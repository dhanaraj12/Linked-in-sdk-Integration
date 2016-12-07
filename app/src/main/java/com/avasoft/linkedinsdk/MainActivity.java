package com.avasoft.linkedinsdk;

import android.content.Intent;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    //replace package string with your package string
    public static final String PACKAGE = "com.avasoft.linkedinsdk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generateHashkey();
    }

    public void generateHashkey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    PACKAGE,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                TextView hashkey=(TextView) findViewById(R.id.hashKey);
           (hashkey).setText(Base64.encodeToString(md.digest(),
                                Base64.NO_WRAP));
              System.out.print(hashkey.getText().toString());
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("Name not found", e.getMessage(), e);

        } catch (NoSuchAlgorithmException e) {
            Log.d("Error", e.getMessage(), e);
        }
    }
    public void login(View view){
        LISessionManager.getInstance(getApplicationContext())
                .init(this, buildScope(), new AuthListener() {
                    @Override
                    public void onAuthSuccess() {

                        Toast.makeText(getApplicationContext(), "success" +
                                        LISessionManager
                                                .getInstance(getApplicationContext())
                                                .getSession().getAccessToken().toString(),
                                Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onAuthError(LIAuthError error) {

                        Toast.makeText(getApplicationContext(), "failed "
                                        + error.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }, true);
    }
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        LISessionManager.getInstance(getApplicationContext())
                .onActivityResult(this,
                        requestCode, resultCode, data);

        Intent intent = new Intent(MainActivity.this, HomePage.class);
        startActivity(intent);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);

    }
}
