package com.hajikoma.nowalkingphone.screen;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.Walker;
import com.hajikoma.nowalkingphone.WalkerManager;
import com.hajikoma.nowalkingphone.framework.Audio;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Input.GestureEvent;
import com.hajikoma.nowalkingphone.framework.Music;
import com.hajikoma.nowalkingphone.framework.Pixmap;
import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.Vibrate;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * タイトル表示、各種画面へのメニュー表示
 */
public class TitleScreen extends Screen {

    /** 共通して使用するインスタンス */
    private final Game game;
    private final Graphics gra;
    private final Audio aud;
    private final Vibrate vib;

    /** スタートボタン描画先 */
    private Rect startDstArea = new Rect(80, 700, 640, 700 + 150);
    /** ランキングボタン描画先 */
    private Rect rankingDstArea = new Rect(80, 890, 640, 890 + 100);
    /** 遊びかたボタン描画先 */
    private Rect tutorialDstArea = new Rect(80, 1030, 640, 1030 + 100);
    /** クレジットへ描画先 */
    private Rect creditDstArea = new Rect(0, 1180, 400, 1280);
    /** バイブボタン描画先 */
    private Rect vibDstArea = new Rect(560, 1190, 560 + 80, 1190 + 80);

    /** ランダムな数を得るのに使用 */
    private Random random = new Random();

    /** Walker */
    private ArrayList<Walker> walkers = new ArrayList<>();
    /** WalkerManager */
    private WalkerManager manager = new WalkerManager(1);

    /** BGM */
    private Music bgm;

    /** 固有画像 */
    private Pixmap bg;
    private Pixmap icon_settings;


    /** TitleScreenを生成する */
    public TitleScreen(Game game) {
        super(game);
        this.game = game;
        gra = game.getGraphics();
        aud = game.getAudio();
        vib = game.getVibrate();

        // ランキングデータを先行して読み込み
        if (((AndroidGame) game).isNetworkConnected()) {
            ((AndroidGame) game).dbManager.fetchScoresDataUnsync();
        }

        // BGM
        bgm = aud.newMusic("music/retrogamecenter.mp3");
        bgm.setVolume(0.2f);
        bgm.setLooping(true);

        //固有グラフィックの読み込み
        bg = gra.newPixmap("bg/bg_title.jpg", PixmapFormat.RGB565);
        icon_settings = gra.newPixmap("others/settings.png", PixmapFormat.ARGB4444);
    }

    @Override
    public void update(float deltaTime) {
        List<GestureEvent> gestureEvents = game.getInput().getGestureEvents();
        game.getInput().getKeyEvents();

        // タッチイベントの処理
        for (int gi = 0; gi < gestureEvents.size(); gi++) {
            GestureEvent ges = gestureEvents.get(gi);

            if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP) {
                if (isBounds(ges, startDstArea)) {
                    playSound(Assets.decision15, 1.0f);
                    game.setScreen(new GameScreen(game, false));
                    break;
                } else if (isBounds(ges, rankingDstArea)) {
                    playSound(Assets.decision15, 1.0f);
                    game.setScreen(new RankingScreen(game));
                    break;
                } else if (isBounds(ges, tutorialDstArea)) {
                    playSound(Assets.decision15, 1.0f);
                    game.setScreen(new TutorialScreen(game));
                    break;
                } else if (isBounds(ges, creditDstArea)) {
                    playSound(Assets.decision15, 1.0f);
                    game.setScreen(new CreditScreen(game));
                    break;
                } else if (isBounds(ges, vibDstArea)) {
                    if (Assets.ud.isVibe()) {
                        Assets.ud.vibeOff();
                        break;
                    } else {
                        Assets.ud.vibeOn();
                        doVibrate(vib, Assets.vibShortOnce);
                        break;
                    }
                }
            }
        }
    }


    @Override
    public void present(float deltaTime) {
        playMusic(bgm);

        // 背景の描画
        gra.drawPixmap(bg, 0, 0);

        // Walkerの処理
        for (int i = 0; i < walkers.size(); i++) {
            if (walkers.get(i).getState() == Walker.ActionType.VANISH) {
                walkers.remove(i);
            }
        }
        if (walkers.size() < manager.maxWalker) {
            int left = 100 + random.nextInt(AndroidGame.TARGET_WIDTH - 100 - 150);
            walkers.add(manager.getSomeWalker(new Rect(left, 370, left + 100, 470)));
        }
        for (Walker walker : walkers) {
            walker.action(deltaTime);
        }

        // 共通部分の描画
        if (Assets.ud.isVibe()) {
            gra.drawPixmap(icon_settings, vibDstArea, 0, 200, 200, 200);
        } else {
            gra.drawPixmap(icon_settings, vibDstArea, 200, 200, 200, 200);
        }
    }


    /** 一時停止状態をセットする */
    @Override
    public void pause() {
        if (bgm.isPlaying()) {
            bgm.pause();
        }
    }


    /** 再開時の処理 */
    @Override
    public void resume() {
        if (!bgm.isPlaying()) {
            playMusic(bgm);
        }
    }


    /** 固有の参照を明示的に切る */
    @Override
    public void dispose() {
        bgm.dispose();
    }


    @Override
    public String toString() {
        return "TitleScreen";
    }
}
