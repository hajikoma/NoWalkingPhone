package com.hajikoma.nowalkingphone.screen;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.hajikoma.nowalkingphone.AlphaGenerator;
import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.CutInEffect;
import com.hajikoma.nowalkingphone.SpriteEffect;
import com.hajikoma.nowalkingphone.NoWalkingPhoneGame;
import com.hajikoma.nowalkingphone.Player;
import com.hajikoma.nowalkingphone.Score;
import com.hajikoma.nowalkingphone.SmashEffect;
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
import com.hajikoma.nowalkingphone.framework.Sound;
import com.hajikoma.nowalkingphone.framework.Text;
import com.hajikoma.nowalkingphone.framework.Vibrate;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * ゲームのメインスクリーン。
 */
public class GameScreen extends Screen {

    /** 現在のシーンを表す列挙型 */
    private enum Scene {
        READY,//開始準備、セットアップに使用
        CONTINUE_READY,// コンティニュー後の開始準備、セットアップに使用
        START,// 開始直後、カットインに使用
        PLAYING,// ゲーム中
        PAUSE,// 一時停止
        GAME_OVER// ゲームオーバー
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

    /** 現在のシーン */
    private Scene scene;

    /** Player */
    private Player player;
    /** Playerのステップ操作の受付範囲 */
    private Rect onStepArea = new Rect(0, 800, NoWalkingPhoneGame.TARGET_WIDTH, NoWalkingPhoneGame.TARGET_HEIGHT);

    /** Walker */
    private ArrayList<Walker> walkers = new ArrayList<>();
    /** 次に出現させるWalkerの種類定数。-1の場合、ランダム出現とする */
    private int nextWalkerType = WalkerManager.MAN;

    /** スマッシュの使用可能回数 */
    private int remainSmash = 3;
    /** スマッシュの最大使用可能回数 */
    private static final int MAX_SMASH = 3;
    /** スマッシュアイコンの描画先 */
    private static final Rect SMASH_DST_RECT_ARR[] = {
            new Rect(350, 60, 415, 125),
            new Rect(425, 60, 490, 125),
            new Rect(500, 60, 565, 125)
    };

    /** 一時停止ボタンのタップ判定エリア */
    private static final Rect PAUSE_DST_AREA = new Rect(580, 0, 720, 140);

    /** Walkerタップ時の、当たり判定の拡大値 */
    private static final int TAP_EXPANSION = 30;

    /** Font */
    private Paint fontNumber;
    private Paint fontScore;

    /** Effect */
    private SmashEffect smashEffect;
    private SpriteEffect crashSpriteEffect;

    // CutIn
    private SpriteEffect cutInBackBlue;
    private SpriteEffect cutInBackYellow;
    private SpriteEffect cutInBackRed;
    private CutInEffect startCutIn;
    private CutInEffect smashPlusCutIn;
    private CutInEffect dangerCutIn;

    /** CutInの表示キュー（カットインは同時には表示しない） */
    private LinkedList<CutInEffect> cutInQueue = new LinkedList<>();

    /** BGM */
    private Music bgm;

    /** 効果音をそのループで再生したかどうか */
    private ArrayList<String> playedSounds;

    /** 効果音マップ */
    private LinkedHashMap<Double, Sound> onTap = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onFling = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onCrash = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onSmashVoice = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onSmash = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onNoSmash = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onLvUpMany = new LinkedHashMap<>();

    /** エフェクトに使用する変化するalpha値 */
    private AlphaGenerator agLifeWarning = new AlphaGenerator(100, 200, 3);
    private AlphaGenerator agLifeDanger = new AlphaGenerator(100, 200, 15);

    /** 固有画像 */
    private Pixmap bg;
    private Pixmap iconSmash;


