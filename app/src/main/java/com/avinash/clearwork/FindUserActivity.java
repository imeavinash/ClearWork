package com.avinash.clearwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SearchRecentSuggestions;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {

    public static final String tag = "FindUserActivity";

    private RecyclerView mUserList;
    private RecyclerView.Adapter mUserListAdapter;
    private RecyclerView.LayoutManager mUserListLayoutManager;

    ArrayList<UserObject> userList,contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        userList = new ArrayList<>();
        contactList = new ArrayList<>();

        initializeRecyclerView();
        getContactList();
    }

    private void initializeRecyclerView() {

        mUserList = findViewById(R.id.userList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(false);
        mUserListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL,false);
        mUserList.setLayoutManager(mUserListLayoutManager);
        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
    }

    private void getContactList() {

        String ISOPrefix = getCountryISO();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        int count = 0;
        ArrayList<String> rawPhones = new ArrayList<>();
        while(phones.moveToNext()){
            count++;
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            if(phone.contains("9148635406"))
            Log.d(tag,"count = "+count+" getContactList cursor phone = "+phone);

            if(!String.valueOf(phone.charAt(0)).equals("+"))
                phone = ISOPrefix + phone;

            if(!rawPhones.contains(phone)){
                UserObject mContact = new UserObject(name,phone);
                contactList.add(mContact);
                getUserDetails(mContact);
                rawPhones.add(phone);

            }


            }

        rawPhones.clear();

        }

        private void getUserDetails(UserObject mContact) {

            DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("user");
            Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        //Log.d(tag,"getUserDetails snapshot exists");
                        String phone = "", name = "";
                        for(DataSnapshot childSnapshot: snapshot.getChildren()){
                            if(childSnapshot.child("phone").getValue()!=null)
                                phone = childSnapshot.child("phone").getValue().toString();
                            if(childSnapshot.child("name").getValue()!=null)
                                name = childSnapshot.child("name").getValue().toString();
                            //Log.d(tag,"childsnapshot name = "+name+" phone = "+phone);

                            UserObject mUser = new UserObject(name,phone);
                            userList.add(mUser);
                            mUserListAdapter.notifyDataSetChanged();
                            return;
                        }
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getCountryISO (){
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso()!=null)
            if(!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();

        return CountryToPhonePrefix.getPhone(iso);
    }

}
