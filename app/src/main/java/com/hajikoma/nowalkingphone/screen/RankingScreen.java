package com.hajikoma.nowalkingphone.screen;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.DBManager;
import com.hajikoma.nowalkingphone.NoWalkingPhoneGame;
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
    private Game game;
    private Graphics gra;
    private Text txt;

    /** ランキング用フォント（大文字） */
    private Paint rankingLarge;
    /** ランキング用フォント（小文字） */
    private Paint rankingSmall;

    /** scoresテーブルの値 */
    Map<String, ArrayList<String>> scores = new LinkedHashMap<>();
    /** 1～10位のスコアscoresテーブルの値 */
    private int[] topTen = new int[10];
    /** ユーザーの順位（表示用） */
    private String userRankStr;
    /** ランキングデータを処理したかどうか */
    private boolean isScoreProcessed = false;


    /** RankingScreenを生成する */
    public RankingScreen(Game game) {
        super(game);
        this.game = game;
        gra = game.getGraphics();
        txt = game.getText();

        rankingLarge = new Paint();
        rankingLarge.setTextSize(NoWalkingPhoneGame.FONT_SIZE_M);
        rankingLarge.setAntiAlias(true);
        rankingLarge.setColor(Color.WHITE);

        rankingSmall = new Paint();
        rankingSmall.setTextSize(NoWalkingPhoneGame.FONT_SIZE_S);
        rankingSmall.setAntiAlias(true);
        rankingSmall.setColor(Color.WHITE);

        // 固有グラフィックの読み込み
        Assets.bg_ranking = gra.newPixmap("others/bg_ranking.jpg", PixmapFormat.RGB565);
    }


    @Override
    public void update(float deltaTime) {

    }

    /** 結果と必要に応じて広告を表示 */
    @Override
    public void present(float deltaTime) {
        List<GestureEvent> gestureEvents = game.getInput().getGestureEvents();
        game.getInput().getKeyEvents();

        gra.drawPixmap(Assets.bg_ranking, 0, 0);


        // スコアデータの処理
        boolean isScoreLoaded = ((AndroidGame) game).dbManager.isScoreLoaded();
        if (isScoreLoaded) {
            if(!isScoreProcessed) {
                processScore();
            }

            for (int ri = 0; ri < topTen.length; ri++) {
                if (ri < 3) {
                    txt.drawText(Integer.toString(topTen[ri]), 260, 235 + ri * 80, 700, rankingLarge);
                } else {
                    txt.drawText(Integer.toString(topTen[ri]), 260, 460 + (ri - 3) * 60, 700, rankingSmall);
                }
            }
        } else {
            userRankStr = "-";
            if (!((AndroidGame) game).isNetworkConnected()) {
                txt.drawText("データの取得に失敗しました", 70, 500, 700, Assets.style_general_white);
                txt.drawText("ネットワーク接続を確認して下さい", 40, 600, 700, Assets.style_general_white);
            } else {
                txt.drawText("ランキング取得中...", 200, 500, 700, Assets.style_general_white);
            }
        }

        txt.drawText(userRankStr, 200, 1030, 280, rankingSmall);
        txt.drawText(Integer.toString(Assets.ud.getFirstScore()), 260, 1130, 700, rankingLarge);

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
        Assets.bg_ranking = null;
    }

    @Override
    public String toString() {
        return "ResultScreen";
    }


    /** scoreデータをランキング用に処理する */
    private void processScore() {
        scores = ((AndroidGame) game).dbManager.getLoadedScore();

        // 上位10スコアを格納
        int rank = 1;
        for (String score : scores.keySet()) {
            if (rank <= 10) {
                int size = scores.get(score).size();
                for (int i = 0; i < size; i++) {
                    topTen[rank - 1] = Integer.valueOf(score);
                    rank++;
                    if (rank > 10) {
                        break;
                    }
                }
            } else {
                break;
            }
        }

        // ユーザーの順位を計算
        rank = 1;
        int firstScore = Assets.ud.getFirstScore();
        int userRank = DBManager.MAX_FETCH_SCORES;
        if (scores.containsKey(String.valueOf(firstScore))) {
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
        userRankStr = userRank < 1000 ? userRank + "位" : userRank + "位以下";

        isScoreProcessed = true;
    }
}
