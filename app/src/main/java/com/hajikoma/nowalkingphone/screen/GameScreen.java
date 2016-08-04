package com.hajikoma.nowalkingphone.screen;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.NoWalkingPhoneGame;
import com.hajikoma.nowalkingphone.Player;
import com.hajikoma.nowalkingphone.Score;
import com.hajikoma.nowalkingphone.Scores;
import com.hajikoma.nowalkingphone.Walker;
import com.hajikoma.nowalkingphone.WalkerManager;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Input.GestureEvent;
import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.Sound;
import com.hajikoma.nowalkingphone.framework.Text;
import com.hajikoma.nowalkingphone.framework.Vibrate;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ゲームのメインスクリーン。
 * TODO:変数のスコープをできるだけ小さくする
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

    /** 共通して使用するゲームクラス */
    private final Game game;
    /** Graphicsインスタンス */
    private final Graphics gra;
    /** Textインスタンス */
    private final Text txt;
    /** Vibrateインスタンス */
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
    private int maxWalker = 3;


    /** 各スコアを格納 */
    private Score sc = new Score();
    /** コンボ数を格納 */
    private int combo = 0;
    /** コンボ数の表示座標 */
    private Point comboXY = new Point();
    /** コンボ数の表示座標 */
    private float comboTime = 0.0f;

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
        manager.addWalker(manager.BASIC, new Walker("歩きスマホ", 2, 2, 2, "普通なのがとりえ", 1, Assets.walker, 50, 50, null));
        manager.addWalker(manager.FRIEND, new Walker("おばあさん", 1, 1, 1, "善良な市民。タップ禁止", -5, Assets.walker, 50, 50, null));
        manager.addWalker(manager.SCHOOL, new Walker("歩き小学生", 1, 3, 1, "すばしっこくぶつかりやすい", 2, Assets.walker, 50, 50, null));
        manager.addWalker(manager.WOMAN, new Walker("歩きウーマン", 2, 2, 2, "たちどまったりふらついたり", 2, Assets.walker, 50, 50, null));
        manager.addWalker(manager.MANIA, new Walker("歩きオタク", 3, 1, 3, "とろいがでかくて痛い", 2, Assets.walker, 50, 50, null));
        manager.addWalker(manager.MONSTER, new Walker("歩きモンスター", 3, 3, 2, "予測不能な危険生物", 3, Assets.walker, 50, 50, null));
        manager.addWalker(manager.CAR, new Walker("歩きくるま", 999, 3, 5, "もはやテロリスト", 10, Assets.walker, 50, 50, null));

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

        player.action(deltaTime);
        for (Walker walker : walkers) {
            walker.action(deltaTime);
        }


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

                // WalkerとPlayerの衝突判定
                for (Walker walker : walkers) {
                    Rect location = walker.getLocation();
                    Point hitArea = player.getHitArea();
                    if (walker.getState() != Walker.ActionType.CRASH
                            && walker.getState() != Walker.ActionType.VANISH
                            && location.bottom >= 1100 && location.bottom <= 1150
                            && location.left >= hitArea.x && location.right <= hitArea.y) {
                        player.addDamage(walker.getPower());
                        player.setState(Player.ActionType.DAMAGE);
                        walker.setState(Walker.ActionType.CRASH);
                        playSound(Assets.click, 0.5f);
                    }
                }

                // タッチイベントの処理
                for (int gi = 0; gi < gestureEvents.size(); gi++) {
                    GestureEvent ges = gestureEvents.get(gi);

                    if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP && !isBounds(ges, onStepArea)) {
                        for (Walker walker : walkers) {
                            if (isBounds(ges, walker.getLocation())) {
                                // Walkerをタップした
                                walker.addDamage(1);
                                if (walker.getLife() >= 1) {
                                    walker.setState(Walker.ActionType.DAMAGE);
                                    playSound(Assets.click, 1.0f);
                                } else {
                                    walker.setState(Walker.ActionType.DEAD);
                                    if (sc.addScore(walker.getPoint())) {
                                        // LvUp時
                                        playSound(Assets.pay_point, 1.0f);
                                        if (sc.level % 2 == 0) {
                                            manager.replaceGenerateTable();
                                        }
                                        if (sc.level % 10 == 0) {
                                            maxWalker++;
                                        }
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
                        playSound(Assets.pick_up1, 0.5f);
                    }
                }

                //ゲームオーバーの判定
                if (player.getDamage() >= 300) {
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
                }
                break;
            //-------------------------------------------------------------------------------------------------

        }
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
}
