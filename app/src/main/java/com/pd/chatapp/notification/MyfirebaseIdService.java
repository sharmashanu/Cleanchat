package com.pd.chatapp.notification;

import android.media.session.MediaSession;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyfirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        String refreshtoken= FirebaseInstanceId.getInstance().getToken();
        if(firebaseUser!=null)
        {
            updateToken(refreshtoken);
        }
    }

    private void updateToken(String refreshtoken) {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        token token=new token(refreshtoken);
        reference.child(firebaseUser.getUid()).setValue(token);
    }
}
