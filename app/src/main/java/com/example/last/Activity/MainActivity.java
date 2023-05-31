package com.example.last.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;

import com.example.last.Note;
import com.example.last.NoteAdapter;
import com.example.last.Util;
import com.example.last.databinding.ActivityMainBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding bin;
    NoteAdapter noteAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        bin = ActivityMainBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(bin.getRoot());
        setListeners();
        setupRecyclerView();
    }

    private void setListeners(){
        bin.buttonNew.setOnClickListener( (v) -> startActivity(new Intent(MainActivity.this, CreateNoteActivity.class)));
        bin.ButtonMenu.setOnClickListener( (v) -> showMenu() );
    }
    void showMenu(){
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, bin.ButtonMenu);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle() == "Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();
                    return true;
                }return  false;
            }
        });
    }

    void setupRecyclerView(){
        Query query = Util.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();
        bin.recyclerView.setLayoutManager( new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        bin.recyclerView.setAdapter(noteAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }


}