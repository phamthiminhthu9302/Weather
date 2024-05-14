package in.sunilpaulmathew.weatherwidget.controller;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.activities.LoginActivity;

public class RegisterController {
    private final FirebaseAuth mAuth;
    private final Context mContext;

    public RegisterController(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mContext = context;
    }

    public void registerUser(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(mContext, mContext.getString(R.string.email_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(mContext, mContext.getString(R.string.email_err), Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(mContext, mContext.getString(R.string.pass_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, mContext.getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                            // Nếu đăng ký thành công, chuyển hướng người dùng đến màn hình đăng nhập
                            Intent i = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(i);
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.register_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
