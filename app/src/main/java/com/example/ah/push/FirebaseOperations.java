package com.example.ah.push;


import android.support.annotation.NonNull;
import android.util.Log;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;



public class FirebaseOperations{

    private DatabaseReference mDatabase;
    boolean isInDbResult = false;


    public boolean isUserInDb(String userId) throws InterruptedException{
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference mRef = database.getReference().child("users");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isInDbResult = false;
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    if(ds.getKey() == userId){
                        isInDbResult = true;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return isInDbResult;
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addDeviceToDb(FirebaseDatabase db, FirebaseUser user, String connstr){

        String userId = user.getUid();
        DatabaseReference dbRef = db.getReference().child("users").child(userId).child("devices");

        ArrayList<String> str_list = new ArrayList<>();
        ArrayList<Integer> int_list = new ArrayList<>();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                str_list.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    str_list.add(key);

                }
                for(String el: str_list){
                    try{
                        int_list.add(Integer.parseInt(el));
                    }catch (Exception e){
                        Log.d("AH-TAG", "Can't parse to int");
                        e.printStackTrace();
                    }
                }

                Collections.sort(int_list);
                int index;

                for(int i = 0; i<int_list.size()-1; i++){
                    if( (int_list.get(i)+1) != int_list.get(i+1)){
                        index = int_list.get(i)+1;
                        dbRef.child(Integer.toString(index)).setValue(connstr);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addUserToDb(FirebaseDatabase db, FirebaseUser user){
        String userId = user.getUid();
        DatabaseReference mRef = db.getReference().child("users");
        mRef.child(userId).child("email").setValue(user.getEmail());
    }

    public void removeDeviceFromDb(FirebaseDatabase db, FirebaseUser user, String connstrToRemove){

        String userId = user.getUid();
        DatabaseReference dbRef = db.getReference().child("users").child(userId).child("devices");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getValue().toString().equals(connstrToRemove)){
                        ds.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
