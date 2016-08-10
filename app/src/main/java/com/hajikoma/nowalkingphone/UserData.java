package com.hajikoma.nowalkingphone;

import android.content.SharedPreferences;

/**
 * ユーザーのデータを格納するクラス。ユーザーデータはこのクラスで一元管理する。
 * データがおかしくなるとユーザーは大変なストレスを感じるため、このクラスの設計は データの安全性を優先して設計する。
 * ・シングルトンデザイン
 * ・全てのフィールド変数をカプセル化し、規定範囲外の値が入らないようにする
 * ・staticな列挙型による定数管理
 * ・例外処理の追加
 */
public class UserData {

    /** SharedPreferenceのキー：ユーザーID */
    public static final String PREF_STR_USER_ID = "user_id";
    /** SharedPreferenceのキー：ユーザー名 */
    public static final String PREF_STR_USER_NAME = "user_name";
    /** SharedPreferenceのキー：チュートリアル見たか */
    public static final String PREF_BOOL_TUTORIAL_SHOW = "tutorial_show";
    /** SharedPreferenceのキー：レビューしたか */
    public static final String PREF_BOOL_REVIEW = "review";
    /** SharedPreferenceのキー：レビュー依頼表示までのゲーム回数 */
    public static final String PREF_INT_REVIEW_REMAIN = "review_remain";
    /** SharedPreferenceのキー：起動回数 */
    public static final String PREF_INT_LAUNCH_TIME = "launch_time";
    /** SharedPreferenceのキー：ゲームした回数 */
    public static final String PREF_INT_PLAY_TIME = "play_time";
    /** SharedPreferenceのキー：最後に発生した例外から、ゲームした回数 */
    public static final String PREF_INT_EXCEPTION_BACKWARD = "exception_backward";
    /** SharedPreferenceのキー：ミュートかどうか */
    public static final String PREF_BOOL_MUTE = "mute";
    /** SharedPreferenceのキー：バイブするかどうか */
    public static final String PREF_BOOL_VIBE = "vibe";
    /** SharedPreferenceのキー：個人スコア1位 */
    public static final String PREF_INT_FIRST_SCORE = "first_score";
    /** SharedPreferenceのキー：個人スコア2位 */
    public static final String PREF_INT_SECOND_SCORE = "second_score";
    /** SharedPreferenceのキー：個人スコア3位 */
    public static final String PREF_INT_THIRD_SCORE = "third_score";
    /** SharedPreferenceのキー：合計スコア */
    public static final String PREF_LONG_GRAND_SCORE = "grand_score";

    /** ユーザーId */
    private String userId = "";
    /** ユーザー名 */
    private String userName = "";
    /** チュートリアル見たか */
    private boolean tutorialShow = false;
    /** レビューしたか */
    private boolean review = false;
    /** レビュー依頼表示までのゲーム回数 */
    private int reviewRemain = 3;
    /** 起動回数 */
    private int launchTime = 0;
    /** ゲームした回数 */
    private int playTime = 0;
    /** 最後に発生した例外から、ゲームした回数 */
    private int exceptionBackward = 0;
    /** 設定の状況（ミュート） */
    private boolean mute = false;
    /** 設定の状況（バイブレーション） */
    private boolean vibe = true;

    /** 個人スコア1位 */
    private int firstScore = 0;
    /** 個人スコア2位 */
    private int secondScore = 0;
    /** 個人スコア3位 */
    private int thirdScore = 0;
    /** 合計スコア */
    private long grandScore = 0L;

    /** 唯一のUserDataインスタンス */
    private static UserData ud = new UserData();


    /**
     * コンストラクタ。このクラスはシングルトンなクラスなので、このメソッドは外部から利用できない。
     * ミッションとアイテムの初期化を行う。
     */
    private UserData() {
    }


    /** UserDataインスタンスを取得する。複数回このメソッドを実行しても返されるインスタンスは同一である。 */
    public static UserData getUserData() {
        return ud;
    }


