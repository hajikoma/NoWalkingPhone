package com.hajikoma.nowalkingphone.screen;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.Effect;
import com.hajikoma.nowalkingphone.NoWalkingPhoneGame;
import com.hajikoma.nowalkingphone.Player;
import com.hajikoma.nowalkingphone.Score;
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
import com.hajikoma.nowalkingphone.framework.Sound;
import com.hajikoma.nowalkingphone.framework.Text;
import com.hajikoma.nowalkingphone.framework.Vibrate;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

/**
 * ゲームのメインスクリーン。
 */
public class GameScreen extends Screen {

    /** 現在のシーンを表す列挙型 */
    private static enum Scene {
        /** お手入れ開始準備、セットアップに使用 */
        READY,
        /** お手入れ開始直後、カウントダウンに使用 */
        START,
        /** ゲーム中 */
        PLAYING,
        /** 一時停止 */
        PAUSE,
        /** ゲームオーバー */
        GAMEOVER;
    }

    /** 共通して使用するインスタンス */
    private final Game game;
    private final Graphics gra;
    private final Audio aud;
    private final Text txt;
    private final Vibrate vib;

    /** 汎用的に使用するタイマー */
    private float timer;
    /** ランダムな数を得るのに使用 */
    private Random random = new Random();

    /** チュートリアル表示フラグ */
    private boolean[] isTrimTutorialShow = new boolean[5];

    /** 現在のシーン */
    private Scene scene;

    /** Player */
    private Player player;
    /** Playerのステップ操作の受付範囲 */
    private Rect onStepArea = new Rect(0, 1000, NoWalkingPhoneGame.TARGET_WIDTH, NoWalkingPhoneGame.TARGET_HEIGHT);

    /** Walker */
    private ArrayList<Walker> walkers = new ArrayList<>();
    /** WalkerManager */
    private WalkerManager manager = new WalkerManager();
    /** Walkerの同時出現上限数 */
    private int maxWalker = 5;

    /** 各スコアを格納 */
    private Score sc = new Score();
    /** コンボ数の表示座標 */
    private Point comboXY = new Point();
    /** コンボ数の表示座標 */
    private float comboTime = 0.0f;

    /** スマッシュの使用可能回数 */
    private int remainSmash = 3;
    /** スマッシュの最大使用可能回数 */
    private final int MAX_SMASH = 3;
    /** スマッシュアイコンの描画先 */
    private final Rect SMASH_DST_RECT_ARR[] = {
            new Rect(500, 1150, 550, 1200),
            new Rect(570, 1150, 620, 1200),
            new Rect(640, 1150, 690, 1200)
    };

    /** Walkerタップ時の、当たり判定の拡大値 */
    private final int TAP_EXPANTION = 30;

    /** Effect */
    private Effect lifeReduceEffect;
    private Effect smashEffect;

    /** BGM */
    private Music bgm;

    /** 効果音をそのループで再生したかどうか */
    private ArrayList<String> playedSounds;

    /** 効果音マップ */
    private LinkedHashMap<Double, Sound> onTap = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onFling = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onCrash = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onSmash = new LinkedHashMap<>();

    /** アイテムエフェクトに使用する変化するalpha値 */
    private int[] argb = new int[]{0, 0, 0, 0};


