package com.example.yohjikusakabe.classprojecto;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText passwordText;
    private EditText rePasswordText;
    private EditText userNameEditText;
    private String TAG = "Register Activity";
    private FirebaseUser userID;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        buttonRegister = findViewById(R.id.registerNow);
        editTextEmail = findViewById(R.id.emailRegister);
        passwordText = findViewById(R.id.passwordRegister);
        rePasswordText = findViewById(R.id.passwordRe);
        userNameEditText = findViewById(R.id.userNameEdit);
        db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        buttonRegister.setOnClickListener(this);


        firebaseAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {
        if (view == buttonRegister) {
            registerUser();
        }
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String password2 = rePasswordText.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter a email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(password2)) {
            Toast.makeText(this, "Passwords must be matching", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Please enter a password of at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }


        progressDialog.setMessage("Registering...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.hide();
                    Toast.makeText(RegisterActivity.this, "You have been registered!", Toast.LENGTH_SHORT).show();
                    registerUserFireStore();
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(RegisterActivity.this, "This email is already in use", Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                } else {
                    Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
                    progressDialog.hide();
                }
            }
        });

    }

    public void registerUserFireStore() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("Username",userNameEditText.getText().toString().trim());
        userData.put("Date Created",new Date());
        userID = FirebaseAuth.getInstance().getCurrentUser();
        String userUID = userID.getUid();
        DocumentReference mDocRef = FirebaseFirestore.getInstance().document("users/" + userUID);
        mDocRef.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG,"Document has been saved");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG,"Document failed to save");
            }
        });
    }

}
