package com.example.rapidresponse;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {


    EditText name, phone, email, aadhar, address,password,cpass,pincode,city, skillInput;
    ChipGroup skillsContainer;
    Button registerButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.signup_email);
        aadhar = findViewById(R.id.aadhar);
        address = findViewById(R.id.street_address);
        skillInput = findViewById(R.id.skill_input);
        password = findViewById(R.id.signup_password);
        cpass = findViewById(R.id.confirm_password);
        pincode = findViewById(R.id.pincode);
        city = findViewById(R.id.city);
        skillsContainer = findViewById(R.id.skills_container);
        registerButton = findViewById(R.id.register_button);

        skillInput.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                String skillText = skillInput.getText().toString().trim();
                if(!skillText.isEmpty()){
                    addSkillChip(skillText);
                    skillInput.setText("");
                }
                return true;
            }
            return false;
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }

            private void registerUser() {

                String userName = name.getText().toString().trim();
                String userPhone = phone.getText().toString().trim();
                String userEmail = email.getText().toString().trim();
                String userAadhar = aadhar.getText().toString().trim();
                String userAddress = address.getText().toString().trim();
                String userPassword = password.getText().toString().trim();
                String userCpass = cpass.getText().toString().trim();
                String userPincode = pincode.getText().toString().trim();
                String userCity = city.getText().toString().trim();

                // Collect skills from ChipGroup
                List<String> userSkills = new ArrayList<>();
                for (int i = 0; i < skillsContainer.getChildCount(); i++) {
                    Chip chip = (Chip) skillsContainer.getChildAt(i);
                    userSkills.add(chip.getText().toString());
                }

                // Input Validation
                if (userName.isEmpty() || userPhone.isEmpty() || userEmail.isEmpty() || userAadhar.isEmpty() ||
                        userAddress.isEmpty() || userPassword.isEmpty() || userCpass.isEmpty() || userPincode.isEmpty() || userCity.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ Validate Phone Number (10 digits)
                if (!userPhone.matches("\\d{10}")) {
                    Toast.makeText(SignUpActivity.this, "Enter a valid 10-digit phone number", Toast.LENGTH_SHORT).show();
                    return;
                }

                // ✅ Validate Aadhar ID (12 digits)
                if (!userAadhar.matches("\\d{12}")) {
                    Toast.makeText(SignUpActivity.this, "Enter a valid 12-digit Aadhar ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!userPassword.equals(userCpass)) {
                    Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Register user in Firebase Authentication
                mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                if (firebaseUser != null) {
                                    saveUserToFirestore(firebaseUser.getUid(), userName, userPhone, userEmail, userAadhar, userAddress, userPincode, userCity, userSkills);
                                }
                            } else {
                                Toast.makeText(SignUpActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });



            }

            private void saveUserToFirestore(String uid, String userName, String userPhone, String userEmail, String userAadhar, String userAddress, String userPincode, String userCity, List<String> userSkills) {
                Map<String, Object> user = new HashMap<>();

                user.put("name", userName);
                user.put("phone", userPhone);
                user.put("email", userEmail);
                user.put("aadhar", userAadhar);
                user.put("address", userAddress);
                user.put("pincode", userPincode);
                user.put("city", userCity);
                user.put("skills", userSkills);

                db.collection("users").document(uid)
                        .set(user)
                        .addOnSuccessListener(aVoid ->
                            Toast.makeText(SignUpActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(SignUpActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show());


            }

        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void addSkillChip(String skill) {
        Chip chip = new Chip(this);
        chip.setText(skill);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(view -> skillsContainer.removeView(chip));
        skillsContainer.addView(chip);
    }
}