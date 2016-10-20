/*
 * Copyright (c) 2016, Gayan Weerakutti <gayan@linuxdeveloper.space>
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package space.linuxdeveloper.osum;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import space.linuxdeveloper.osum.view.AnimatedBackground;
import space.linuxdeveloper.osumlogin.LoginTask;
import space.linuxdeveloper.osumlogin.NetworkManager;
import space.linuxdeveloper.osumlogin.OnLoginListener;
import timber.log.Timber;


public class LoginActivity extends AppCompatActivity {

    // views
    private EditText mUsernameField;
    private EditText mPasswordField;
    private ImageView mCaptcha;
    private EditText mCaptchaField;

    private ImageButton mLoginButton;
    private AnimatedBackground mAnimatedBackground;

    public static WebView mWebView;
    public static AppSavedData sAppSavedData;
    public static LoginTask sLoginTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initVars();
        setupTimber();
        getViews();
        loadCredentials();

        sLoginTask = new LoginTask(this, mWebView, new OnLoginListener() {
            @Override
            public void onLoadCaptcha(Bitmap bitmap) {
                mCaptcha.setImageBitmap(bitmap);
                mCaptchaField.setText("");
                mCaptchaField.setHint(R.string.enter_captcha);
                mLoginButton.setEnabled(true);
            }

            @Override
            public void onReceiveHtml(String html) {
                saveCredentials(sLoginTask.getUsername(), sLoginTask.getPassword());

                // start activity
                Intent intent = new Intent(LoginActivity.this, DisplayStatsActivity.class);
                intent.putExtra(EXTRA_HTML, html);
                startActivity(intent);
            }

            @Override
            public void onLoginError(int error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                mLoginButton.setEnabled(true);
            }

            @Override
            public void onLoginError(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                mLoginButton.setEnabled(true);
            }

            @Override
            public void onChangeState(int state) {
                mCaptchaField.setText(state);
                if (state == R.string.connecting || state == R.string.submitting) {
                    mLoginButton.setEnabled(false);
                }
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsernameField.getText().toString().trim();
                final String password = mPasswordField.getText().toString();
                final String captcha = mCaptchaField.getText().toString().trim();

                if (NetworkManager.isConnected(LoginActivity.this)) {
                    // Login
                    mLoginButton.setEnabled(false);
                    sLoginTask.submit(username, password, captcha);

                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_network_connection, Toast.LENGTH_LONG).show();
                    mCaptchaField.setHint(R.string.no_network);
                }

            }
        });

        mCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sLoginTask.refreshCaptcha();
            }
        });

        new AnimationHandler().start();
    }

    private void initVars() {
        sAppSavedData = new AppSavedData(this);
    }

    private void setupTimber() {
        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
    }

    private void getViews() {
        mUsernameField = (EditText) findViewById(R.id.username);
        mPasswordField = (EditText) findViewById(R.id.password);
        mCaptcha = (ImageView) findViewById(R.id.captcha_view);
        mCaptchaField = (EditText) findViewById(R.id.captcha);
        mWebView = (WebView) findViewById(R.id.web_view);
        mLoginButton = (ImageButton) findViewById(R.id.login_button);
        mAnimatedBackground = (AnimatedBackground) findViewById(R.id.animated_background);
    }

    private void loadCredentials() {
        mUsernameField.setText(sAppSavedData.getUsernameText());
        mPasswordField.setText(sAppSavedData.getPasswordText());
    }

    private void saveCredentials(String username, String password) {
        sAppSavedData.setUsernamePassword(username, password);
    }


    private class AnimationHandler {
        private ImageView mNameIcon;
        private ImageView mPasswordIcon;

        private AnimationHandler() {
            getViews();
        }

        private void start() {
            mUsernameField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mNameIcon.setActivated(hasFocus);
                }

            });
            mPasswordField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    mPasswordIcon.setActivated(hasFocus);
                }
            });
        }

        private void getViews() {
            mNameIcon = (ImageView) findViewById(R.id.name_icon);
            mPasswordIcon = (ImageView) findViewById(R.id.password_icon);
        }
    }

    public void toggleAnimation(View v) {
        mAnimatedBackground.toggleAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginButton.setEnabled(true);
    }

    public static final String EXTRA_HTML = "space.linuxdeveloper.osum.app.HTML";
}
