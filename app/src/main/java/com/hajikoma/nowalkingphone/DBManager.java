package com.hajikoma.nowalkingphone;


import android.app.Activity;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * FirebaseのDBへのアクセス全般を担当する。
 * FirebaseDBは複数ユーザーによる同時read/writeが行われるため、対象のテーブルに同期処理が必要でないか気を付けること。
 * Firebaseとの通信は非同期で行われるため、処理タイミングにも注意し、適宜同期化などすること。
 * これら条件により、このクラスのメンバは可能な限り隠蔽すること。
 */
public class DBManager {

    private final String TAG = "DBManager";

    /** usersテーブルへのアクセス */
    private Firebase t_users;
    /** scoresテーブルへのアクセス */
    private Firebase t_scores;

    /** scoresテーブルのデータ */
    private Map<String, ArrayList<String>> scores = new LinkedHashMap<>();
    /** scoresテーブルから取得するレコード上限 */
    public static final int MAX_FETCH_SCORES = 1000;


    public DBManager(Activity activity) {
        // Firebaseデータベースへの参照を取得
        Firebase.setAndroidContext(activity);
        Firebase mFirebaseRef = new Firebase("https://nowalkingphone.firebaseio.com/");
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
     * DBにアクセスし、自動で発行される一意の文字列をユーザーIDとして、にユーザーのレコードを作成する
     * push()を使用すると、タイムスタンプを基にした一意の文字列が返されることを利用している
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
        // 古いスコアの削除
        t_scores.child(Integer.toString(oldFirstScore)).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() != null) {
                    // 古いスコアのレコードからuser_idを削除
                    ArrayList<String> ids = mutableData.getValue(ArrayList.class);
                    for (String id : ids) {
                        if (id.equals(userId)) {
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
                    ids.add(userId);
                    mutableData.setValue(ids);
                } else {
                    // 同スコアのレコードが存在する場合、値にuser_idを追加
                    ids = mutableData.getValue(ArrayList.class);
                    if (ids.indexOf(userId) == -1) {
                        ids.add(userId);
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


    /**
     * scoresテーブルから取得した降順のレコードを返す
     *
     * @return キー：スコア　値：そのスコアのid一覧
     */
    public Map<String, ArrayList<String>> getLoadedScore() {
        return scores;
    }


    /**
     * scoresテーブルからデータが取得できているかどうかを返す
     */
    public boolean isScoreLoaded() {
        return scores.size() >= 1;
    }


    /**
     * scoresテーブルからレコードを取得し、降順でscoresメンバに格納する
     * レコード取得は別スレッドで行われるため、非同期処理である
     */
    public void fetchScoresDataUnsync() {
        // 一度だけデータを取得し、リスナー解除
        t_scores.limitToLast(MAX_FETCH_SCORES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.i(TAG, "start on data change");
                scores.clear();
                Map<String, ArrayList<String>> data = snapshot.getValue(LinkedHashMap.class);
                ArrayList<String> keys = new ArrayList<>(data.keySet());
                Integer keys_i[] = new Integer[keys.size()];
                // keyをIntegerに
                for (int i = 0; i < keys.size(); i++) {
                    keys_i[i] = Integer.valueOf(keys.get(i));
                }
                // 降順で並べ替え
                Arrays.sort(keys_i, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer lhs, Integer rhs) {
                        if (lhs < rhs) {
                            return 1;
                        } else if (lhs > rhs) {
                            return -1;
                        }

                        return 0;
                    }
                });
                // 格納
                for (Integer key_i : keys_i) {
                    scores.put(String.valueOf(key_i), data.get(String.valueOf(key_i)));
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, firebaseError.getMessage(), firebaseError.toException());
            }
        });
    }
}
