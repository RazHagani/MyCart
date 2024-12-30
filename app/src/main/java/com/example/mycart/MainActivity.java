package com.example.mycart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mycart.fragments.CartFragment;
import com.example.mycart.models.Client;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();
    }

    private FirebaseAuth mAuth;

    public void navigateToCartFragment() {
        // מציאת ה-NavController
        NavController navController = Navigation.findNavController(this, R.id.fragmentContainerView);

        // ניווט ל-CartFragment
        navController.navigate(R.id.action_loginFragment_to_cartFragment);
    }




    public void login() {
        String email = ((EditText) findViewById(R.id.emailInputLoginFragment)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordInputLoginFragment)).getText().toString();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Login success", Toast.LENGTH_LONG).show();
                            navigateToCartFragment();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void register() {
        String email = ((EditText) findViewById(R.id.emailInputRegisterFragment)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordInputRegisterFragment)).getText().toString();
        String phone = ((EditText) findViewById(R.id.phoneInputRegisterFragment)).getText().toString();
        String username = ((EditText) findViewById(R.id.usernameInputRegisterFragment)).getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "register success", Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "register failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void addDATA() {
        // קריאת ערכים מהשדות
        String phone = ((EditText) findViewById(R.id.phoneInputRegisterFragment)).getText().toString();
        String username = ((EditText) findViewById(R.id.usernameInputRegisterFragment)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordInputRegisterFragment)).getText().toString();
        String email = ((EditText) findViewById(R.id.emailInputRegisterFragment)).getText().toString();

        // אימות קלטים
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        // שמירת הנתונים ב-Firebase
        String emailKey = email.replace(".", "_");

        // שמירת הנתונים ב-Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users").child(emailKey);

        Client C = new Client(username, email, password, phone);

        myRef.setValue(C).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show();
                // מעבר למסך הבא או ניקוי שדות
            } else {
                Toast.makeText(this, "Failed to add user. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }



//    public void getStudents(String phone) {
//
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("users").child(phone);
//
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // This method is called once with the initial value and again
//                // whenever data at this location is updated.
//                Students value = dataSnapshot.getValue(Students.class);
//                Toast.makeText(MainActivity.this, value.getEmail(), Toast.LENGTH_LONG).show();
//            }
}
