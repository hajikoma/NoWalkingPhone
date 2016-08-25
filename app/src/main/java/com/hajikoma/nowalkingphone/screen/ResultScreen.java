package com.hajikoma.nowalkingphone.screen;

import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.NoWalkingPhoneGame;
import com.hajikoma.nowalkingphone.Score;
import com.hajikoma.nowalkingphone.UserData;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Input.GestureEvent;
import com.hajikoma.nowalkingphone.framework.Pixmap;
import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.Text;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;

/**
 * ゲームの結果を表示するスクリーン
 */
public class ResultScreen extends Screen {

    /** タイトルへボタン当たり矩形 */
    private Rect goTitleDstArea = new Rect(0, 1100, 280, 1280);
    /** コンティニューボタン当たり矩形 */
    private Rect continueDstArea = new Rect(320, 1080, 720, 1280);

    /** 共通して使用するインスタンス */
    private final Graphics gra;
    private final Text txt;

    /** スコア */
    private Score sc = Assets.score;

    /** 経過時間 */
    private float timer;

    /** 固有画像 */
    private Pixmap bg;

    /** レビュー依頼表示フラグ */
    private boolean isReviewShow = false;
    /** ランキング更新フラグ。順に1位、2位、3位 */
    private boolean isRankedIn[] = new boolean[]{false, false, false};


    /**
     * ResultScreenを生成する
     */
    public ResultScreen(Game game) {
        super(game);

        //広告を先行して読み込み
        if (((AndroidGame) game).isNetworkConnected()) {
            ((AndroidGame) game).prepareAd();
        }

        gra = game.getGraphics();
        txt = game.getText();

        // 固有グラフィックの読み込み
        bg = gra.newPixmap("bg/bg_result.jpg", PixmapFormat.RGB565);

        // データを保存
        UserData ud = Assets.ud;
        if (sc.score >= 1) {
            if (sc.score >= ud.getFirstScore()) {
                isRankedIn[0] = true;
                playSound(Assets.peoplePerformanceCheer1, 1.0f);
                ((AndroidGame) game).dbManager.saveFirstScore(ud.getUserId(), ud.getFirstScore(), sc.score); //DBにも保存
                ud.setThirdScore(ud.getSecondScore());
                ud.setSecondScore(ud.getFirstScore());
                ud.setFirstScore(sc.score);
            } else if (sc.score >= ud.getSecondScore()) {
                isRankedIn[1] = true;
                playSound(Assets.peoplePerformanceCheer1, 1.0f);
                ud.setThirdScore(ud.getSecondScore());
                ud.setSecondScore(sc.score);
            } else if (sc.score >= ud.getThirdScore()) {
                isRankedIn[2] = true;
                playSound(Assets.peoplePerformanceCheer1, 1.0f);
                ud.setThirdScore(sc.score);
            }
        }
        ud.addPlayTime();
        ud.reduceReviewRemain();
        ud.addExceptionBackward();
        ud.addGrandScore(sc.score);
        ud.saveAllToPref(getSharedPreference());
    }


    @Override
    public void update(float deltaTime) {
    }


