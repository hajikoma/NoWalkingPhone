package com.hajikoma.nowalkingphone;


import android.app.Activity;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class DBManager {

    private final String TAG = "DBManager";

    // FirebaseDBへのアクセス
    private Firebase mFirebaseRef;
    // usersテーブルへのアクセス
    private Firebase t_users;
    // scoresテーブルへのアクセス
    private Firebase t_scores;


    public DBManager(Activity activity) {
        // クライアントライブラリにコンテキスト(Activity)をセット
        Firebase.setAndroidContext(activity);
        // Firebaseアプリへの参照を取得
        mFirebaseRef = new Firebase("https://nowalkingphone.firebaseio.com/");
        t_users = mFirebaseRef.child("users");
        t_scores = mFirebaseRef.child("scores");

        setListener();
    }


    // リスナー（DB更新時に自動で呼ばれる）
    private void setListener(){
        t_users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("Firebase", snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError error) {
                Log.d("Firebase", "error");
            }
        });
    }


    /**
     * DBにアクセスし、自動で発行されるidを元にユーザーのレコードを作成する
     * @return ユーザーID
     */
    public String getUserId(String userName, int firstScore){
        Firebase newPostRef = t_users.push();
        newPostRef.setValue(new UserEntity(userName, firstScore));
        return newPostRef.getKey();
    }
}