    /**
     * GameScreenを生成する
     *
     * @param game 共通のgameアクティビティ
     */
    public GameScreen(Game game) {
        super(game);
        this.game = game;
        gra = game.getGraphics();
        aud = game.getAudio();
        txt = game.getText();
        vib = game.getVibrate();

        // Playerのセットアップ
        Assets.player = gra.newPixmap("player/player_ready.png", PixmapFormat.ARGB4444);
        player = new Player(Assets.player, 500, 500);

        // Walkerのセットアップ
        manager.addWalker(manager.BASIC, new SmaphoWalker("歩きスマホ", 1, 3, 2, "普通なのがとりえ", 2, Assets.walker_man, 500, 500, null));
        manager.addWalker(manager.FRIEND, new Walker("おばあさん", 1, 2, 1, "善良な市民。タップ禁止", -5, Assets.walker_grandma, 500, 500, null));
        manager.addWalker(manager.SCHOOL, new SmaphoWalker("歩き小学生", 1, 5, 1, "すばしっこくぶつかりやすい", 3, Assets.walker_boy, 500, 500, null));
        manager.addWalker(manager.WOMAN, new SmaphoWalker("歩き系女子", 1, 3, 2, "たちどまったりふらついたり", 3, Assets.walker_girl, 500, 500, null));
        manager.addWalker(manager.MANIA, new SmaphoWalker("歩きオタク", 2, 2, 3, "とろいがでかくて痛い", 4, Assets.walker_mania, 500, 500, null));
        manager.addWalker(manager.MONSTER, new SmaphoWalker("歩き外人さん", 3, 3, 2, "日本に歩きスマホしにきた", 4, Assets.walker_visitor, 500, 500, null));
        manager.addWalker(manager.CAR, new Walker("歩きくるま", 999, 10, 5, "もはやテロリスト", 20, Assets.walker_car, 500, 500, null));

        // 固有グラフィックの読み込み
        Assets.trim_bg = gra.newPixmap("others/bg.jpg", PixmapFormat.RGB565);

        // Effect
        lifeReduceEffect = new Effect(Assets.onomatopee, 180, new float[]{0.25f, 0.25f, 0.25f, 0.25f});

        // BGM
        bgm = aud.newMusic("music/me_6777276_Race.mp3");
        bgm.setLooping(true);
        bgm.setVolume(0.01f);

        // 効果音
        onTap.put(0.5, Assets.voice_soko);
        onTap.put(1.0, Assets.punchMiddle2);
        onFling.put(0.5, Assets.voice_mieru);
        onFling.put(1.0, Assets.punchSwing1);
        onSmash.put(0.5, Assets.magicElectron2);
        onSmash.put(1.0, Assets.bomb1);
        onCrash.put(0.5, Assets.voice_nanto);
        onCrash.put(1.0, Assets.punchMiddle2);

        changeScene(Scene.READY);
    }