    /** UserDataインスタンス内のすべての値をSharedPreferenceから読み込む */
    public boolean getAllFromPref(SharedPreferences preference) {
        try {
            userId = preference.getString(PREF_STR_USER_ID, userId);
            userName = preference.getString(PREF_STR_USER_NAME, userName);
            tutorialShow = preference.getBoolean(PREF_BOOL_TUTORIAL_SHOW, tutorialShow);
            review = preference.getBoolean(PREF_BOOL_REVIEW, review);
            reviewRemain = preference.getInt(PREF_INT_REVIEW_REMAIN, reviewRemain);
            launchTime = preference.getInt(PREF_INT_LAUNCH_TIME, launchTime);
            playTime = preference.getInt(PREF_INT_PLAY_TIME, playTime);
            exceptionBackward = preference.getInt(PREF_INT_EXCEPTION_BACKWARD, exceptionBackward);
            mute = preference.getBoolean(PREF_BOOL_MUTE, mute);
            vibe = preference.getBoolean(PREF_BOOL_VIBE, vibe);
            firstScore = preference.getInt(PREF_INT_FIRST_SCORE, firstScore);
            secondScore = preference.getInt(PREF_INT_SECOND_SCORE, secondScore);
            thirdScore = preference.getInt(PREF_INT_THIRD_SCORE, thirdScore);
            grandScore = preference.getLong(PREF_LONG_GRAND_SCORE, grandScore);
        } catch (ExceptionInInitializerError e) {
            e.getCause().printStackTrace();
            return false;
        }

        return true;
    }


    /** UserDataインスタンス内のすべての値を保存する */
    public void saveAllToPref(SharedPreferences preference) {
        SharedPreferences.Editor editor = preference.edit();

        editor.putString(PREF_STR_USER_ID, userId);
        editor.putString(PREF_STR_USER_NAME, userName);
        editor.putBoolean(PREF_BOOL_TUTORIAL_SHOW, tutorialShow);
        editor.putBoolean(PREF_BOOL_REVIEW, review);
        editor.putInt(PREF_INT_REVIEW_REMAIN, reviewRemain);
        editor.putInt(PREF_INT_LAUNCH_TIME, launchTime);
        editor.putInt(PREF_INT_PLAY_TIME, playTime);
        editor.putInt(PREF_INT_EXCEPTION_BACKWARD, exceptionBackward);
        editor.putBoolean(PREF_BOOL_MUTE, mute);
        editor.putBoolean(PREF_BOOL_VIBE, vibe);
        editor.putInt(PREF_INT_FIRST_SCORE, firstScore);
        editor.putInt(PREF_INT_SECOND_SCORE, secondScore);
        editor.putInt(PREF_INT_THIRD_SCORE, thirdScore);
        editor.putLong(PREF_LONG_GRAND_SCORE, grandScore);

        editor.commit();
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isTutorialShow() {
        return tutorialShow;
    }

    public void setTutorialShow(boolean tutorialShow) {
        this.tutorialShow = tutorialShow;
    }

    public boolean isReview() {
        return review;
    }

    public void setReview(boolean review) {
        this.review = review;
    }

    public int getReviewRemain() {
        return reviewRemain;
    }

    public void setReviewRemain(int reviewRemain) {
        this.reviewRemain = reviewRemain;
    }

    public void reduceReviewRemain() {
        if (reviewRemain > 0) {
            reviewRemain--;
        }
    }

    public int getLaunchTime() {
        return launchTime;
    }

    public void addLaunchTime() {
        launchTime++;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void addPlayTime() {
        playTime++;
    }

    public int getExceptionBackward() {
        return exceptionBackward;
    }

    public void addExceptionBackward() {
        exceptionBackward++;
    }

    public void exceptionOccurred() {
        exceptionBackward = 0;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public boolean isVibe() {
        return vibe;
    }

    public void setVibe(boolean vibe) {
        this.vibe = vibe;
    }

    public int getFirstScore() {
        return firstScore;
    }

    public void setFirstScore(int firstScore) {
        this.firstScore = firstScore;
    }

    public int getSecondScore() {
        return secondScore;
    }

    public void setSecondScore(int secondScore) {
        this.secondScore = secondScore;
    }

    public int getThirdScore() {
        return thirdScore;
    }

    public void setThirdScore(int thirdScore) {
        this.thirdScore = thirdScore;
    }

    public long getGrandScore() {
        return grandScore;
    }

    public void addGrandScore(int score) {
        grandScore += (long) score;
    }

    public void mute() {
        mute = true;
    }

    public void unMute() {
        mute = false;
    }

    public void vibeOn() {
        vibe = true;
    }

    public void vibeOff() {
        vibe = false;
    }
}
