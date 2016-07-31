package com.hajikoma.nowalkingphone.screen;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.CharacterHandler;
import com.hajikoma.nowalkingphone.HairRoot;
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
import com.hajikoma.nowalkingphone.item.Item;
import com.hajikoma.nowalkingphone.item.Item.ItemCategory;
import com.hajikoma.nowalkingphone.item.Item.ItemType;
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
    /** キャラクターの描画先 */
    private Rect charaDstArea = new Rect(430, 40, 430 + 280, 40 + 400);
    /** アイテム画像の描画先 */
    private Rect itemGraDstArea = new Rect(40, 1280 - 40 - 180, 40 + 180, 1280 - 40);
    /** アイテム使用ボタンの描画先 */
    private Rect itemUseDstArea = new Rect(40, 1280 - 40 - 180, 720 - 40, 1280 - 40);

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
    /** 現在のキャラクターのアクション */
    private int charaAction = CharacterHandler.ACTION_STAND;

    /** Walker */
    private Walker walker;

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
    /** 毛根 */
    private HairRoot[] hr;
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

    /** アイテムを使い始めてからの経過時間 */
    private float itemTime = 0.0f;
    /** 選択中のアイテム */
    private Item item;
    /** 選択中のアイテムのインデックス */
    private int useItemIndex;
    /** 選択中のアイテムのカテゴリー */
    private ItemCategory iCate;
    /** 選択中のアイテムのタイプ */
    private ItemType iType;
    /** 選択中のアイテムの効果音 */
    private Sound itemUseSound;
    /** 強化型アイテムの効果(int型) */
    private int cPickerExpansion, cPickerTorning, cHRPower;
    /** 強化型アイテムの効果（float型） */
    private float cPickerBonusPar, cEnergy;
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
        player = new Player(gra, Assets.player, 100, 100, itemGraDstArea);

        // Walkerのセットアップ
        Assets.walker = gra.newPixmap("chara/chara.png", PixmapFormat.ARGB4444);
        walker = new Walker(gra, "歩き大学生", 2, 2, 2, "普通なのがとりえ", 1, Assets.walker, 50, 50, charaDstArea);
        walker.setState(Walker.ActionType.WALK);

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


        walker.action(deltaTime);


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


                //タッチイベントの処理
                for (int gi = 0; gi < gestureEvents.size(); gi++) {
                    GestureEvent ges = gestureEvents.get(gi);

                    if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP && isBounds(ges, walker.getLocation())) {
                        walker.addDamage(1);
                        if (walker.getLife() >= 1) {
                            walker.setState(Walker.ActionType.DAMAGE);
                        } else {
                            walker.setState(Walker.ActionType.VANISH);
                        }
                    }
//                        //アイテム使用ボタンが押された場合
//                        itemOnTouch();
//                        itemState = ItemState.USING;
//                        Assets.ud.setItemNumber(useItemIndex, -1);
//                    } else if (ges.type == picker.GESTURE) {
//                        //それ以外の領域でタッチイベントが発生した場合
//                        for (int ri = 0; ri < mis.ROOT_COUNT; ri++) {
//                            //毛を抜く判定。毛が生えており、タッチがその毛の域内で、かつちぎれ・ひっぱられの各モーション中以外の場合にのみ抜ける
//                            if (hr[ri].isGrown() && !hr[ri].obj.isTorn() && isBounds(ges, hr[ri].obj.getX(), hr[ri].obj.getY(), 100, hr[ri].obj.getHeight(), picker.getExpansion() + cPickerExpansion)) {
//                                boolean pickedFlag = true;    //毛が抜けたかどうか
//
//                                if (hr[ri].obj.getWeakness() > picker.getTorning() + cPickerTorning + random.nextInt(20)) {
//                                    //ちぎれた場合
//                                    pickedFlag = false;
//                                    hr[ri].obj.torn();
//                                    sc.tornCount++;
//                                    combo = 0;
//                                    playSound(Assets.pick_up2, 0.5f);
//                                    int tornLength = hr[ri].obj.getHeight() / 2;
//                                    hr[ri].obj.setY(hr[ri].obj.getY() + tornLength);
//                                    hr[ri].obj.setHeight(tornLength);
//                                    hr[ri].obj.setMaxLength(hr[ri].obj.getMaxLength() - tornLength);
//                                    //短くなることで、少しちぎれにくくなる
//                                    hr[ri].obj.setWeakness(hr[ri].obj.getWeakness() - 3);
//                                } else {
//                                    pickedFlag = true;
//                                }
//
//                                //毛が抜けた場合の共通処理
//                                if (pickedFlag) {
//                                    //抜き方別の特殊な処理
//                                    switch (picker.GESTURE) {
//                                        case GestureEvent.GESTURE_FLING:
//                                            velocityX[ri] = (int) ges.velocityX;
//                                            velocityY[ri] = (int) ges.velocityY;
//                                            break;
//                                    }
//
//                                    hr[ri].pickedUp();
//                                    hr[ri].changePower(-1);
//                                    addPoint[ri] = (int) (hr[ri].obj.getPoint() * (picker.getPointPar()
//                                            + cPickerBonusPar) + combo * 0.1f);
//                                    sc.pickCount++;
//                                    sc.pickLength += hr[ri].obj.getHeight();
//                                    pickedBy[ri] = picker.GESTURE;
//                                }
//                            }
//                        }
//                    }
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
                    game.setScreen(new ResultScreen(game, misIndex, useItemIndex, sc));
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
