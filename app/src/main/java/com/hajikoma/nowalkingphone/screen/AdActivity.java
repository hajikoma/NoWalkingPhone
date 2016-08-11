package com.hajikoma.nowalkingphone.screen;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.ad_stir.videoreward.AdstirVideoReward;
import com.ad_stir.videoreward.AdstirVideoRewardListener;

/**
 * 広告表示アクティビティ
 */
public class AdActivity extends Activity {


    private boolean isRewarded = false;
    private AdstirVideoReward adstirVideoReward;

    // リスナーの定義
    private AdstirVideoRewardListener listener = new AdstirVideoRewardListener() {
        public void onLoad(int spot_no) {
            Log.e("AD", "fail");
        }

        public void onFailed(int spot_no) {
            Log.e("AD", "fail");
        }

        public void onStart(int spot_no) {
            Log.e("AD", "fail");
        }

        public void onStartFailed(int spot_no) {
            Log.e("AD", "fail");
        }

        public void onFinished(int spot_no) {
            Log.e("AD", "fail");
        }

        public void onReward(int spot_no) {
            isRewarded = true;
        }

        public void onRewardCanceled(int spot_no) {
            Log.e("AD", "fail");
        }

        public void onClose(int spot_no) {
            Log.e("AD", "fail");
        }
    };


    @Override
    // 広告の読み込み
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // このアクティビティで使用するスポットIDについて、初期化処理を行います。
        int[] spotIds = {1};
        AdstirVideoReward.init(this, "MEDIA-989f9626", spotIds);
        // スポットIDごとにインスタンスを生成します。ここでは1についてのみ生成します。
        adstirVideoReward = new AdstirVideoReward(this, "MEDIA-989f9626", 1);
        // 上で定義したリスナーを登録します。
        adstirVideoReward.setAdstirVideoRewardListener(listener);
        // 広告を読み込みます。
        adstirVideoReward.load();
        adstirVideoReward.showRewardVideo();

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
