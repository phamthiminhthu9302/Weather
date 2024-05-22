package in.sunilpaulmathew.weatherwidget.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;

import in.sunilpaulmathew.weatherwidget.controller.LoginController;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 9001;
    private EditText emailedit, passedit;
    private Button btnlogin, btnregis, btnforgotpassword;
    private CheckBox checkBoxShowPassword;
    private LoginController mLoginController;
    private ImageView imagebtnLoginGg;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Utils.getString("language", "VietNamese", this).equals("English")){
            Utils.setLocale("en", this);
        }else{
            Utils.setLocale("vi", this);
        }
        setContentView(R.layout.fragment_login);

        mLoginController = new LoginController(this);

        imagebtnLoginGg = findViewById(R.id.imagebtnLoginGg);
        emailedit = findViewById(R.id.email);
        passedit = findViewById(R.id.password);
        btnlogin = findViewById(R.id.btnLogin);
        btnregis = findViewById(R.id.btnRegister);
        btnforgotpassword = findViewById(R.id.btnForgotPassword);
        checkBoxShowPassword = findViewById(R.id.checkBoxShowPassword);

        mLoginController.handleCheckBox(passedit, checkBoxShowPassword);



        imagebtnLoginGg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginController.handleGoogleLg();
            }
        });


        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginController.handleLogin(emailedit, passedit);

            }
        });

        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginController.handleRegister(LoginActivity.this);
            }
        });

        btnforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginController.handleForgotPassword(emailedit);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            mLoginController.manageResults(task);
        }
    }
}