package com.example.yohjikusakabe.classprojecto;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // UI references.
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    EditText userName;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.emailSign);
        password = findViewById(R.id.passwordSign);
        progressDialog = new ProgressDialog(this);

        Button b = findViewById(R.id.email_sign_in_button);
        Button r = findViewById(R.id.create_account);
        b.setOnClickListener(this);
        r.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Sign in
        if (v == findViewById(R.id.email_sign_in_button)) {
            if (TextUtils.isEmpty(userName.getText())) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(password.getText())) {
                Toast.makeText(this, "Please enter a valid password", Toast.LENGTH_SHORT).show();
                return;
            }
            userLogIn();
            // Register account
        } else if (v == findViewById(R.id.create_account)) {
            Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(intent);
        }
    }

    private void userLogIn() {
        progressDialog.setMessage("Logging you in!");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(userName.getText().toString().trim(),password.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    // If already signed in will not create new activity?
                    progressDialog.hide();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    progressDialog.hide();
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

}