    /**
     * GameScreenを生成する
     */
    public GameScreen(Game game, boolean isContinue) {
        super(game);
        this.game = game;
        gra = game.getGraphics();
        aud = game.getAudio();
        txt = game.getText();
        vib = game.getVibrate();

        // Playerのセットアップ
        player = new Player(gra.newPixmap("player/player.png", PixmapFormat.ARGB4444), 200);

        // 固有グラフィックの読み込み
        bg = gra.newPixmap("bg/bg_game.jpg", PixmapFormat.RGB565);
        iconSmash = gra.newPixmap("others/icon_smash2.png", PixmapFormat.ARGB4444);

        // Font
        fontNumber = new Paint();
        fontNumber.setAntiAlias(true);
        fontNumber.setTextSize(NoWalkingPhoneGame.FONT_SIZE_M);
        fontScore = new Paint();
        fontScore.setAntiAlias(true);
        fontScore.setTextSize(NoWalkingPhoneGame.FONT_SIZE_L);

        // Effect
        smashEffect = new SmashEffect(gra.newPixmap("others/smash1.png", PixmapFormat.ARGB4444), gra.newPixmap("others/smash2.png", PixmapFormat.ARGB4444));
        crashSpriteEffect = new SpriteEffect(gra.newPixmap("others/crash.png", PixmapFormat.ARGB4444), 620, new float[]{0.5f});

        // CutIn
        cutInBackBlue = new SpriteEffect(gra.newPixmap("cutin/cutin_back_blue.jpg", PixmapFormat.RGB565), 360, new float[]{0.1f, 0.1f, 0.1f});
        cutInBackYellow = new SpriteEffect(gra.newPixmap("cutin/cutin_back_yellow.jpg", PixmapFormat.RGB565), 360, new float[]{0.1f, 0.1f, 0.1f});
        cutInBackRed = new SpriteEffect(gra.newPixmap("cutin/cutin_back_red.jpg", PixmapFormat.RGB565), 360, new float[]{0.1f, 0.1f, 0.1f});
        startCutIn = new CutInEffect(gra.newPixmap("cutin/start.png", PixmapFormat.ARGB4444), cutInBackBlue);
        smashPlusCutIn = new CutInEffect(gra.newPixmap("cutin/smash_plus.png", PixmapFormat.ARGB4444), cutInBackBlue);
        dangerCutIn = new CutInEffect(gra.newPixmap("cutin/life_danger.png", PixmapFormat.ARGB4444), cutInBackRed);

        // BGM
        bgm = aud.newMusic("music/me_6777276_Race.mp3");
        bgm.setVolume(0.05f);
        bgm.setLooping(true);

        // 効果音セット
        onTap.put(0.2, Assets.voice_kurae);
        onTap.put(0.4, Assets.voice_soko);
        onTap.put(0.6, Assets.voice_amai);
        onTap.put(1.0, Assets.punchMiddle2);

        onFling.put(0.25, Assets.voice_mieru);
        onFling.put(0.5, Assets.voice_abunai);
        onFling.put(1.0, Assets.setup1);

        onCrash.put(0.25, Assets.voice_nanto);
        onCrash.put(0.5, Assets.voice_itete);
        onCrash.put(1.0, Assets.kickLow1);

        onSmashVoice.put(0.5, Assets.voice_douda);
        onSmashVoice.put(1.0, Assets.voice_futtonjae);

        onSmash.put(0.5, Assets.magicElectron2);
        onSmash.put(1.0, Assets.bomb1);

        onNoSmash.put(1.0, Assets.incorrect1);

        onLvUpMany.put(0.5, Assets.peoplePerformanceCheer1);
        onLvUpMany.put(1.0, Assets.peopleStadiumCheer1);

        if (!isContinue) {
            // 前回のゲームデータを削除、初期化
            Assets.score = new Score();
            Assets.manager = new WalkerManager(2);
            changeScene(Scene.READY);
        } else {
            changeScene(Scene.CONTINUE_READY);
        }
    }


