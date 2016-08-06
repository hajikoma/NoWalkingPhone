package com.hajikoma.nowalkingphone.screen;

import java.util.List;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.Player;
import com.hajikoma.nowalkingphone.Score;
import com.hajikoma.nowalkingphone.Settings;
import com.hajikoma.nowalkingphone.Walker;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Input.GestureEvent;
import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.Text;

/**
 * ゲームの結果を表示するスクリーン
 */
public class ResultScreen extends Screen {


    /** コンティニューボタン描画先 */
    Rect continueDstArea = new Rect(40, 1040, 40 + 400, 1040 + 200);
    /** タイトルへボタン描画先 */
    private Rect goMenuDstArea = new Rect(40 + 400 + 30, 1040, 40 + 400 + 10 + 200, 1020 + 200);

    /** 共通して使用するゲームクラス */
    private final Game game;
    /** Graphicインスタンス */
    private final Graphics gra;
    /** Textインスタンス */
    private final Text txt;

    /** スコア */
    private Score sc;

    /** 経過時間 */
    private float timer;
    /** 個別結果を表示する際の効果音を鳴らしたかどうかのフラグ */
    private boolean[] isSoundPlayed = new boolean[]{false, false, false, false};


    /** ResultScreenを生成する */
    public ResultScreen(Game game, Score sc) {
        super(game);
        this.game = game;
        this.sc = sc;

        gra = game.getGraphics();
        txt = game.getText();

        //固有グラフィックの読み込み
        Assets.result_bg = gra.newPixmap("others/result_bg.jpg", PixmapFormat.RGB565);

        //データを保存
        Assets.ud.setGameResult(sc);
        Settings.save(game.getFileIO(), Assets.ud);
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

        drawGraphicalNumber(sc.score, 80, 220, 250, 6);
        drawGraphicalNumber(sc.maxCombo, 80, 220, 450, 6);
        drawGraphicalNumber(sc.beatCount, 80, 220, 650, 6);

        if (timer >= 4.5f) {
            txt.drawText("コンティニュー？", 220, 950, 500, Assets.map_style.get("big"));
        }


        // タッチイベントの処理
        for (int gi = 0; gi < gestureEvents.size(); gi++) {
            GestureEvent ges = gestureEvents.get(gi);

            if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP) {
                game.setScreen(new GameScreen(game));
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
        Assets.result_bg = null;
        Assets.icon_button_result = null;
    }

    /** 効果音を一度だけ再生するヘルパー */
    private void soundPlayOnce(int flagIndex) {
        if (!isSoundPlayed[flagIndex]) {
            playSound(Assets.result_score, 0.5f);
            isSoundPlayed[flagIndex] = true;
        }
    }

    @Override
    public String toString() {
        return "ResultScreen";
    }

}
