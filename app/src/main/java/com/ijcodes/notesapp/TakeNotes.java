package com.ijcodes.notesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class TakeNotes extends AppCompatActivity {

    private TextView notesTextView; // Display the notes
    private EditText noteEditText; // to enter a new note
    private Button addNoteButton; // to add new note

    // Firestore
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_notes);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Reference elements from XML
        noteEditText = findViewById(R.id.noteEditText);
        notesTextView = findViewById(R.id.notesTextView);
        addNoteButton = findViewById(R.id.addNoteButton);

        // Fetch notes from Firestore
        fetchNotesFromFirestore();

        // Set click listener for addNoteButton
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note = noteEditText.getText().toString();
                addNoteToFirestore(note);
            }
        });
    }

    private void fetchNotesFromFirestore() {
        firestore.collection("notes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Handle each document here
                                String note = document.getString("note");
                                stringBuilder.append(note).append("\n");
                            }
                            notesTextView.setText(stringBuilder.toString());
                        } else {
                            // Handle the error
                        }
                    }
                });
    }

    private void addNoteToFirestore(String note) {
        Map<String, Object> noteData = new HashMap<>();
        noteData.put("note", note);

        firestore.collection("notes")
                .add(noteData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Note successfully added, fetch updated notes from Firestore
                        fetchNotesFromFirestore();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                    }
                });
    }
}
