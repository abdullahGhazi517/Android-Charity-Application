package com.example.tawfiqthefooddonationapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Receive extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private EditText edtTxtName, edtTxtCNIC, edtTxtPhone, edtTxtAddress, edtTxtFamilyMembers, edtTxtMonthlyIncome;
    private List<Uri> fileUris = new ArrayList<>();
    private Button btnUploadStampPaper, btnReceive;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userID;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        edtTxtName = findViewById(R.id.editTextName_R);
        edtTxtCNIC = findViewById(R.id.editTextCNIC_R);
        edtTxtPhone = findViewById(R.id.editTextPhoneNumber_R);
        edtTxtAddress = findViewById(R.id.editTextAddress_R);
        edtTxtFamilyMembers = findViewById(R.id.editTextFamilyMembers_R);
        edtTxtMonthlyIncome = findViewById(R.id.editTextMonthlyIncome_R);
        btnUploadStampPaper = findViewById(R.id.buttonUploadStampPaper);
        btnReceive = findViewById(R.id.buttonRecv);

        btnUploadStampPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleReceiveClick();
            }
        });
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                if (data.getClipData() != null) {
                    // Multiple files selected
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri fileUri = data.getClipData().getItemAt(i).getUri();
                        fileUris.add(fileUri);
                        Toast.makeText(this, "File selected: " + fileUri.getPath(), Toast.LENGTH_SHORT).show();
                    }
                } else if (data.getData() != null) {
                    // Single file selected
                    Uri fileUri = data.getData();
                    fileUris.add(fileUri);
                    Toast.makeText(this, "File selected: " + fileUri.getPath(), Toast.LENGTH_SHORT).show();
                }
                btnReceive.setEnabled(true);
                btnReceive.setBackgroundResource(R.drawable.button_enabled);
            }
        }
    }

    private void handleReceiveClick() {
        String fullname = edtTxtName.getText().toString().trim();
        String phone = edtTxtPhone.getText().toString().trim();
        String YourAddress = edtTxtAddress.getText().toString().trim();
        String yourCnic = edtTxtCNIC.getText().toString().trim();
        String familyMembers = edtTxtFamilyMembers.getText().toString().trim();
        String monthlyIncome = edtTxtMonthlyIncome.getText().toString().trim();
        String type = "Receiver";

        if (TextUtils.isEmpty(fullname)) {
            edtTxtName.setError("Name is Required.");
            return;
        }
        if (TextUtils.isEmpty(YourAddress)) {
            edtTxtAddress.setError("Address is Required.");
            return;
        }
        if (TextUtils.isEmpty(yourCnic)) {
            edtTxtCNIC.setError("CNIC is Required.");
            return;
        }
        if (yourCnic.length() < 13) {
            edtTxtCNIC.setError("CNIC Must be equal to 13 Characters");
            return;
        }
        if (phone.length() < 10) {
            edtTxtPhone.setError("Phone Number Must be 11 Characters");
            return;
        }

        if (isEligible()) {
            if (!fileUris.isEmpty()) {
                saveReceiverData(fullname, phone, YourAddress, yourCnic, familyMembers, monthlyIncome, type);
            } else {
                Toast.makeText(Receive.this, "Please upload the stamp paper.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(Receive.this, "You are not eligible for the donation.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEligible() {
        String familyMembersStr = edtTxtFamilyMembers.getText().toString().trim();
        String monthlyIncomeStr = edtTxtMonthlyIncome.getText().toString().trim();

        if (familyMembersStr.isEmpty() || monthlyIncomeStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return false;
        }

        int familyMembers = Integer.parseInt(familyMembersStr);
        int monthlyIncome = Integer.parseInt(monthlyIncomeStr);

        return familyMembers >= 4 && monthlyIncome <= 20000;
    }

    private void saveReceiverData(String fullname, String phone, String YourAddress, String yourCnic, String familyMembers, String monthlyIncome, String type) {
        if (fAuth.getCurrentUser() != null) {
            userID = fAuth.getCurrentUser().getUid();
            CollectionReference collectionReference = fStore.collection("user_receiver");

            Map<String, Object> user = new HashMap<>();
            user.put("userid", userID);
            user.put("cnic", yourCnic);
            user.put("name", fullname);
            user.put("phone", phone);
            user.put("address", YourAddress);
            user.put("familyMembers", familyMembers);
            user.put("monthlyIncome", monthlyIncome);
            user.put("timestamp", FieldValue.serverTimestamp());
            user.put("type", type);

            collectionReference.add(user)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Upload each file in the fileUris list
                            for (Uri uri : fileUris) {
                                uploadFile(uri, documentReference.getId());
                            }
                            Intent intent = new Intent(Receive.this, donorRequest.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error!", e);
                        }
                    });
        }
    }

    private void uploadFile(Uri fileUri, String documentId) {
        if (fileUri != null) {
            // Create a storage reference from our app
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            // Create a reference to the file you want to upload
            StorageReference fileRef = storageRef.child("uploads/" + System.currentTimeMillis() + "_" + documentId + "_" + fileUri.getLastPathSegment());

            // Upload the file to Firebase Storage
            fileRef.putFile(fileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get the download URL
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // Add the download URL to the Firestore document
                                    addFileUrlToDocument(uri.toString(), documentId);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error getting download URL", e);
                                    Toast.makeText(Receive.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error uploading file", e);
                            Toast.makeText(Receive.this, "File upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void addFileUrlToDocument(String downloadUrl, String documentId) {
        // Get the Firestore instance
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Create a reference to the document
        DocumentReference docRef = firestore.collection("user_receiver").document(documentId);

        // Update the document with the new file URL
        docRef.update("fileUrls", FieldValue.arrayUnion(downloadUrl))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated with file URL!");
                        Toast.makeText(Receive.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(Receive.this, "Failed to update document with file URL", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
