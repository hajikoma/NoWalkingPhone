package com.hajikoma.nowalkingphone.screen;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.NoWalkingPhoneGame;
import com.hajikoma.nowalkingphone.UserData;
import com.hajikoma.nowalkingphone.framework.Audio;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.Text;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;


/**
 * アプリ起動後、最初に使用するスクリーン。 ゲーム内で使用する画像、音楽、デフォルト設定、ユーザーデータなどを読み込む。
 * データ読み込みが完了次第、次のスクリーンに処理を移す。
 * ユーザーデータ読み込み失敗時以外、描画は行わない。
 */
public class LoadingScreen extends Screen {

    private Game game;
    private Graphics gra;
    private Text txt;

    /** ユーザーデータの読み込みが成功したかどうかのフラグ */
    private boolean isLoadOK;


    /** LoadingScreenを生成し、データをセットアップする */
    public LoadingScreen(Game game) {
        super(game);
        this.game = game;
        gra = game.getGraphics();
        txt = game.getText();
        Audio aud = game.getAudio();

        // デバッグ用：preferenceを削除
//        SharedPreferences.Editor editor = getSharedPreference().edit();
//        editor.clear().apply();

        // ユーザーデータのロード処理
        Assets.ud = UserData.getUserData();
        if (getIntFromPref(UserData.PREF_INT_LAUNCH_TIME) == 0) {
            // 初回起動時の処理
            isLoadOK = true;
            Assets.ud.saveAllToPref(getSharedPreference());
        } else {
            // 二回目以降の処理。読み込み失敗時は初期値をそのまま使用
            isLoadOK = Assets.ud.getAllFromPref(getSharedPreference());
        }
        Assets.ud.addLaunchTime();

        // user_idがなければ、DBから新規に取得。同時に、usersテーブル内にユーザー専用の階層を作成する
        if(Assets.ud.getUserId().equals("")){
            String userId;
            userId = ((AndroidGame)game).dbManager.getUserId(Assets.ud.getUserName(), Assets.ud.getFirstScore());
            Assets.ud.setUserId(userId);
        }

        //共有グラフィックの読み込み
        Assets.number = gra.newPixmap("others/number.png", PixmapFormat.ARGB4444);

        Assets.walker_boy = gra.newPixmap("walker/walker_boy.png", PixmapFormat.ARGB4444);
        Assets.walker_car = gra.newPixmap("walker/walker_car.png", PixmapFormat.ARGB4444);
        Assets.walker_girl = gra.newPixmap("walker/walker_girl.png", PixmapFormat.ARGB4444);
        Assets.walker_grandma = gra.newPixmap("walker/walker_grandma.png", PixmapFormat.ARGB4444);
        Assets.walker_man = gra.newPixmap("walker/walker_man.png", PixmapFormat.ARGB4444);
        Assets.walker_mania = gra.newPixmap("walker/walker_mania.png", PixmapFormat.ARGB4444);
        Assets.walker_visitor = gra.newPixmap("walker/walker_visitor.png", PixmapFormat.ARGB4444);


        // サウンドの読み込み
        Assets.voice_amai = aud.newSound("sound/voice_amai.mp3");
        Assets.voice_nanto = aud.newSound("sound/voice_nanto.mp3");
        Assets.voice_mieru = aud.newSound("sound/voice_mieru.mp3");
        Assets.voice_soko = aud.newSound("sound/voice_soko.mp3");
        Assets.voice_abunai = aud.newSound("sound/voice_abunai.mp3");
        Assets.voice_douda= aud.newSound("sound/voice_douda.mp3");
        Assets.voice_futtonjae = aud.newSound("sound/voice_futtonjae.mp3");
        Assets.voice_itete = aud.newSound("sound/voice_itete.mp3");
        Assets.voice_kurae = aud.newSound("sound/voice_kurae.mp3");

        Assets.decision15 = aud.newSound("sound/decision15.mp3");
        Assets.peoplePerformanceCheer1 = aud.newSound("sound/people-performance-cheer1.mp3");
        Assets.peopleStadiumCheer1 = aud.newSound("sound/people-stadium-cheer1.mp3");
        Assets.punchSwing1 = aud.newSound("sound/punch-swing1.mp3");
        Assets.bomb1 = aud.newSound("sound/bomb1.mp3");
        Assets.kickLow1 = aud.newSound("sound/kick-low1.mp3");
        Assets.punchMiddle2 = aud.newSound("sound/punch-middle2.mp3");
        Assets.magicElectron2 = aud.newSound("sound/magic-electron2.mp3");
        Assets.incorrect1 = aud.newSound("sound/incorrect1.mp3");
        Assets.setup1 = aud.newSound("sound/setup1.mp3");

        // バイブパターンのセットアップ
        Assets.vibShortOnce = 50;
        Assets.vibLongOnce = 150;

        // スタイルのセットアップ
        Assets.style_general_black = new Paint();
        Assets.style_general_black.setAntiAlias(true);
        Assets.style_general_black.setTextSize(NoWalkingPhoneGame.FONT_SIZE_GENERAL);
        Assets.style_general_white = new Paint();
        Assets.style_general_white.setAntiAlias(true);
        Assets.style_general_white.setTextSize(NoWalkingPhoneGame.FONT_SIZE_GENERAL);
        Assets.style_general_white.setColor(Color.WHITE);
        Assets.style_general_black_big = new Paint();
        Assets.style_general_black_big.setAntiAlias(true);
        Assets.style_general_black_big.setTextSize(NoWalkingPhoneGame.FONT_SIZE_M);
        Assets.style_general_white_big = new Paint();
        Assets.style_general_white_big.setAntiAlias(true);
        Assets.style_general_white_big.setTextSize(NoWalkingPhoneGame.FONT_SIZE_M);
        Assets.style_general_white_big.setColor(Color.WHITE);
    }

    /** 次のスクリーンに処理を移す。 */
    @Override
    public void update(float deltaTime) {
        // データ読み込み失敗時、ダイアログに警告表示
        if (!isLoadOK) {
            final NoWalkingPhoneGame nwp = (NoWalkingPhoneGame) game;
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    StringBuilder strBuilder = new StringBuilder();
                    String separator = System.getProperty("line.separator");
                    strBuilder.append("セーブデータの読み込みに失敗しました。");
                    strBuilder.append(separator);
                    strBuilder.append("主な原因");
                    strBuilder.append(separator);
                    strBuilder.append("・SDカードが挿さっていない、または認識されていない");
                    strBuilder.append(separator);
                    strBuilder.append("・何らかの操作によりセーブデータが消去された");
                    strBuilder.append(separator);
                    strBuilder.append(separator);
                    strBuilder.append("初期状態でゲームを開始します...");
                    nwp.showConfirmDialog(
                            "セーブデータの読み込みに失敗しました",
                            strBuilder.toString(),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //描画を再開
                                    nwp.getCurrentScreen().resume();
                                    nwp.getRenderView().resume();
                                }
                            }
                    );
                }
            };
            nwp.postRunnable(run);
            isLoadOK = true;
        } else {
            game.setScreen(new SplashScreen(game));
        }
    }

    @Override
    public void present(float deltaTime) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public String toString() {
        return "LoadingScreen";
    }
}
