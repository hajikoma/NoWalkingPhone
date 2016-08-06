package com.hajikoma.nowalkingphone.screen;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.NoWalkingPhoneGame;
import com.hajikoma.nowalkingphone.Player;
import com.hajikoma.nowalkingphone.Score;
import com.hajikoma.nowalkingphone.SmaphoWalker;
import com.hajikoma.nowalkingphone.Walker;
import com.hajikoma.nowalkingphone.WalkerManager;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Input.GestureEvent;
import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.Text;
import com.hajikoma.nowalkingphone.framework.Vibrate;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    private int maxWalker = 10;

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

    /** Walkerタップ時の、当たり判定の拡大値 */
    private final int TAP_EXPANTION = 30;
    /** フリック距離x,yを格納 */
    private int[] velocityX, velocityY;
    /** スワイプ距離x,yを格納 */
    private int[] distanceX, distanceY;

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
        txt = game.getText();
        vib = game.getVibrate();

        // Playerのセットアップ
        Assets.player = gra.newPixmap("chara/chara.png", PixmapFormat.ARGB4444);
        player = new Player(Assets.player, 400, 280);

        // Walkerのセットアップ
        Assets.walker = gra.newPixmap("chara/chara.png", PixmapFormat.ARGB4444);
        manager.addWalker(manager.BASIC, new SmaphoWalker("歩きスマホ", 1, 3, 2, "普通なのがとりえ", 2, Assets.walker, 400, 280, null));
        manager.addWalker(manager.FRIEND, new Walker("おばあさん", 1, 2, 1, "善良な市民。タップ禁止", -5, Assets.walker, 400, 280, null));
        manager.addWalker(manager.SCHOOL, new SmaphoWalker("歩き小学生", 1, 5, 1, "すばしっこくぶつかりやすい", 3, Assets.walker, 400, 280, null));
        manager.addWalker(manager.WOMAN, new SmaphoWalker("歩きウーマン", 1, 3, 2, "たちどまったりふらついたり", 3, Assets.walker, 400, 280, null));
        manager.addWalker(manager.MANIA, new SmaphoWalker("歩きオタク", 2, 2, 3, "とろいがでかくて痛い", 4, Assets.walker, 400, 280, null));
        manager.addWalker(manager.MONSTER, new SmaphoWalker("歩きモンスター", 3, 3, 2, "予測不能な危険生物", 4, Assets.walker, 400, 280, null));
        manager.addWalker(manager.CAR, new Walker("歩きくるま", 999, 10, 5, "もはやテロリスト", 20, Assets.walker, 400, 280, null));

        //固有グラフィックの読み込み
        Assets.trim_bg = gra.newPixmap("others/trim_bg_head.jpg", PixmapFormat.RGB565);

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
        txt.drawText(String.valueOf(sc.combo), 20, 220, 200, Assets.map_style.get("score"));
        txt.drawText(String.valueOf(sc.score), 20, 360, 1000, Assets.map_style.get("score"));
        drawLife(player.getInitLife(), player.getDamage());


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
                    int left = 150 + random.nextInt(AndroidGame.TARGET_WIDTH - 150 - 150);
                    walkers.add(manager.getWalker(new Rect(left, 300, left + 100, 400)));
                }

                // playerの描画
                player.action(deltaTime);
                // bottomの値が小さい（背面にいる）Walkerから描画
                int size = walkers.size();
                HashMap<Integer, Integer> bottomList = new HashMap<>();
                for (int wi = 0; wi < size; wi++){
                    bottomList.put(walkers.get(wi).getLocation().bottom, wi);
                }
                ArrayList<Integer> keyList = new ArrayList<>(bottomList.keySet());
                Collections.sort(keyList);//昇順
                for (int key: keyList ){
                    walkers.get(bottomList.get(key)).action(deltaTime);
                }

                // WalkerとPlayerの衝突処理
                for (Walker walker : walkers) {
                    if (isCrash(walker)) {
                        player.addDamage(walker.getPower());
                        player.setState(Player.ActionType.DAMAGE);
                        walker.setState(Walker.ActionType.CRASH);
                        sc.combo = 0;
                        playSound(Assets.voice_nanto, 0.5f);
                    }
                }

                // タッチイベントの処理
                for (int gi = 0; gi < gestureEvents.size(); gi++) {
                    GestureEvent ges = gestureEvents.get(gi);

                    if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP && !isBounds(ges, onStepArea)) {
                        for (Walker walker : walkers) {
                            if (isBounds(ges, walker.getLocation(), TAP_EXPANTION)) {
                                // Walkerをタップした
                                walker.addDamage(1);
                                if (walker.getLife() >= 1) {
                                    walker.setState(Walker.ActionType.DAMAGE);
                                    playSound(Assets.voice_amai, 1.0f);
                                } else {
                                    walker.setState(Walker.ActionType.DEAD);
                                    if (sc.beatWalker(walker.getPoint())) {
                                        lvUp();
                                    }
                                }
                            }
                        }
                    } else if (ges.type == GestureEvent.GESTURE_FLING && isBounds(ges, onStepArea)) {
                        // Playerをステップさせた
                        if (ges.velocityX > 0.0f) {
                            player.setState(Player.ActionType.STEP_RIGHT);
                        } else {
                            player.setState(Player.ActionType.STEP_LEFT);
                        }
                        playSound(Assets.voice_mieru, 0.5f);
                    }
                }

                //ゲームオーバーの判定
                if (player.getDamage() >= player.getInitLife()) {
                    player.setState(Player.ActionType.STANDBY);
                    for (Walker walker : walkers) {
                        walker.setState(Walker.ActionType.STANDBY);
                    }
                    changeScene(Scene.GAMEOVER);
                }

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
                && location.bottom >= 1100 && location.bottom <= 1150
                && location.left >= hitArea.x && location.right <= hitArea.y;
    }


    /**
     * LvUp時の処理
     */
    private void lvUp() {
        player.lvUp();

        if (sc.level % 2 == 0) {
            manager.replaceGenerateTable();
        }
        if (sc.level % 10 == 0) {
            maxWalker++;
        }

        if (remainSmash < MAX_SMASH) {
            remainSmash++;
        }

        playSound(Assets.voice_soko, 1.0f);
    }


    /** このスクリーンでの処理はない */
    @Override
    public void present(float deltaTime) {

    }

    /** 一時停止状態をセットする */
    @Override
    public void pause() {
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
    }


    @Override
    public String toString() {
        return "GameScreen";
    }


    private void changeScene(Scene toScene) {
        this.scene = toScene;
        timer = 0.0f;
    }


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
}
