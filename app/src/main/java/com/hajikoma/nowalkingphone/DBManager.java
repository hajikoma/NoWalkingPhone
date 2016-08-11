package com.hajikoma.nowalkingphone;


import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private void setListener() {
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
     *
     * @return ユーザーID
     */
    public String getUserId(String userName, int firstScore) {
        Firebase newPostRef = t_users.push();
        newPostRef.setValue(new UserEntity(userName, firstScore));
        return newPostRef.getKey();
    }


    /**
     * first_scoreを、usersテーブルとscoresテーブルに保存する
     */
    public void saveFirstScore(final String userId, int oldFirstScore, int newFirstScore) {
        Map<String, Object> uSender = new HashMap<>();
        uSender.put("first_score", newFirstScore);
        t_users.child(userId).updateChildren(uSender);

        // scoresには同時アクセスの可能性があるため、トランザクションで処理
        final String user_id = userId;
        // 古いスコアの削除
        t_scores.child(Integer.toString(oldFirstScore)).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() != null) {
                    // 古いスコアのレコードからuser_idを削除
                    ArrayList<String> ids = mutableData.getValue(ArrayList.class);
                    for (String id : ids) {
                        if (id.equals(user_id)) {
                            ids.remove(id);
                        }
                    }
                    mutableData.setValue(ids);
                }
                return Transaction.success(mutableData);
            }

            // トランザクション完了後の処理
            @Override
            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (!committed) {
                    Log.e(TAG, firebaseError.getMessage(), firebaseError.toException());
                }
            }
        });
        // 新しいスコアの登録
        t_scores.child(Integer.toString(newFirstScore)).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                ArrayList<String> ids = new ArrayList<>();
                if (mutableData.getValue() == null) {
                    // そのスコアのレコードを新規作成
                    ids.add(user_id);
                    mutableData.setValue(ids);
                } else {
                    // 同スコアのレコードが存在する場合、値にuser_idを追加
                    ids = mutableData.getValue(ArrayList.class);
                    if (ids.indexOf(user_id) == -1) {
                        ids.add(user_id);
                        mutableData.setValue(ids);
                    }
                }
                return Transaction.success(mutableData);
            }

            // トランザクション完了後の処理
            @Override
            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot dataSnapshot) {
                if (!committed) {
                    Log.e(TAG, firebaseError.getMessage(), firebaseError.toException());
                }
            }
        });
    }
}