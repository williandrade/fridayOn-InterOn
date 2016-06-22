package me.williandrade.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.sinch.android.rtc.SinchError;

import me.williandrade.R;
import me.williandrade.dto.UserDTO;
import me.williandrade.service.SinchService;
import me.williandrade.util.BaseActivity;
import me.williandrade.util.Helper;

public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener {

    private EditText inputEmail;
    private EditText inputPassword;
    private Button buttonSingIn;
    private Button buttonForgotPassword;
    private Button buttonFacebook;
    private Button buttonLinkedin;
    private Button buttonSingUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPassword = (EditText) findViewById(R.id.inputPassword);

        setKeyListners();

        buttonSingIn = (Button) findViewById(R.id.buttonSingIn);
        buttonForgotPassword = (Button) findViewById(R.id.buttonForgotPassword);
        buttonFacebook = (Button) findViewById(R.id.buttonFacebook);
        buttonLinkedin = (Button) findViewById(R.id.buttonLinkedin);
        buttonSingUp = (Button) findViewById(R.id.buttonSingUp);

        setOnClicks();

    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStarted() {
        openPlaceCallActivity();
    }


    public void singIn() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, "Please write your e-mail", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please write your password", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            if (!getSinchServiceInterface().isStarted()) {
                                persistActive(task.getResult().getUser());
                                getSinchServiceInterface().startClient(task.getResult().getUser());
                            }

                            openPlaceCallActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void forgotPassword() {
        Toast.makeText(LoginActivity.this, "Forget Password called", Toast.LENGTH_SHORT).show();
    }

    public void facebook() {
        Toast.makeText(LoginActivity.this, "Facebook called", Toast.LENGTH_SHORT).show();
    }

    public void linkedin() {
        Toast.makeText(LoginActivity.this, "Linkedin called", Toast.LENGTH_SHORT).show();
    }

    public void singUp() {
        Toast.makeText(LoginActivity.this, "Sing Up called", Toast.LENGTH_SHORT).show();
    }

    private void setKeyListners() {
        inputEmail.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    new Helper().setEditTextFocus(inputPassword, true, getBaseContext());
                    return true;
                }
                return false;
            }
        });

        inputPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    singIn();
                    return true;
                }
                return false;
            }
        });
    }

    private void setOnClicks() {
        buttonSingIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singIn();
            }
        });
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });
        buttonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebook();
            }
        });
        buttonLinkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkedin();
            }
        });
        buttonSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singUp();
            }
        });
    }

    public void persistActive(FirebaseUser user) {

        UserDTO userDTO = new UserDTO();

        userDTO.setUid(user.getUid());
        userDTO.setDisplayName(user.getDisplayName());
        userDTO.setDoing("Testing - App");
        userDTO.setEmail(user.getEmail());
        if(user.getPhotoUrl() != null){
            userDTO.setPhoto(user.getPhotoUrl().toString());
        }

        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("logged");

        String toPersist = new Gson().toJson(userDTO);

        myRef.child(user.getUid()).setValue(toPersist);
    }

    private void openPlaceCallActivity() {
        Intent mainActivity = new Intent(this, IndexActivity.class);
        startActivity(mainActivity);
    }

}