    /** メインループ内で呼ばれる。ループ内のためインスタンスの生成には慎重を期すこと。 */
    @Override
    public void update(float deltaTime) {
        List<GestureEvent> gestureEvents = game.getInput().getGestureEvents();
        game.getInput().getKeyEvents();

        //共通部分の描画
        gra.drawPixmap(Assets.trim_bg, 0, 0);
        txt.drawText(String.valueOf(sc.level), 20, 100, 200, Assets.map_style.get("score"));
        txt.drawText(String.valueOf(sc.combo), 400, 100, 200, Assets.map_style.get("score"));
        drawGraphicalNumber(sc.score, 90, 20, 220, 9);
        drawLife(player.getInitLife(), player.getDamage());
        drawSmashIcon();

        // bgm
        bgm.play();

        // シーンごとの処理
        switch (scene) {
            case READY://-----------------------------------------------------------------------------------
                // 変数の初期化
                changeScene(Scene.START);
                break;
            //-------------------------------------------------------------------------------------------------

            case START://--------------------------------------------------------------------------------------
                // チュートリアル表示
                changeScene(Scene.PLAYING);
                break;
            //-------------------------------------------------------------------------------------------------

            case PLAYING://-----------------------------------------------------------------------------------

                timer += deltaTime;

                // 再生済み効果音を初期化
                playedSounds = new ArrayList<>();

                // Walkerの状態に応じた処理
                for (int i = 0; i < walkers.size(); i++) {
                    Walker.ActionType walkerState = walkers.get(i).getState();
                    switch (walkerState) {
                        case VANISH:
                            walkers.remove(i);
                            break;
                    }
                }

                // Walkerを出現させる
                if (walkers.size() < maxWalker) {
                    int left = 100 + random.nextInt(AndroidGame.TARGET_WIDTH - 100 - 150);
                    walkers.add(manager.getWalker(new Rect(left, 370, left + 100, 470)));
                }

                // playerの描画
                player.action(deltaTime);
                // bottomの値が小さい（背面にいる）Walkerから描画
                int size = walkers.size();
                HashMap<Integer, Integer> bottomList = new HashMap<>();
                for (int wi = 0; wi < size; wi++) {
                    bottomList.put(walkers.get(wi).getLocation().bottom, wi);
                }
                ArrayList<Integer> keyList = new ArrayList<>(bottomList.keySet());
                Collections.sort(keyList);//昇順
                for (int key : keyList) {
                    walkers.get(bottomList.get(key)).action(deltaTime);
                }

                // Effectの描画
                lifeReduceEffect.play(deltaTime);

                // WalkerとPlayerの衝突処理
                for (Walker walker : walkers) {
                    if (isCrash(walker)) {
                        player.addDamage(walker.getPower());
                        player.setState(Player.ActionType.DAMAGE);
                        walker.setState(Walker.ActionType.CRASH);
                        sc.combo = 0;
                        lifeReduceEffect.on(new Rect(50, 1150, 300, 1230));
                        playSoundOnceRandom("onCrash", onCrash, 1.5f);
                    }
                }

                // タッチイベントの処理
                for (int gi = 0; gi < gestureEvents.size(); gi++) {
                    GestureEvent ges = gestureEvents.get(gi);

                    if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP) {
                        // Walkerのタップ判定
                        for (Walker walker : walkers) {
                            if (isBounds(ges, walker.getLocation(), TAP_EXPANTION)) {
                                walker.addDamage(1);
                                playSoundOnceRandom("onTap", onTap, 1.5f);
                                if (walker.getLife() >= 1) {
                                    walker.setState(Walker.ActionType.DAMAGE);
                                } else {
                                    walker.setState(Walker.ActionType.DEAD);
                                    if (sc.beatWalker(walker.getPoint())) {
                                        lvUp();
                                    }
                                }
                            }
                        }

                        // smashの使用判定
                        for (int si = 0; si < remainSmash; si++) {
                            if (isBounds(ges, SMASH_DST_RECT_ARR[si], TAP_EXPANTION)) {
                                player.setState(Player.ActionType.SMASH);
                                for (Walker walker : walkers) {
                                    walker.setState(Walker.ActionType.SMASHED);
                                    if (sc.beatWalker(walker.getPoint())) {
                                        lvUp();
                                    }
                                }
                                playSoundOnceRandom("onSmash", onSmash, 1.5f);
                                remainSmash--;
                                break;
                            }
                        }

                    } else if (ges.type == GestureEvent.GESTURE_FLING && isBounds(ges, onStepArea)) {
                        // Playerをステップさせた
                        if (ges.velocityX > 0.0f) {
                            player.setState(Player.ActionType.STEP_RIGHT);
                        } else {
                            player.setState(Player.ActionType.STEP_LEFT);
                        }
                        playSoundOnceRandom("onFling", onFling, 1.5f);
                    }
                }

                // ゲームオーバーの判定
                if (player.getDamage() >= player.getInitLife()) {
                    player.setState(Player.ActionType.STANDBY);
                    manager.setAllWalkerState(walkers, Walker.ActionType.STANDBY);
                    changeScene(Scene.GAMEOVER);
                }

                // ループ終了時処理
                playedSounds = null;

                break;
            //-------------------------------------------------------------------------------------------------

            case PAUSE://-----------------------------------------------------------------------------------
                //再開待機画面
                gra.drawRoundRect(40, 40, 640, 1200, 15.0f, Color.argb(230, 255, 204, 204));
                txt.drawText("画面タッチで再開！", 140, 650, 500, Assets.map_style.get("title"));

                for (int i = 0; i < gestureEvents.size(); i++) {
                    GestureEvent ges = gestureEvents.get(i);
                    if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP) {
                        changeScene(Scene.PLAYING);
                    }
                }
                break;
            //-------------------------------------------------------------------------------------------------

