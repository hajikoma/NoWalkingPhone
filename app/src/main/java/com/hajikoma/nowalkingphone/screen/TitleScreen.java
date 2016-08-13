package com.hajikoma.nowalkingphone.screen;

import android.graphics.Color;
import android.graphics.Rect;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.SmaphoWalker;
import com.hajikoma.nowalkingphone.Walker;
import com.hajikoma.nowalkingphone.WalkerManager;
import com.hajikoma.nowalkingphone.framework.Audio;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Input.GestureEvent;
import com.hajikoma.nowalkingphone.framework.Music;
import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.Text;
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
    private final Text txt;
    private final Audio aud;
    private final Vibrate vib;

    /** スタートボタン描画先 */
    private Rect startDstArea = new Rect(80, 700, 640, 700 + 150);
    /** ランキングボタン描画先 */
    private Rect rankingDstArea = new Rect(80, 890, 640, 890 + 150);
    /** 遊びかたボタン描画先 */
    private Rect tutorialDstArea = new Rect(80, 1080, 640, 1080 + 100);
    /** ミュートボタン描画先 */
    private Rect muteDstArea = new Rect(80, 1200, 80 + 60, 1200 + 60);
    /** バイブボタン描画先 */
    private Rect vibDstArea = new Rect(580, 1200, 580 + 60, 1200 + 60);

    /** 経過時間 */
    private float timer;
    /** ランダムな数を得るのに使用 */
    private Random random = new Random();

    /** Walker */
    private ArrayList<Walker> walkers = new ArrayList<>();
    /** WalkerManager */
    private WalkerManager manager = new WalkerManager();
    /** Walkerの同時出現上限数 */
    private int maxWalker = 1;

    /** BGM */
    private Music bgm;


    /** TitleScreenを生成する */
    public TitleScreen(Game game) {
        super(game);
        this.game = game;
        gra = game.getGraphics();
        txt = game.getText();
        aud = game.getAudio();
        vib = game.getVibrate();

        // ランキングデータを先行して読み込み
        if (((AndroidGame) game).isNetworkConnected()) {
            ((AndroidGame) game).dbManager.fetchScoresDataUnsync();
        }

        // Walkerのセットアップ
        manager.addWalker(manager.BASIC, new SmaphoWalker("歩きスマホ", 1, 3, 2, "普通なのがとりえ", 2, Assets.walker_man, 500, 500, null));
        manager.addWalker(manager.FRIEND, new Walker("おばあさん", 1, 2, 1, "善良な市民。タップ禁止", -5, Assets.walker_grandma, 500, 500, null));
        manager.addWalker(manager.SCHOOL, new SmaphoWalker("歩き小学生", 1, 5, 1, "すばしっこくぶつかりやすい", 3, Assets.walker_boy, 500, 500, null));
        manager.addWalker(manager.WOMAN, new SmaphoWalker("歩き系女子", 1, 3, 2, "たちどまったりふらついたり", 3, Assets.walker_girl, 500, 500, null));
        manager.addWalker(manager.MANIA, new SmaphoWalker("歩きオタク", 2, 2, 3, "とろいがでかくて痛い", 4, Assets.walker_mania, 500, 500, null));
        manager.addWalker(manager.MONSTER, new SmaphoWalker("歩き外人さん", 3, 3, 2, "日本に歩きスマホしにきた", 4, Assets.walker_visitor, 500, 500, null));
        manager.addWalker(manager.CAR, new Walker("歩きくるま", 999, 10, 5, "もはやテロリスト", 20, Assets.walker_car, 500, 500, null));

        // BGM
        bgm = aud.newMusic("music/retrogamecenter.mp3");
        bgm.setVolume(0.1f);
        bgm.setLooping(true);

        //固有グラフィックの読み込み
        Assets.title_bg = gra.newPixmap("others/bg.jpg", PixmapFormat.RGB565);
        Assets.settings = gra.newPixmap("others/settings.png", PixmapFormat.ARGB4444);
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
                    game.setScreen(new GameScreen(game));
                } else if (isBounds(ges, rankingDstArea)) {
                    playSound(Assets.decision15, 1.0f);
                    game.setScreen(new RankingScreen(game));
                } else if (isBounds(ges, tutorialDstArea)) {
                    playSound(Assets.decision15, 1.0f);
                    game.setScreen(new GameScreen(game));
                } else if (isBounds(ges, muteDstArea)) {
                    if (Assets.ud.isMute()) {
                        Assets.ud.unMute();
                        break;
                    } else {
                        Assets.ud.mute();
                        break;
                    }
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
        timer += deltaTime;
        playMusic(bgm);

        // 背景の描画
        gra.drawPixmap(Assets.title_bg, 0, 0);

        // Walkerの処理
        for (int i = 0; i < walkers.size(); i++) {
            if (walkers.get(i).getState() == Walker.ActionType.VANISH) {
                walkers.remove(i);
            }
        }
        if (walkers.size() < maxWalker) {
            int left = 100 + random.nextInt(AndroidGame.TARGET_WIDTH - 100 - 150);
            walkers.add(manager.getWalker(new Rect(left, 370, left + 100, 470)));
        }
        for (Walker walker : walkers) {
            walker.action(deltaTime);
        }

        // 共通部分の描画
        txt.drawText("歩きスマホ撃退アクション", 80, 100, 540, Assets.map_style.get("score"));
        txt.drawText("スマ歩NO!", 100, 360, 520, Assets.map_style.get("big"));
        gra.drawRoundRect(startDstArea, 15.0f, Color.MAGENTA);
        gra.drawRoundRect(rankingDstArea, 15.0f, Color.YELLOW);
        gra.drawRoundRect(tutorialDstArea, 15.0f, Color.GREEN);
        gra.drawRoundRect(tutorialDstArea, 15.0f, Color.GREEN);
        if(Assets.ud.isMute()){
            gra.drawPixmap(Assets.settings, muteDstArea, 200, 0, 200, 200);
        }else{
            gra.drawPixmap(Assets.settings, muteDstArea, 0, 0, 200, 200);
        }
        if(Assets.ud.isVibe()){
            gra.drawPixmap(Assets.settings, vibDstArea, 0, 200, 200, 200);
        }else{
            gra.drawPixmap(Assets.settings, vibDstArea, 200, 200, 200, 200);
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
        Assets.title_bg = null;
        Assets.settings = null;
        bgm.dispose();
    }


    @Override
    public String toString() {
        return "TitleScreen";
    }
}
