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
        /** お手入れ開始準備、セットアップに使用 */
        READY,
        /** お手入れ開始直後、カウントダウンに使用 */
        START,
        /** ゲーム中 */
        PLAYING,
        /** 一時停止 */
        PAUSE,
        /** ゲームオーバー */
        GAME_OVER
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
    private Rect onStepArea = new Rect(0, 1000, NoWalkingPhoneGame.TARGET_WIDTH, NoWalkingPhoneGame.TARGET_HEIGHT);

    /** Walker */
    private ArrayList<Walker> walkers = new ArrayList<>();
    /** WalkerManager */
    private WalkerManager manager = new WalkerManager();
    /** Walkerの同時出現上限数 */
    private int maxWalker = 2;
    /** 次に出現させるWalkerの種類定数。-1の場合、ランダム出現とする */
    private int nextWalkerType = WalkerManager.MAN;

    /** 各スコアを格納 */
    private Score sc = new Score();

    /** スマッシュの使用可能回数 */
    private int remainSmash = 3;
    /** スマッシュの最大使用可能回数 */
    private static final int MAX_SMASH = 3;
    /** スマッシュアイコンの描画先 */
    private final Rect SMASH_DST_RECT_ARR[] = {
            new Rect(450, 1175, 520, 1245),
            new Rect(540, 1175, 610, 1245),
            new Rect(630, 1175, 700, 1245)
    };

    /** Walkerタップ時の、当たり判定の拡大値 */
    private static final int TAP_EXPANSION = 30;

    /** Font */
    private Paint fontNumber;
    private Paint fontScore;

    /** Effect */
    private SmashEffect smashEffect;
    private SpriteEffect crashSpriteEffect;

    // CutIn
    private SpriteEffect blueCutInBack;
    private CutInEffect startCutIn;
    private CutInEffect smashPlusCutIn;
    private CutInEffect dangerCutIn;

    /** CutInの表示キュー */
    private LinkedList<CutInEffect> cutInQueue = new LinkedList<>();

    /** BGM */
    private Music bgm;

    /** 効果音をそのループで再生したかどうか */
    private ArrayList<String> playedSounds;

    /** 効果音マップ */
    private LinkedHashMap<Double, Sound> onTap = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onFling = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onCrash = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onBarrier = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onSmash = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onNoSmash = new LinkedHashMap<>();
    private LinkedHashMap<Double, Sound> onLvUpMany = new LinkedHashMap<>();

    /** エフェクトに使用する変化するalpha値 */
    private AlphaGenerator agLifeWarning = new AlphaGenerator(100, 200, 3);
    private AlphaGenerator agLifeDanger = new AlphaGenerator(100, 200, 15);

    /** 固有画像 */
    private Pixmap bg;
    private Pixmap icon_smash;


    /**
     * GameScreenを生成する
     */
    public GameScreen(Game game) {
        super(game);
        this.game = game;
        gra = game.getGraphics();
        aud = game.getAudio();
        txt = game.getText();
        vib = game.getVibrate();

        // Playerのセットアップ
        Assets.player = gra.newPixmap("player/player.png", PixmapFormat.ARGB4444);
        player = new Player(Assets.player, 200);

        // 固有グラフィックの読み込み
        bg = gra.newPixmap("others/bg_game.jpg", PixmapFormat.RGB565);
        icon_smash = gra.newPixmap("others/icon_smash2.png", PixmapFormat.ARGB4444);

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
        blueCutInBack = new SpriteEffect(gra.newPixmap("others/pipo-btleffect-blue.jpg", PixmapFormat.ARGB4444), 320, new float[]{0.1f, 0.1f, 0.1f});
        startCutIn = new CutInEffect(gra.newPixmap("cutin/start.png", PixmapFormat.ARGB4444), blueCutInBack);
        smashPlusCutIn = new CutInEffect(gra.newPixmap("cutin/smash_plus.png", PixmapFormat.ARGB4444), blueCutInBack);
        dangerCutIn = new CutInEffect(gra.newPixmap("cutin/life_danger.png", PixmapFormat.ARGB4444), blueCutInBack);

        // BGM
        bgm = aud.newMusic("music/me_6777276_Race.mp3");
        bgm.setVolume(0.05f);
        bgm.setLooping(true);

        // 効果音セット
        onTap.put(0.5, Assets.voice_soko);
        onTap.put(1.0, Assets.punchMiddle2);
        onFling.put(0.5, Assets.voice_mieru);
        onFling.put(1.0, Assets.punchSwing1);
        onCrash.put(0.5, Assets.voice_nanto);
        onCrash.put(1.0, Assets.punchMiddle2);
        onBarrier.put(1.0, Assets.laser3);
        onSmash.put(0.5, Assets.magicElectron2);
        onSmash.put(1.0, Assets.bomb1);
        onNoSmash.put(1.0, Assets.weak);
        onLvUpMany.put(0.5, Assets.peoplePerformanceCheer1);
        onLvUpMany.put(1.0, Assets.peopleStadiumCheer1);

        changeScene(Scene.READY);
    }


    /**
     * コンティニュー時のGameScreenを生成する
     *
     * @param score 前回ゲームのスコア
     */
    public GameScreen(Game game, Score score) {
        this(game);
        sc = score;
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
        txt.drawText(String.valueOf(sc.level), 20, 130, 200, fontNumber);
        txt.drawText(String.valueOf(sc.combo), 280, 130, 200, fontNumber);
        drawGraphicalNumber(sc.score, 90, 20, 220, 9);
        drawLife(player.getInitLife(), player.getDamage());
        drawSmashIcon();

        // シーンごとの処理
        switch (scene) {
            case READY://-----------------------------------------------------------------------------------
                // 変数の初期化など
                startCutIn.on();
                changeScene(Scene.START);
                break;
            //-------------------------------------------------------------------------------------------------

            case START://--------------------------------------------------------------------------------------
                // スタートカットイン表示
                if (!startCutIn.play(deltaTime)) {
                    startCutIn = null;
                    changeScene(Scene.PLAYING);
                }
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
                            if (sc.beatWalker(walker.getPoint())) {
                                lvUp();
                            }
                        }
                        playSoundOnceRandom("onSmash", onSmash, 1.2f);
                        remainSmash--;
                    } else {
                        player.setState(Player.ActionType.WALK);
                        playSoundOnceRandom("onNoSmash", onNoSmash, 1.2f);
                    }
                }

                // Walkerの状態に応じた処理
                for (int i = 0; i < walkers.size(); i++) {
                    if (walkers.get(i).getState() == Walker.ActionType.VANISH) {
                        walkers.remove(i);
                    }
                }

                // Walkerを出現させる
                if (walkers.size() < maxWalker) {
                    int left = 50 + random.nextInt(AndroidGame.TARGET_WIDTH - 100 - 50);
                    if (nextWalkerType == WalkerManager.RANDOM) {
                        // ランダム出現
                        walkers.add(manager.getSomeWalker(new Rect(left, 370, left + 100, 470)));
                    } else {
                        // 固定出現
                        walkers.add(manager.getTheWalker(nextWalkerType, new Rect(left, 370, left + 100, 470)));
                        nextWalkerType = WalkerManager.RANDOM;
                    }
                }

                // 描画処理
                player.action(deltaTime);
                actWalkerBehindToForward(deltaTime);
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
                            // Walkerのタップ判定
                            for (Walker walker : walkers) {
                                if (isBounds(ges, walker.getLocation(), TAP_EXPANSION)) {
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

                        } else if (ges.type == GestureEvent.GESTURE_FLING && isBounds(ges, onStepArea)) {
                            // Playerをステップさせた
                            if (ges.velocityX > 0.0f) {
                                player.setState(Player.ActionType.STEP_RIGHT);
                            } else {
                                player.setState(Player.ActionType.STEP_LEFT);
                                playSoundOnceRandom("onFling", onFling, 1.5f);
                            }
                        }
                    }
                }

                // ゲームオーバーの判定
                if (player.getDamage() >= player.getInitLife()) {
                    player.setState(Player.ActionType.STANDBY);
                    manager.setAllWalkerState(walkers, Walker.ActionType.STANDBY);
                    changeScene(Scene.GAME_OVER);
                }

                // ループ終了時処理
                playedSounds = null;

                break;
            //-------------------------------------------------------------------------------------------------

            case PAUSE://-----------------------------------------------------------------------------------
                //再開待機画面
                break;
            //-------------------------------------------------------------------------------------------------

            case GAME_OVER://-----------------------------------------------------------------------------------
                //お手入れ終了表示
                if (timer <= 3.0f) {
                    txt.drawText("もう歩けない…", 5, 700, 620, Assets.style_general_black);
                    timer += deltaTime;
                } else {
                    game.setScreen(new ResultScreen(game, sc));
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
        playSoundOnceRandom("onBarrier", onBarrier, 1.5f);
    }


    /** 衝突時の一連の処理 */
    private void processCrash(Walker walker) {
        int power = walker.getPower();

        player.addDamage(power);

        if (player.getDamage() >= player.getInitLife() * 4 / 5) {
            dangerCutIn.on();
            cutInQueue.add(dangerCutIn);
        }

        player.setState(Player.ActionType.DAMAGE);
        walker.setState(Walker.ActionType.CRASH);
        sc.combo = 0;
        crashSpriteEffect.on(new Rect(0, 0, 720, 1280));
        playSoundOnceRandom("onCrash", onCrash, 1.5f);
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
                && location.bottom >= 1010 && location.bottom <= 1060;
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
            playSoundRandom(onLvUpMany, 0.7f);
            if (remainSmash < MAX_SMASH) {
                remainSmash++;
                smashPlusCutIn.on();
                cutInQueue.add(smashPlusCutIn);
            }
        }


        // 特定のWalkerの初出現
        CutInEffect cutIn = null;
        switch (sc.level) {
            case 3:
                nextWalkerType = WalkerManager.SCHOOL;
                manager.appearedFlags[WalkerManager.SCHOOL] = true;
                cutIn = new CutInEffect(gra.newPixmap("cutin/appear_school.png", PixmapFormat.ARGB4444), blueCutInBack);
                break;
            case 10:
                nextWalkerType = WalkerManager.GRANDMA;
                manager.appearedFlags[WalkerManager.GRANDMA] = true;
                cutIn = new CutInEffect(gra.newPixmap("cutin/appear_grandma.png", PixmapFormat.ARGB4444), blueCutInBack);
                break;
            case 30:
                nextWalkerType = WalkerManager.GIRL;
                manager.appearedFlags[WalkerManager.GIRL] = true;
                cutIn = new CutInEffect(gra.newPixmap("cutin/appear_girl.png", PixmapFormat.ARGB4444), blueCutInBack);
                break;
            case 60:
                nextWalkerType = WalkerManager.MANIA;
                manager.appearedFlags[WalkerManager.MANIA] = true;
                cutIn = new CutInEffect(gra.newPixmap("cutin/appear_mania.png", PixmapFormat.ARGB4444), blueCutInBack);
                break;
            case 90:
                nextWalkerType = WalkerManager.VISITOR;
                manager.appearedFlags[WalkerManager.VISITOR] = true;
                cutIn = new CutInEffect(gra.newPixmap("cutin/appear_visitor.png", PixmapFormat.ARGB4444), blueCutInBack);
                break;
            case 120:
                nextWalkerType = WalkerManager.CAR;
                manager.appearedFlags[WalkerManager.CAR] = true;
                cutIn = new CutInEffect(gra.newPixmap("cutin/appear_car.png", PixmapFormat.ARGB4444), blueCutInBack);
                break;
        }

        if (cutIn != null) {
            cutIn.on();
            cutInQueue.add(cutIn);
        }
    }


    /** 残りlifeゲージを描画する */
    private void drawLife(int initLife, int damage) {
        int left = 20;
        int top = 1185;
        int width = 250;
        int height = 80;
        int warningZone = initLife / 2;
        int dangerZone = initLife * 4 / 5;

        // lifeゲージ背景
        if (damage < warningZone) {
            gra.drawRoundRect(left, top, width, height, 10.0f, Color.LTGRAY);
        } else if (damage < dangerZone) {
            gra.drawRoundRect(left, top, width, height, 10.0f, Color.argb(agLifeWarning.getAlpha(), 255, 0, 0));
        } else {
            gra.drawRoundRect(left, top, width, height, 10.0f, Color.argb(agLifeDanger.getAlpha(), 255, 0, 0));
        }

        // lifeゲージ残り
        left += 5;
        top += 5;
        width -= 10;
        height -= 10;
        int color;
        if (damage < warningZone) {
            color = Color.GREEN;
        } else if (damage < dangerZone) {
            color = Color.YELLOW;
        } else {
            color = Color.RED;
        }
        gra.drawRoundRect(left, top, width - (int) ((float) width / (float) initLife * (float) damage), height, 9.0f, color);
    }


    /** 残りsmash回数分、アイコンを描画する */
    private void drawSmashIcon() {
        Rect srcRect = new Rect(0, 0, 143, 143);
        for (int ii = 0; ii < remainSmash; ii++) {
            gra.drawPixmap(icon_smash, SMASH_DST_RECT_ARR[ii], srcRect);
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