    /**
     * 結果と、必要に応じて広告を表示
     */
    @Override
    public void present(float deltaTime) {
        List<GestureEvent> gestureEvents = game.getInput().getGestureEvents();
        game.getInput().getKeyEvents();

        gra.drawPixmap(bg, 0, 0);

        // 結果
        drawGraphicalNumber(sc.beatCount, 60, 210, 180, 8);
        drawGraphicalNumber(sc.maxCombo, 60, 210, 280, 8);
        drawGraphicalNumber(sc.score, 80, 20, 420, 9);

        // ユーザーランキング
        String message = "コンティニューしてハイスコア更新を目指そう！";
        if (isRankedIn[0]) {
            if (timer % 0.6f > 0.3f) {
                txt.drawText(Integer.toString(Assets.ud.getFirstScore()), 280, 710, 700, Assets.style_general_black);
            }
            message = "ハイスコア更新おめでとう！";
            // コンティニュー表示を隠す
            gra.drawRect(new Rect(320, 1180, 720, 1280), Color.WHITE);
        } else {
            txt.drawText(Integer.toString(Assets.ud.getFirstScore()), 280, 710, 700, Assets.style_general_black);
        }
        if (isRankedIn[1]) {
            if (timer % 0.6f > 0.3f) {
                txt.drawText(Integer.toString(Assets.ud.getSecondScore()), 280, 785, 700, Assets.style_general_black);
            }
            message = "もう少しでハイスコア更新だ！";
        } else {
            txt.drawText(Integer.toString(Assets.ud.getSecondScore()), 280, 785, 700, Assets.style_general_black);
        }
        if (isRankedIn[2]) {
            if (timer % 0.6f > 0.3f) {
                txt.drawText(Integer.toString(Assets.ud.getThirdScore()), 280, 865, 700, Assets.style_general_black);
            }
            message = "さらなるスコア更新を目指せ！";
        } else {
            txt.drawText(Integer.toString(Assets.ud.getThirdScore()), 280, 865, 700, Assets.style_general_black);
        }
        txt.drawText(message, 80, 980, 550, Assets.style_general_white_big);

        if (timer >= 3.0f) {
            // 一定回数以上遊んでおり、ランキング更新時、レビューを依頼
            if (!isReviewShow
                    && Assets.ud.getReviewRemain() == 0
                    && Assets.ud.getExceptionBackward() >= 5
                    && (isRankedIn[0] || isRankedIn[1] || isRankedIn[2])) {
                final NoWalkingPhoneGame nwp = (NoWalkingPhoneGame) game;
                nwp.postRunnable(getReviewRunnable(nwp));
                isReviewShow = true;
            }

            // タッチイベントの処理
            for (int gi = 0; gi < gestureEvents.size(); gi++) {
                GestureEvent ges = gestureEvents.get(gi);

                if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP) {
                    if (!isRankedIn[0] && isBounds(ges, continueDstArea)) {
                        // 広告の表示
                        playSound(Assets.decision15, 1.0f);
                        if (((AndroidGame) game).isAdLoaded) {
                            ((NoWalkingPhoneGame) game).showAd();
                        } else {
                            if (((AndroidGame) game).isNetworkConnected()) {
                                ((AndroidGame) game).prepareAd();
                            } else {
                                final NoWalkingPhoneGame nwp = (NoWalkingPhoneGame) game;
                                ((NoWalkingPhoneGame) game).showConfirmDialog(
                                        "動画が取得できません。",
                                        "動画の取得に失敗しました。ネットワーク接続を確認して下さい。",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                nwp.getCurrentScreen().resume();
                                                nwp.getRenderView().resume();
                                            }
                                        }
                                );
                            }
                        }

                        break;

                    } else if (isBounds(ges, goTitleDstArea)) {
                        // タイトルへ戻る
                        playSound(Assets.decision15, 1.0f);
                        game.setScreen(new TitleScreen(game));
                        break;
                    }
                }
            }
        }

        timer += deltaTime;
    }


    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    /** 固有の参照を明示的に切る */
    @Override
    public void dispose() {
    }

    @Override
    public String toString() {
        return "ResultScreen";
    }


    /** レビュー依頼を表示するインスタンスを生成する */
    @NonNull
    private Runnable getReviewRunnable(final NoWalkingPhoneGame nwp) {
        return new Runnable() {
            @Override
            public void run() {
                nwp.showYesNoLaterDialog(
                        "感想をお聞かせください！",
                        "アプリをご使用いただきありがとうございます。楽しんで頂けたら、ぜひ評価で応援をお願いします",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Assets.ud.setReview(true);
                                Assets.ud.setReviewRemain(50);

                                // 描画を再開
                                nwp.getCurrentScreen().resume();
                                nwp.getRenderView().resume();

                                // playストアを開く
                                Intent googlePlayIntent = new Intent(Intent.ACTION_VIEW);
                                googlePlayIntent.setData(Uri.parse("market://details?id=com.xxx.myapp"));
                                nwp.startActivity(googlePlayIntent);
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Assets.ud.setReviewRemain(10);

                                // 描画を再開
                                nwp.getCurrentScreen().resume();
                                nwp.getRenderView().resume();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Assets.ud.setReviewRemain(5);

                                // 描画を再開
                                nwp.getCurrentScreen().resume();
                                nwp.getRenderView().resume();
                            }
                        }
                );
            }
        };
    }
}
