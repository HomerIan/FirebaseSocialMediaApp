package com.homerianreyes.firebasesocialmediaapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.homerianreyes.firebasesocialmediaapp.databinding.ActivityViewPostBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewPostActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ActivityViewPostBinding binding;
    private ArrayList<String> usernames;
    private ArrayAdapter adapter;
    private FirebaseAuth mAuth;
    private ArrayList<DataSnapshot> dataSnapshots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_view_post);
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        dataSnapshots = new ArrayList<>();
        usernames = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, usernames);
        binding.postListView.setAdapter(adapter);
        binding.postListView.setOnItemClickListener(this);
        binding.postListView.setOnItemLongClickListener(this);

        FirebaseDatabase.getInstance().getReference().child("my_users").child(mAuth.getCurrentUser().getUid()).child("received_posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                dataSnapshots.add(snapshot);
                String fromWhomUsername = (String) snapshot.child("fromWhom").getValue();
                usernames.add(fromWhomUsername);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                int i = 0;
                for (DataSnapshot mySnapshot : dataSnapshots){
                    if (mySnapshot.getKey().equals(snapshot.getKey())) {
                        dataSnapshots.remove(i);
                        usernames.remove(i);
                    }
                    i++;
                }
                adapter.notifyDataSetChanged();
                binding.sentPostImageView.setImageResource(R.drawable.image_placeholder);
                binding.descriptionTextView.setText("");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

        DataSnapshot mydataSnapshot = dataSnapshots.get(position);
        String downloadLink = (String) mydataSnapshot.child("imageLink").getValue();

        Picasso.get().load(downloadLink).into(binding.sentPostImageView);
        binding.descriptionTextView.setText((String) mydataSnapshot.child("des").getValue());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //continue to delete
                        //delete image in storage
                        FirebaseStorage.getInstance().getReference()
                                .child("my_images").child((String) dataSnapshots.get(position)
                                .child("imageIdentifier").getValue())
                                .delete();
                        //delete info in database
                        FirebaseDatabase.getInstance().getReference()
                                .child("my_users").child(mAuth.getCurrentUser()
                                .getUid()).child("recevied_posts")
                                .child(dataSnapshots.get(position).getKey()).removeValue();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        return false;
    }
}