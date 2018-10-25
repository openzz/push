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


    public DatabaseReference getDatabaseReference (){
        return FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDatabaseReferenceChild (DatabaseReference dbRef, String childName){
        return dbRef.child(childName);
    }

    public ArrayList<String> getDevices(DatabaseReference dbRef){

        //showProgressDialog();

        ArrayList<String> my_list = new ArrayList<>();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String connStr = ds.getValue().toString();
                    my_list.add(connStr);
                    Log.d("TAG", connStr);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        dbRef.addListenerForSingleValueEvent(eventListener);

        //hideProgressDialog();

        return my_list;
    }
}