    /** メインループ内で呼ばれる。ループ内のためインスタンスの生成には慎重を期すこと。 */
    @Override
    public void update(float deltaTime) {
        // gestureが稀に正しく格納されないことへの対策
        List<GestureEvent> gestureEvents = null;
        try {
            gestureEvents = game.getInput().getGestureEvents();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        game.getInput().getKeyEvents();

        //共通部分の描画
        gra.drawPixmap(bg, 0, 0);
        txt.drawText(String.valueOf(Assets.score.combo), 20, 270, 200, fontNumber);
        drawGraphicalNumber(Assets.score.score, 80, 220, 200, 7);
        drawLife(player.getInitLife(), player.getDamage());
        drawSmashIcon();

        // シーンごとの処理
        switch (scene) {
            case READY://-----------------------------------------------------------------------------------
                startCutIn.on();
                changeScene(Scene.START);
                player.action(deltaTime);

                break;
            //-------------------------------------------------------------------------------------------------

            case CONTINUE_READY://-----------------------------------------------------------------------------------
                // 動画が終わるまで待つ
                if (((AndroidGame)game).isRewarded) {
                    startCutIn.on();
                    changeScene(Scene.START);
                }

                player.action(deltaTime);

                break;
            //-------------------------------------------------------------------------------------------------

            case START://--------------------------------------------------------------------------------------
                // スタートカットイン表示
                if (!startCutIn.play(deltaTime)) {
                    startCutIn = null;
                    changeScene(Scene.PLAYING);
                }
                player.action(deltaTime);

                break;
            //-------------------------------------------------------------------------------------------------

            case PLAYING://-----------------------------------------------------------------------------------

                timer += deltaTime;

                // 再生済み効果音を初期化
                playedSounds = new ArrayList<>();

                // エフェクト描画
                if (cutInQueue.size() >= 1) {
                    // 先頭のカットインを表示
                    if (!cutInQueue.get(0).play(deltaTime)) {
                        // カットインが終了していたら削除
                        cutInQueue.remove(0);
                    }
                }

                // Playerの状態に応じた処理
                if (player.getState() == Player.ActionType.STEP_RIGHT) {
                    // 右ステップでsmash使用
                    if (remainSmash >= 1) {
                        smashEffect.on();
                        player.setState(Player.ActionType.SMASH);
                        for (Walker walker : walkers) {
                            walker.setState(Walker.ActionType.SMASHED);
                            if (Assets.score.beatWalker(walker.getPoint())) {
                                lvUp();
                            }
                        }
                        playSoundOnceRandom("onSmashVoice", onSmashVoice, 1.0f);
                        playSoundOnceRandom("onSmash", onSmash, 1.0f);
                        remainSmash--;
                    } else {
                        player.setState(Player.ActionType.WALK);
                        playSoundOnceRandom("onNoSmash", onNoSmash, 1.0f);
                    }
                }

                // Walkerの状態に応じた処理
                for (int i = 0; i < walkers.size(); i++) {
                    if (walkers.get(i).getState() == Walker.ActionType.VANISH) {
                        walkers.remove(i);
                    }
                }

                // Walkerを出現させる
                if (walkers.size() < Assets.manager.maxWalker) {
                    int left = 50 + random.nextInt(AndroidGame.TARGET_WIDTH - 100 - 50);
                    if (nextWalkerType == WalkerManager.RANDOM) {
                        // ランダム出現
                        walkers.add(Assets.manager.getSomeWalker(new Rect(left, 370, left + 100, 470)));
                    } else {
                        // 固定出現
                        walkers.add(Assets.manager.getTheWalker(nextWalkerType, new Rect(left, 370, left + 100, 470)));
                        nextWalkerType = WalkerManager.RANDOM;
                    }
                }

                // 描画処理
                actWalkerBehindToForward(deltaTime);
                player.action(deltaTime);
                crashSpriteEffect.play(deltaTime);
                smashEffect.play(deltaTime);

                // WalkerとPlayerの衝突処理
                for (Walker walker : walkers) {
                    if (isCrash(walker)) {
                        if (player.getState() == Player.ActionType.STEP_LEFT) {
                            // 左ステップ中は無敵
                            processBarrier(walker);
                        } else {
                            processCrash(walker);
                        }
                    }
                }

                // タッチイベントの処理
                if (gestureEvents != null) {
                    for (int gi = 0; gi < gestureEvents.size(); gi++) {
                        GestureEvent ges = gestureEvents.get(gi);

                        // 稀にgesが正しく格納されないことがあることへの対処
                        if (ges == null) {
                            continue;
                        }

                        if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP) {
                            if (!isBounds(ges, PAUSE_DST_AREA)) {
                                // Walkerのタップ判定
                                for (Walker walker : walkers) {
                                    if (isBounds(ges, walker.getLocation(), TAP_EXPANSION)) {
                                        walker.addDamage(1);
                                        playSoundOnceRandom("onTap", onTap, 1.0f);
                                        if (walker.getLife() >= 1) {
                                            walker.setState(Walker.ActionType.DAMAGE);
                                        } else {
                                            walker.setState(Walker.ActionType.DEAD);
                                            if (Assets.score.beatWalker(walker.getPoint())) {
                                                lvUp();
                                            }
                                        }
                                    }
                                }
                            } else {
                                // 一時停止が押された
                                changeScene(Scene.PAUSE);
                            }

                        } else if (ges.type == GestureEvent.GESTURE_FLING && isBounds(ges, onStepArea)) {
                            // Playerをステップさせた
                            if (ges.velocityX > 0.0f) {
                                player.setState(Player.ActionType.STEP_RIGHT);
                            } else {
                                player.setState(Player.ActionType.STEP_LEFT);
                                playSoundOnceRandom("onFling", onFling, 1.0f);
                            }
                        }
                    }
                }

                // ゲームオーバーの判定
                if (player.getDamage() >= player.getInitLife()) {
                    player.setState(Player.ActionType.STANDBY);
                    Assets.manager.setAllWalkerState(walkers, Walker.ActionType.STANDBY);
                    changeScene(Scene.GAME_OVER);
                }

                // ループ終了時処理
                playedSounds = null;

                break;
            //-------------------------------------------------------------------------------------------------

            case PAUSE://-----------------------------------------------------------------------------------
                // 停止中
                gra.drawRect(0, 0, NoWalkingPhoneGame.TARGET_WIDTH, NoWalkingPhoneGame.TARGET_HEIGHT, Color.argb(100, 255, 255, 255));
                txt.drawText("一時休戦中…", 250, 600, 620, Assets.style_general_black);

                if (gestureEvents != null) {
                    for (int gi = 0; gi < gestureEvents.size(); gi++) {
                        GestureEvent ges = gestureEvents.get(gi);
                        if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP) {
                            changeScene(Scene.PLAYING);
                        }
                    }
                }

                break;
            //-------------------------------------------------------------------------------------------------

            case GAME_OVER://-----------------------------------------------------------------------------------
                //お手入れ終了表示
                if (timer <= 3.0f) {
                    txt.drawText("もう歩けない…", 5, 700, 620, Assets.style_general_black);
                    timer += deltaTime;
                } else {
                    game.setScreen(new ResultScreen(game));
                }
                break;
            //-------------------------------------------------------------------------------------------------

        }
    }


