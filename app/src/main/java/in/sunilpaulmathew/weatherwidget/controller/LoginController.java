package in.sunilpaulmathew.weatherwidget.controller;
import in.sunilpaulmathew.weatherwidget.R;
import in.sunilpaulmathew.weatherwidget.activities.LoginActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.text.method.PasswordTransformationMethod;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import in.sunilpaulmathew.weatherwidget.activities.MainActivity;
import in.sunilpaulmathew.weatherwidget.activities.RegisterActivity;
import in.sunilpaulmathew.weatherwidget.model.User;
public class LoginController {

    private LoginActivity mContext;
    private FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    @SuppressLint("MissingInflatedId")
    public LoginController(LoginActivity context) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
    }
    public void handleGoogleLg() {
            GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(mContext.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(mContext, options);
            Intent signInClient = googleSignInClient.getSignInIntent();
            mContext.startActivityForResult(signInClient, mContext.RC_SIGN_IN);
    }



    public void manageResults(Task<GoogleSignInAccount> task) {

            GoogleSignInAccount account = task.getResult();
            if (task.isSuccessful() && account != null) {
                String email = account.getEmail();
                String userId = account.getId();

                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential).addOnCompleteListener(mContext, new OnCompleteListener <AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task <AuthResult> signInTask) {
                        if (signInTask.isSuccessful()) {
                            // Truy cập Firebase Realtime Database để kiểm tra thông tin đăng nhập
                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        User user = dataSnapshot.getValue(User.class);
                                        if (user != null) {
                                            String userId = dataSnapshot.getKey();
                                            // Nếu thông tin đăng nhập chính xác, chuyển hướng người dùng đến MainActivity
                                            Intent intent = new Intent(mContext, MainActivity.class);
                                            intent.putExtra("userId", userId);
                                            intent.putExtra("mobile", email);
                                            intent.putExtra("action", "loginWithPhone");
                                            mContext.startActivity(intent);
                                            mContext.finish(); // Đóng activity hiện tại để không quay lại nếu nhấn nút back
                                            return;
                                        }
                                    } else {
                                        @SuppressLint("SimpleDateFormat")
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                        // Lấy thời gian hiện tại
                                        Date currentTime = new Date();
                                        // Biến đổi thời gian thành chuỗi theo định dạng đã định
                                        String formattedTime = dateFormat.format(currentTime);

                                        // Tạo một người dùng mới với email và userId từ Google
                                        User newUser = new User();
                                        newUser.setId(userId);
                                        newUser.setEmail(email);
                                        newUser.setFormattedTime(formattedTime);



                                        // Lưu người dùng mới vào Firebase Realtime Database
                                        usersRef.child(userId).setValue(newUser);


                                        Intent intent = new Intent(mContext, MainActivity.class);
                                        intent.putExtra("userId", userId);
                                        intent.putExtra("email", email);
                                        intent.putExtra("action", "loginWithEmail");
                                        Toast.makeText(mContext, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                        mContext.startActivity(intent);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Xử lý khi có lỗi xảy ra
                                    Toast.makeText(mContext, "Lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
 //                               Toast.makeText(mContext, "Login Failed: " + signInTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(mContext, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
    }

    public void handleCheckBox(final EditText passedit, CheckBox checkBoxShowPassword) {
        checkBoxShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Hiện mật khẩu
                    passedit.setTransformationMethod(null);
                } else {
                    // Ẩn mật khẩu
                    passedit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                // Di chuyển con trỏ đến cuối chuỗi
                passedit.setSelection(passedit.getText().length());
            }
        });
    }

    public void handleLogin(final EditText emailedit, final EditText passedit) {
        String email = emailedit.getText().toString();
        String pass = passedit.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(mContext, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(mContext, "Địa chỉ email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(mContext, "Vui lòng nhập password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(mContext, new OnCompleteListener <AuthResult>() {
            @Override
            public void onComplete(@NonNull Task  <AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, MainActivity.class);
                    mContext.startActivity(intent);
                    // Kết thúc LoginActivity
                    mContext.finish();
                } else {
                    Toast.makeText(mContext, "Đăng nhập không thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void handleForgotPassword(final EditText emailedit) {
        String email = emailedit.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(mContext, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi email đặt lại mật khẩu
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mContext, "Đã gửi email đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Gửi email đặt lại mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void handleRegister(Context context) {
        Intent i = new Intent(context, RegisterActivity.class);
        context.startActivity(i);
    }
}