            case GAMEOVER://-----------------------------------------------------------------------------------
                //お手入れ終了表示
                if (timer <= 3.0f) {
                    txt.drawText("お手入れ終了", 5, 700, 620, Assets.map_style.get("big"));
                    timer += deltaTime;
                } else {
                    game.setScreen(new ResultScreen(game, sc));
                }
                break;
            //-------------------------------------------------------------------------------------------------

        }
    }


    /**
     * PlayerとWalkerが衝突したかどうか判定する
     */
    protected boolean isCrash(Walker walker) {
        Rect location = walker.getLocation();
        Point hitArea = player.getHitArea();

        return walker.getState() != Walker.ActionType.CRASH
                && walker.getState() != Walker.ActionType.VANISH
                && location.bottom >= 1050 && location.bottom <= 1100
                && location.left >= hitArea.x && location.right <= hitArea.y;
    }


    /**
     * LvUp時の処理
     */
    private void lvUp() {
        player.lvUp();

        if (sc.level % 3 == 0) {
            manager.replaceGenerateTable();
        }
        if (sc.level % 10 == 0) {
            maxWalker++;
        }
        if (sc.level % 25 == 0) {
            playSound(Assets.peoplePerformanceCheer1, 0.8f);
            if (remainSmash < MAX_SMASH) {
                remainSmash++;
            }
        }
    }


    /**
     * 効果音が再生されていない場合、渡された効果音セットから、ランダムで効果音を再生する。
     * 設定でミュートになっている場合は再生しない。
     * 効果音が再生されているかどうかは、再生済み効果音の種類のStringリストex)["onTap", "onFling"]を参照している。
     *
     * @param onWhat   再生する効果音の種類　ex) onTap, onSmash
     * @param soundMap 再生する効果音の、キー：再生基準値（0.0～1.0の間）　値：Soundインスタンス のMAP
     * @param volume   音量
     * @return 再生したかどうか
     */
    public boolean playSoundOnceRandom(String onWhat, LinkedHashMap<Double, Sound> soundMap, float volume) {
        if (playedSounds.indexOf(onWhat) == -1) {
            if (playSoundRandom(soundMap, volume)) {
                playedSounds.add(onWhat);
                return true;
            }
        }

        return false;
    }


    /** このスクリーンでの処理はない */
    @Override
    public void present(float deltaTime) {

    }

    /** 一時停止状態をセットする */
    @Override
    public void pause() {
        if (bgm.isPlaying()) {
            bgm.pause();
        }
        scene = Scene.PAUSE;
    }


    /** このスクリーンでの処理はない */
    @Override
    public void resume() {
    }


    /** 固有の参照を明示的に切る */
    @Override
    public void dispose() {
        Assets.trim_bg = null;
        Assets.onomatopee = null;
        bgm.dispose();
    }


    @Override
    public String toString() {
        return "GameScreen";
    }


    private void changeScene(Scene toScene) {
        this.scene = toScene;
        timer = 0.0f;
    }


    /** 残りlifeゲージを描画する */
    private void drawLife(int initLife, int damage) {
        int left = 50;
        int top = 1150;
        int width = 250;
        int height = 80;

        // lifeゲージ背景
        gra.drawRoundRect(left, top, width, height, 10.0f, Color.LTGRAY);

        // lifeゲージ残り
        left += 5;
        top += 5;
        width -= 10;
        height -= 10;
        int color;
        if (damage < initLife / 2) {
            color = Color.GREEN;
        } else if (damage < initLife * 4 / 5) {
            color = Color.YELLOW;
        } else {
            color = Color.RED;
        }
        gra.drawRoundRect(left, top, width - (int) ((float) width / (float) initLife * (float) damage), height, 9.0f, color);
    }


    /** 残りsmash回数のアイコンを描画する */
    private void drawSmashIcon() {
        Rect srcRect = new Rect(0, 0, 200, 200);
        for (int ii = 0; ii < remainSmash; ii++) {
            gra.drawPixmap(Assets.icon_button, SMASH_DST_RECT_ARR[ii], srcRect);
        }
    }
}
