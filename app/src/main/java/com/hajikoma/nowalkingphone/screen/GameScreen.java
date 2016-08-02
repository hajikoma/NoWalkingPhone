package com.hajikoma.nowalkingphone.screen;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.CharacterHandler;
import com.hajikoma.nowalkingphone.MayuGame;
import com.hajikoma.nowalkingphone.Mission;
import com.hajikoma.nowalkingphone.Player;
import com.hajikoma.nowalkingphone.Scores;
import com.hajikoma.nowalkingphone.Walker;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Input.GestureEvent;
import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.Sound;
import com.hajikoma.nowalkingphone.framework.Text;
import com.hajikoma.nowalkingphone.framework.Vibrate;
import com.hajikoma.nowalkingphone.item.Picker;

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

    /** 使用する毛根の場所変数 */
    private Rect[] hrArea;

    /** キャラクターの描画に使用する矩形。画像内で描画すべき矩形の座標 */
    private Rect charaSrcArea;


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
    /** チュートリアルのデモ用の毛の描画座標y */
    int tutoMayuY = 825;

    /** 現在のシーン */
    private Scene scene;
    /** 挑戦中のミッション */
    private Mission mis;
    /** 挑戦中のミッションのインデックス */
    private int misIndex;
    /** 使用するまゆ毛MAPから読み込んだキー一覧 */
    private String[] mayuMapKeys;

    /** Player */
    private Player player;
    /** Playerのステップ操作の受付範囲 */
    private Rect onStepArea = new Rect(0, 1000, MayuGame.TARGET_WIDTH, MayuGame.TARGET_HEIGHT);

    /** Walker */
    private Walker walkers[];

    /** 各スコアを格納 */
    private Scores sc = new Scores();
    /** コンボ数を格納 */
    private int combo = 0;
    /** コンボ数の表示座標 */
    private Point comboXY = new Point();
    /** コンボ数の表示座標 */
    private float comboTime = 0.0f;
    /** 毛根の残り成長力合計 */
    private int totalPower;
    /** 毛根、毛の各処理に汎用的に使用するカウンタ */
    private float[] counter;
    /** ポイントの加算、効果音再生を各毛に一度だけ行うためのフラグ */
    private boolean[] isDoneOnceCommand;
    /** 抜けた時に加算されるポイント */
    private int[] addPoint;

    /** 抜き方(抜くジェスチャー) */
    private Picker picker = Assets.default_picker;
    /** 抜かれたジェスチャーを格納 */
    private int[] pickedBy;
    /** フリック距離x,yを格納 */
    private int[] velocityX, velocityY;
    /** スワイプ距離x,yを格納 */
    private int[] distanceX, distanceY;


    /** 選択中のアイテムの効果音 */
    private Sound itemUseSound;
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
        player = new Player(gra, Assets.player, 100, 100);

        // Walkerのセットアップ
        walkers = new Walker[4];
        Assets.walker = gra.newPixmap("chara/chara.png", PixmapFormat.ARGB4444);
        walkers[0] = new Walker(gra, "歩き大学生", 2, 2, 2, "普通なのがとりえ", 1, Assets.walker, 50, 50, new Rect(100, 100, 200, 200));
        walkers[1] = new Walker(gra, "歩き小学生", 1, 3, 1, "すばしっこくぶつかりやすい", 1, Assets.walker, 50, 50, new Rect(300, 300, 400, 400));
        walkers[2] = new Walker(gra, "歩きウーマン", 2, 2, 2, "たちどまったりふらついたり", 1, Assets.walker, 50, 50, new Rect(500, 500, 600, 600));
        walkers[3] = new Walker(gra, "歩きオタク", 3, 1, 3, "とろいがでかくて痛い", 1, Assets.walker, 50, 50, new Rect(700, 700, 800, 800));

        //固有グラフィックの読み込み
        Assets.trim_bg = gra.newPixmap("others/trim_bg_head.jpg", PixmapFormat.RGB565);

        scene = Scene.READY;
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
                scene = Scene.START;
                break;
            //-------------------------------------------------------------------------------------------------

            case START://--------------------------------------------------------------------------------------
                // チュートリアル表示
                scene = Scene.PLAYING;
                break;
            //-------------------------------------------------------------------------------------------------

            case PLAYING://-----------------------------------------------------------------------------------

                timer += deltaTime;

                // Walkerの描画


                if (timer >= 3.0f && timer < 6.0f) {
                    player.setState(Player.ActionType.WALK);
                } else if (timer >= 6.0f && timer < 7.0f) {
                    player.setState(Player.ActionType.DAMAGE);
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

                // WalkerとPlayerの状態に応じた処理
                for (Walker walker : walkers) {
                    if (walker.getState() == Walker.ActionType.VANISH) {
                        System.out.print("消える");
                        walker = null;
                    }
                }


                // タッチイベントの処理
                for (int gi = 0; gi < gestureEvents.size(); gi++) {
                    GestureEvent ges = gestureEvents.get(gi);

                    if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP && !isBounds(ges, onStepArea)) {
                        for (Walker walker : walkers) {
                            if (isBounds(ges, walker.getLocation())) {
                                walker.addDamage(1);
                                if (walker.getLife() >= 1) {
                                    walker.setState(Walker.ActionType.DAMAGE);
                                } else {
                                    walker.setState(Walker.ActionType.DEAD);
                                }
                            }
                        }
                    } else if (ges.type == GestureEvent.GESTURE_FLING && isBounds(ges, onStepArea)) {
                        if (ges.velocityX > 0.0f) {
                            player.setState(Player.ActionType.STEP_RIGHT);
                        } else {
                            player.setState(Player.ActionType.STEP_LEFT);
                        }
                        playSound(Assets.pick_up1, 0.5f);
                    }
                }


                //ゲームオーバーの判定
//                if (totalPower == 0) {
//                    scene = Scene.GAMEOVER;
//                }

                break;
            //-------------------------------------------------------------------------------------------------

            case PAUSE://-----------------------------------------------------------------------------------
                //再開待機画面
                gra.drawRoundRect(40, 40, 640, 1200, 15.0f, Color.argb(230, 255, 204, 204));
                txt.drawText("画面タッチで再開！", 140, 650, 500, Assets.map_style.get("title"));

                for (int i = 0; i < gestureEvents.size(); i++) {
                    GestureEvent ges = gestureEvents.get(i);
                    if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP) {
                        scene = Scene.PLAYING;
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
//                    game.setScreen(new ResultScreen(game, misIndex, useItemIndex, sc));
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

}
