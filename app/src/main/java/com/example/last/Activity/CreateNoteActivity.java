package com.example.last.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.last.Note;
import com.example.last.R;
import com.example.last.Util;
import com.example.last.databinding.ActivityCreateNoteBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class CreateNoteActivity extends AppCompatActivity {
    private ActivityCreateNoteBinding bin;
    String title, content,docId;
    boolean isEditMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bin = ActivityCreateNoteBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(bin.getRoot());
        setListeners();
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");
        bin.NoteContent.setSelection(bin.NoteContent.getText().length());
        bin.NoteTitle.setText(title);
        bin.NoteContent.setText(content);

        if (docId != null && !docId.isEmpty()){
            isEditMode = true;
        }


    }

    private void setListeners(){

        bin.saveNote.setOnClickListener( (v) -> saveNote());
        bin.deleteNote.setOnClickListener( (v) -> deleteNotefromFire());
    }
    void deleteNotefromFire(){
        DocumentReference documentReference;
        documentReference = Util.getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Util.showToast(CreateNoteActivity.this,"Note deleted " );
                    finish();
                }else{
                    Util.showToast(CreateNoteActivity.this, "Failed deleted note");
                }
            }
        });
    }

    void saveNote(){
        String noteTitle = bin.NoteTitle.getText().toString();
        String noteContent = bin.NoteContent.getText().toString();
        if(noteTitle == null || noteTitle.isEmpty() ){
            bin.NoteTitle.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFire(note);
    }

    void saveNoteToFire(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            documentReference = Util.getCollectionReferenceForNotes().document(docId);
        }else {
            documentReference = Util.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Util.showToast(CreateNoteActivity.this,"Note added " );
                    finish();
                }else{
                    Util.showToast(CreateNoteActivity.this, "Failed added note");
                }
            }
        });
    }
}