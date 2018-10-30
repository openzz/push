package com.example.ah.push;


import android.support.annotation.NonNull;
import android.util.Log;

import com.example.ah.push.login.BaseActivity;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class FirebaseOperations extends BaseActivity {
    private DatabaseReference mDatabase;

    //MainList mainList = new MainList();


    public DatabaseReference getDatabaseReference (){
        return FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDatabaseReferenceChild (DatabaseReference dbRef, String childName){
        return dbRef.child(childName);
    }

    public ArrayList<String> saveFromFirebaseTest(ArrayList<String> input){
        ArrayList<String> test_list = new ArrayList<>();
        test_list = input;
        return test_list;
    }

    public void getDevices(DatabaseReference dbRef){

        ArrayList<String> my_list = new ArrayList<>();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v("Async101", "Done loading bookmarks");
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String connStr = ds.getKey();
                    my_list.add(connStr);
                    Log.d("TAG", connStr);
                }
                //mainList.updateListFromFirebase(my_list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
