package com.pd.chatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;


public class Chat extends AppCompatActivity {
    LinearLayout layout;
    RelativeLayout layout_2;
    ImageView sendButton;
    ImageButton back;
    EditText messageArea;
    TextView username_display;
    ScrollView scrollView;
    Firebase reference1, reference2;
    Trie trie=new Trie();
    int found_bad_word=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        sendButton = findViewById(R.id.sendButton);
        messageArea = findViewById(R.id.messageArea);
        scrollView = findViewById(R.id.scrollView);
        back=findViewById(R.id.back_button);
        username_display = findViewById(R.id.username_display);

        String string = "";
        StringBuilder stringBuilder = new StringBuilder();
        InputStream is = this.getResources().openRawResource(R.raw.sample);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        while (true) {
            try {
                if ((string = reader.readLine()) == null) break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            //stringBuilder.append(string).append("\n");
            //System.out.println(string);
            trie.insert(string);

        }
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Toast.makeText(getBaseContext(), stringBuilder.toString(),
//                Toast.LENGTH_LONG).show();
//
//        if(trie.search("ass"))
//            Toast.makeText(getApplicationContext(),"Yes",Toast.LENGTH_LONG).show();
//        else
//            Toast.makeText(getApplicationContext(),"No",Toast.LENGTH_LONG).show();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Chat.this,Users.class));
            }
        });

        Firebase.setAndroidContext(this);
        reference1 = new Firebase("https://letschat-8dd83.firebaseio.com/messages/" + UserDetails.username + "_" + UserDetails.chatWith);
        reference2 = new Firebase("https://letschat-8dd83.firebaseio.com/messages/" + UserDetails.chatWith + "_" + UserDetails.username);
        username_display.setText(UserDetails.chatWith);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                found_bad_word=0;
                String messageText = messageArea.getText().toString();
                int i=0;
                boolean check=false;
                String s="";
                Stack<String> queue=new Stack<>();
                while(i<messageText.length())
                {
                    if(messageText.charAt(i)=='\0')
                    {
                        System.out.println(s);
                        queue.push(s);
                        s="";
                    }
                    else
                    s=s+messageText.charAt(i);

                    i++;
                }
                queue.push(s);
                System.out.println(queue);

                while(!queue.empty())
                {
                    String a=queue.pop();
                    System.out.println(a);
                    check=trie.search(a);
                    if(check==true)
                    {
                        found_bad_word=1;
                        LayoutInflater inflater = (LayoutInflater)
                                getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.popup_window, null);

                        // create the popup window
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                        // show the popup window
                        // which view you pass in doesn't matter, it is only used for the window tolken
                        popupWindow.showAtLocation(v, Gravity.START, 0, 0);

                        // dismiss the popup window when touched
                        popupView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                popupWindow.dismiss();
                                return true;
                            }
                        });
                        break;
                    }
                }
                if(!messageText.equals("")&&found_bad_word==0&&check==false){
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("message", messageText);
                    map.put("user", UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                    messageArea.setText("");
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map = dataSnapshot.getValue(Map.class);
                String message = map.get("message").toString();
                String userName = map.get("user").toString();

                if(userName.equals(UserDetails.username)){
                    addMessageBox(message, 1);
                }
                else{
                    addMessageBox(message, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }



    public void addMessageBox(String message, int type){
        TextView textView = new TextView(Chat.this);

        textView.setText(message);

        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = 7.0f;

        if(type == 1) {
            lp2.gravity = Gravity.RIGHT;
            textView.setBackgroundResource(R.drawable.bubble_in);
        }
        else{
            lp2.gravity = Gravity.LEFT;
            textView.setBackgroundResource(R.drawable.bubble_out);
        }
        textView.setLayoutParams(lp2);
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}