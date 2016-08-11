package com.hajikoma.nowalkingphone.screen;

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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ランキングを表示するスクリーン
 */
public class RankingScreen extends Screen {


    /** コンティニューボタン描画先 */
    private Rect continueDstArea = new Rect(40, 1040, 40 + 400, 1040 + 200);
    /** タイトルへボタン描画先 */
    private Rect goMenuDstArea = new Rect(40 + 400 + 30, 1040, 40 + 400 + 10 + 200, 1020 + 200);

    /** 共通して使用するインスタンス */
    private final Game game;
    private final Graphics gra;
    private final Text txt;

    // scoresテーブルの値
    Map<String, ArrayList<String>> scores = new LinkedHashMap<>();
    // 順位
    private int userRank = 1000;


    /** RankingScreenを生成する */
    public RankingScreen(Game game) {
        super(game);
        this.game = game;
        gra = game.getGraphics();
        txt = game.getText();

        UserData ud = Assets.ud;

        scores = ((AndroidGame) game).dbManager.fetchScoresData();

        // 固有グラフィックの読み込み
        Assets.result_bg = gra.newPixmap("others/bg.jpg", PixmapFormat.RGB565);

        // データを取得
        int firstScore = ud.getFirstScore();
        int rank = 1;
        for (String score : scores.keySet()) {
            // 1000位までカウント
            if (rank >= 1000) {
                break;
            }

            if (firstScore < Integer.valueOf(score)) {
                // そのscoreの人数分カウントアップ
                rank += scores.get(score).size();
            } else {
                userRank = rank;
                break;
            }
        }
    }

    @Override
    public void update(float deltaTime) {

    }

    /** 結果と必要に応じて広告を表示 */
    @Override
    public void present(float deltaTime) {
        List<GestureEvent> gestureEvents = game.getInput().getGestureEvents();
        game.getInput().getKeyEvents();

        gra.drawPixmap(Assets.result_bg, 0, 0);

        txt.drawText("全国ランキング", 80, 80, 700, Assets.map_style.get("title"));

        int counter = 1;
        for (String score : scores.keySet()) {
            if (counter <= 3) {
                txt.drawText(counter + "位：" + score, 40, 200 + counter * 80, 700, Assets.map_style.get("score"));
            } else {
                txt.drawText(counter + "位：" + score, 40, 200 + counter * 80, 700, Assets.map_style.get("title"));
            }

            if (counter < 10) {
                counter++;
            } else {
                break;
            }
        }

        String rankStr = userRank < 1000 ? userRank + "位" : userRank + "位以下";
        txt.drawText("あなたの順位", 80, 1000, 700, Assets.map_style.get("title"));
        txt.drawText(rankStr, 40, 1120, 280, Assets.map_style.get("title"));
        txt.drawText(Integer.toString(Assets.ud.getFirstScore()), 340, 1100, 700, Assets.map_style.get("score"));

        // タッチイベントの処理
        for (int gi = 0; gi < gestureEvents.size(); gi++) {
            GestureEvent ges = gestureEvents.get(gi);

            if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP) {
                game.setScreen(new TitleScreen(game));
            }
        }

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
        Assets.result_bg = null;
    }

    @Override
    public String toString() {
        return "ResultScreen";
    }

}
