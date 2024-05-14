package in.sunilpaulmathew.weatherwidget.activities;

import in.sunilpaulmathew.weatherwidget.controller.RegisterController;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import in.sunilpaulmathew.weatherwidget.R;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailedit, passedit;
    private Button btnregis;

    private RegisterController authController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);
        authController = new RegisterController(this);
        emailedit = findViewById(R.id.email);
        passedit = findViewById(R.id.password);
        btnregis = findViewById(R.id.btnRegister);


        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailedit.getText().toString(); // Lấy giá trị email sau khi người dùng nhập
                String pass = passedit.getText().toString(); // Lấy giá trị password sau khi người dùng nhập
                authController.registerUser(email, pass);
            }
        });
    }




}
