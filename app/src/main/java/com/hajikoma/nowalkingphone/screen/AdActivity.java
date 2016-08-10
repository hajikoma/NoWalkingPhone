package com.hajikoma.nowalkingphone.screen;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;

import com.ad_stir.videoreward.AdstirVideoReward;
import com.ad_stir.videoreward.AdstirVideoRewardListener;
import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.Score;
import com.hajikoma.nowalkingphone.UserData;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Input.GestureEvent;
import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.Text;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;

import java.util.List;

/**
 * ゲームの結果を表示するスクリーン
 */
public class AdActivity extends Activity {


    private boolean isRewarded = false;
    private AdstirVideoReward adstirVideoReward;

    // リスナーの定義
    private AdstirVideoRewardListener listener = new AdstirVideoRewardListener() {
        public void onLoad(int spot_no) {
            Log.e("AD", "fail");
        }

        ;

        public void onFailed(int spot_no) {
            Log.e("AD", "fail");
        }

        ;

        public void onStart(int spot_no) {
            Log.e("AD", "fail");
        }

        ;

        public void onStartFailed(int spot_no) {
            Log.e("AD", "fail");
        }

        ;

        public void onFinished(int spot_no) {
            Log.e("AD", "fail");
        }

        ;

        public void onReward(int spot_no) {
            isRewarded = true;
        }

        ;

        public void onRewardCanceled(int spot_no) {
            Log.e("AD", "fail");
        }

        ;

        public void onClose(int spot_no) {
            Log.e("AD", "fail");
        }

        ;
    };


    @Override
    // 広告の読み込み
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // サーバーサイドで成果通知を受け取る場合、メディアユーザーIDを設定します。
        // メディアユーザーIDは、AdstirVideoReward.init()の実行前にしか機能しません。
        // ここでは例として、メールアドレスを指定していますが、任意のIDが利用可能です。
        AdstirVideoReward.setMediaUserID("test@gmail.com");
        // このアクティビティで使用するスポットIDについて、初期化処理を行います。
        int[] spotIds = {1};
        AdstirVideoReward.init(this, "MEDIA-989f9626", spotIds);
        // スポットIDごとにインスタンスを生成します。ここでは1についてのみ生成します。
        adstirVideoReward = new AdstirVideoReward(this, "MEDIA-989f9626", 1);
        // 上で定義したリスナーを登録します。
        adstirVideoReward.setAdstirVideoRewardListener(listener);
        // 広告を読み込みます。
        adstirVideoReward.load();

    }

    // 広告の一時停止等
    @Override
    protected void onResume() {
        if (adstirVideoReward != null) adstirVideoReward.resume();
        if (isRewarded) {
            isRewarded = false;
            Log.d("AD", "Reward success.");
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (adstirVideoReward != null) adstirVideoReward.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (adstirVideoReward != null) adstirVideoReward.destroy();
        super.onDestroy();
    }
}
