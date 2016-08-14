package com.hajikoma.nowalkingphone.screen;

import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.NoWalkingPhoneGame;
import com.hajikoma.nowalkingphone.Score;
import com.hajikoma.nowalkingphone.UserData;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Input.GestureEvent;
import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.Text;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;

/**
 * ゲームの結果を表示するスクリーン
 */
public class ResultScreen extends Screen {


    /** コンティニューボタン描画先 */
    private Rect continueDstArea = new Rect(40, 1040, 40 + 400, 1040 + 200);
    /** タイトルへボタン描画先 */
    private Rect goMenuDstArea = new Rect(40 + 400 + 30, 1040, 40 + 400 + 10 + 200, 1020 + 200);

    /** 共通して使用するインスタンス */
    private final Game game;
    private final Graphics gra;
    private final Text txt;

    /** スコア */
    private Score sc;

    /** 経過時間 */
    private float timer;
    /** 個別結果を表示する際の効果音を鳴らしたかどうかのフラグ */
    private boolean[] isSoundPlayed = new boolean[]{false, false, false, false};

    /** 広告表示フラグ */
    private boolean isAdShow = false;
    /** レビュー依頼表示フラグ */
    private boolean isReviewShow = false;


    /** ResultScreenを生成する */
    public ResultScreen(Game game, Score sc) {
        super(game);
        this.game = game;
        this.sc = sc;

        gra = game.getGraphics();
        txt = game.getText();

        UserData ud = Assets.ud;

        // 固有グラフィックの読み込み
        Assets.bg_result = gra.newPixmap("others/bg_result.jpg", PixmapFormat.RGB565);

        // データを保存
        if (sc.score >= ud.getFirstScore()) {
            ((AndroidGame)game).dbManager.saveFirstScore(ud.getUserId(), ud.getFirstScore(), sc.score); //DBにも保存
            ud.setThirdScore(ud.getSecondScore());
            ud.setSecondScore(ud.getFirstScore());
            ud.setFirstScore(sc.score);
        } else if (sc.score >= ud.getSecondScore()) {
            ud.setThirdScore(ud.getSecondScore());
            ud.setSecondScore(sc.score);
        } else if (sc.score >= ud.getThirdScore()) {
            ud.setThirdScore(sc.score);
        }
        Assets.ud.addPlayTime();
        Assets.ud.reduceReviewRemain();
        Assets.ud.addExceptionBackward();
        Assets.ud.addGrandScore(sc.score);
        Assets.ud.saveAllToPref(getSharedPreference());

        // 広告
        ((NoWalkingPhoneGame) game).adActivityPrepare();
    }

    @Override
    public void update(float deltaTime) {

    }

    /** 結果と必要に応じて広告を表示 */
    @Override
    public void present(float deltaTime) {
        List<GestureEvent> gestureEvents = game.getInput().getGestureEvents();
        game.getInput().getKeyEvents();

        gra.drawPixmap(Assets.bg_result, 0, 0);

        // 結果
        drawGraphicalNumber(sc.beatCount, 60, 210, 180, 8);
        drawGraphicalNumber(sc.maxCombo, 60, 210, 280, 8);
        drawGraphicalNumber(sc.score, 80, 20, 420, 9);

        // ユーザーランキング
        txt.drawText(Integer.toString(Assets.ud.getFirstScore()), 280, 710, 700, Assets.style_general_black);
        txt.drawText(Integer.toString(Assets.ud.getSecondScore()), 280, 785, 700, Assets.style_general_black);
        txt.drawText(Integer.toString(Assets.ud.getThirdScore()), 280, 865, 700, Assets.style_general_black);

        if (timer >= 3.0f) {
            // 一定間隔、かつ例外発生から一定回数以上遊んでいる場合、レビューを依頼
            if (!isReviewShow && Assets.ud.getReviewRemain() == 0 && Assets.ud.getExceptionBackward() >= 5) {
                final NoWalkingPhoneGame nwp = (NoWalkingPhoneGame) game;
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        nwp.showYesNoLaterDialog(
                                "感想をお聞かせください！",
                                "アプリをご使用いただきありがとうございます。楽しんで頂けたら、ぜひ評価で応援をお願いします",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Assets.ud.setReview(true);
                                        Assets.ud.setReviewRemain(100);

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
                                        Assets.ud.setReviewRemain(15);

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
                nwp.postRunnable(run);
                isReviewShow = true;
            } else if (!isAdShow) {
                ((NoWalkingPhoneGame) game).adActivityForward();
                isAdShow = true;
            }

            // タッチイベントの処理
            for (int gi = 0; gi < gestureEvents.size(); gi++) {
                GestureEvent ges = gestureEvents.get(gi);

                if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP) {
//                    game.setScreen(new GameScreen(game, sc));
                    game.setScreen(new TitleScreen(game));
                }
            }
        }

        timer += deltaTime;
    }

    /** このスクリーンでの処理はない */
    @Override
    public void pause() {
    }

    /** このスクリーンでの処理はない */
    @Override
    public void resume() {
    }

    /** 固有の参照を明示的に切る */
    @Override
    public void dispose() {
        Assets.bg_result = null;
    }

    /** 効果音を一度だけ再生するヘルパー */
    private void soundPlayOnce(int flagIndex) {
        if (!isSoundPlayed[flagIndex]) {
            //playSound(Assets.result_score, 0.5f);
            isSoundPlayed[flagIndex] = true;
        }
    }

    @Override
    public String toString() {
        return "ResultScreen";
    }

}