    @Override
    public void present(float deltaTime) {
        playMusic(bgm);
    }


    /** 一時停止状態をセットする */
    @Override
    public void pause() {
        if (bgm.isPlaying()) {
            bgm.pause();
        }
        scene = Scene.PAUSE;
    }


    /** 再開時の処理 */
    @Override
    public void resume() {
        if (!bgm.isPlaying()) {
            playMusic(bgm);
        }
        if (scene != Scene.READY) {
            scene = Scene.PLAYING;
        }
    }


    /** 固有の参照を明示的に切る */
    @Override
    public void dispose() {
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


    /** bottomの値が小さい（背面にいる）Walkerから描画する */
    private void actWalkerBehindToForward(float deltaTime) {
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
    }


    /** ステップ中に衝突した時の一連の処理 */
    private void processBarrier(Walker walker) {
        walker.setState(Walker.ActionType.VANISH);
    }


    /** 衝突時の一連の処理 */
    private void processCrash(Walker walker) {
        int power = walker.getPower();

        player.addDamage(power);

        if (player.getRemainLife() <= player.getInitLife() / 5) {
            dangerCutIn.on();
            cutInQueue.add(dangerCutIn);
        }

        player.setState(Player.ActionType.DAMAGE);
        walker.setState(Walker.ActionType.CRASH);
        Assets.score.combo = 0;
        crashSpriteEffect.on(new Rect(0, 0, 720, 1280));
        playSoundOnceRandom("onCrash", onCrash, 1.0f);
        if (power >= 5) {
            doVibrate(vib, Assets.vibLongOnce);
        } else {
            doVibrate(vib, Assets.vibShortOnce);
        }
    }


    /**
     * PlayerとWalkerが衝突したかどうか判定する
     */
    protected boolean isCrash(Walker walker) {
        Walker.ActionType walkerState = walker.getState();
        Rect location = walker.getLocation();

        return walkerState != Walker.ActionType.CRASH
                && walkerState != Walker.ActionType.VANISH
                && player.getState() != Player.ActionType.STEP_LEFT
                && location.bottom >= 1050 && location.bottom <= 1100;
    }


    /**
     * LvUp時の処理
     */
    private void lvUp() {
        player.lvUp();

        if (Assets.score.level % 3 == 0) {
            Assets.manager.replaceGenerateTable();
        }
        if (Assets.score.level % 10 == 0) {
            Assets.manager.maxWalker++;
        }
        if (Assets.score.level % 25 == 0) {
            playSoundRandom(onLvUpMany, 0.8f);
            if (remainSmash < MAX_SMASH) {
                remainSmash++;
                smashPlusCutIn.on();
                cutInQueue.add(smashPlusCutIn);
            }
        }


        // 特定のWalkerの初出現
        CutInEffect cutIn = null;
        boolean appearedFlags[] = Assets.manager.appearedFlags;
        switch (Assets.score.level) {
            case 3:
                nextWalkerType = WalkerManager.SCHOOL;
                appearedFlags[WalkerManager.SCHOOL] = true;
                cutIn = new CutInEffect(gra.newPixmap("cutin/appear_school.png", PixmapFormat.ARGB4444), cutInBackYellow);
                break;
            case 10:
                nextWalkerType = WalkerManager.GRANDMA;
                appearedFlags[WalkerManager.GRANDMA] = true;
                cutIn = new CutInEffect(gra.newPixmap("cutin/appear_grandma.png", PixmapFormat.ARGB4444), cutInBackYellow);
                break;
            case 30:
                nextWalkerType = WalkerManager.GIRL;
                appearedFlags[WalkerManager.GIRL] = true;
                cutIn = new CutInEffect(gra.newPixmap("cutin/appear_girl.png", PixmapFormat.ARGB4444), cutInBackYellow);
                break;
            case 60:
                nextWalkerType = WalkerManager.MANIA;
                appearedFlags[WalkerManager.MANIA] = true;
                cutIn = new CutInEffect(gra.newPixmap("cutin/appear_mania.png", PixmapFormat.ARGB4444), cutInBackYellow);
                break;
            case 90:
                nextWalkerType = WalkerManager.VISITOR;
                appearedFlags[WalkerManager.VISITOR] = true;
                cutIn = new CutInEffect(gra.newPixmap("cutin/appear_visitor.png", PixmapFormat.ARGB4444), cutInBackYellow);
                break;
            case 120:
                nextWalkerType = WalkerManager.CAR;
                appearedFlags[WalkerManager.CAR] = true;
                cutIn = new CutInEffect(gra.newPixmap("cutin/appear_car.png", PixmapFormat.ARGB4444), cutInBackYellow);
                break;
        }

        if (cutIn != null) {
            cutIn.on();
            cutInQueue.add(cutIn);
        }
    }


    /** 残りlifeゲージを描画する */
    private void drawLife(int initLife, int damage) {
        int left = 10;
        int top = 50;
        int width = 10 + initLife * (10 + 5) + 5; // 左余白10＋ライフゲージバー10+バー間の余白5+右余白10(5+5)
        int height = 80;
        int warningZone = initLife / 2;
        int dangerZone = initLife / 5;
        int remainLife = initLife - damage;

        // lifeゲージ背景
        int color;
        if (remainLife <= 0) {
            gra.drawRoundRect(left, top, width, height, 10.0f, Color.rgb(255, 0, 0));
            color = Color.RED;
        } else if (remainLife <= dangerZone) {
            gra.drawRoundRect(left, top, width, height, 10.0f, Color.argb(agLifeDanger.getAlpha(), 255, 0, 0));
            color = Color.RED;
        } else if (remainLife <= warningZone) {
            gra.drawRoundRect(left, top, width, height, 10.0f, Color.argb(agLifeWarning.getAlpha(), 255, 0, 0));
            color = Color.YELLOW;
        } else {
            gra.drawRoundRect(left, top, width, height, 10.0f, Color.rgb(156, 167, 226)); // 薄い青
            color = Color.GREEN;
        }

        // lifeゲージバー
        left += 10;
        top += 10;
        width = 10;
        height -= 20;
        for (int li = 1; li <= remainLife; li++) {
            gra.drawRoundRect(left, top, width, height, 5.0f, color);
            left += 10 + 5; // バーが10、余白5
        }
    }


    /** 残りsmash回数分、アイコンを描画する */
    private void drawSmashIcon() {
        Rect srcRect = new Rect(0, 0, 143, 143);
        for (int ii = 0; ii < remainSmash; ii++) {
            gra.drawPixmap(iconSmash, SMASH_DST_RECT_ARR[ii], srcRect);
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
}
