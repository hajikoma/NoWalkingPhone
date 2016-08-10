package com.hajikoma.nowalkingphone.screen;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.NoWalkingPhoneGame;
import com.hajikoma.nowalkingphone.UserData;
import com.hajikoma.nowalkingphone.framework.Audio;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Input.GestureEvent;
import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.Text;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;


/**
 * アプリ起動後、最初に使用するスクリーン。 ゲーム内で使用する画像、音楽、デフォルト設定、ユーザーデータなどを読み込む。
 * データ読み込みが完了次第、次のスクリーンに処理を移す。
 * ユーザーデータ読み込み失敗時以外、描画は行わない。
 */
public class LoadingScreen extends Screen {

    protected final Game game;
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

        //テスト用のUserData初期化-------------------------------
/*		int testItemState = 10;
        int[] test = Assets.ud.getAllItemState();
		for(int i = 0; i < test.length; i++){
			Assets.ud.itemUnlock(i);
			Log.d(""+i, "" + Assets.ud.setItemNumber(i, testItemState));
		}*/
        //Assets.ud.setTotalMP(111111);
        //-------------------------------テスト用のUserData初期化


        //共有グラフィックの読み込み
        Assets.icon_hand = gra.newPixmap("others/icon_hand.png", PixmapFormat.ARGB4444);
        Assets.number = gra.newPixmap("others/number.png", PixmapFormat.ARGB4444);

        Assets.walker_boy = gra.newPixmap("walker/walker_boy_right.png", PixmapFormat.ARGB4444);
        Assets.walker_car = gra.newPixmap("walker/walker_car_right.png", PixmapFormat.ARGB4444);
        Assets.walker_girl = gra.newPixmap("walker/walker_girl_right.png", PixmapFormat.ARGB4444);
        Assets.walker_grandma = gra.newPixmap("walker/walker_grandma_right.png", PixmapFormat.ARGB4444);
        Assets.walker_man = gra.newPixmap("walker/walker_man_right.png", PixmapFormat.ARGB4444);
        Assets.walker_mania = gra.newPixmap("walker/walker_mania_right.png", PixmapFormat.ARGB4444);
        Assets.walker_visitor = gra.newPixmap("walker/walker_visitor_right.png", PixmapFormat.ARGB4444);


        // サウンドの読み込み(アイテム使用時のサウンドは、trimScreenにて読み込む)
        Assets.voice_amai = aud.newSound("sound/voice_amai.mp3");
        Assets.voice_nanto = aud.newSound("sound/voice_nanto.mp3");
        Assets.voice_mieru = aud.newSound("sound/voice_mieru.mp3");
        Assets.voice_soko = aud.newSound("sound/voice_soko.mp3");
        Assets.voice_abunai = aud.newSound("sound/voice_abunai.mp3");
        Assets.voice_kamihitoe = aud.newSound("sound/voice_kamihitoe.mp3");

        Assets.decision22 = aud.newSound("sound/decision22.mp3");
        Assets.decision15 = aud.newSound("sound/decision15.mp3");
        Assets.peoplePerformanceCheer1 = aud.newSound("sound/people-performance-cheer1.mp3");
        Assets.peopleStadiumCheer1 = aud.newSound("sound/people-stadium-cheer1.mp3");
        Assets.punchSwing1 = aud.newSound("sound/punch-swing1.mp3");
        Assets.bomb1 = aud.newSound("sound/bomb1.mp3");
        Assets.kickLow1 = aud.newSound("sound/kick-low1.mp3");
        Assets.punchMiddle2 = aud.newSound("sound/punch-middle2.mp3");
        Assets.magicElectron2 = aud.newSound("sound/magic-electron2.mp3");

        //バイブパターンのセットアップ
        Assets.vibShortOnce = 50;
        Assets.vibLongOnce = 150;
        Assets.vibShortRythem = new long[]{50, 50};
        Assets.vibLongRythem = new long[]{150, 150};

        // テキストスタイルのセットアップ
        HashMap<String, Paint> styleMap = new HashMap<String, Paint>();

        Paint general = new Paint();
        general.setTextSize(30);
        general.setAntiAlias(true);
        styleMap.put("general", general);

        Paint title = new Paint();
        title.setTextSize(50);
        title.setAntiAlias(true);
        styleMap.put("title", title);

        Paint score = new Paint();
        score.setTextSize(100);
        score.setAntiAlias(true);
        styleMap.put("score", score);

        Paint big = new Paint();
        big.setTextSize(120);
        big.setAntiAlias(true);
        styleMap.put("big", big);
        Assets.map_style = styleMap;
    }

    /** 次のスクリーンに処理を移す。 */
    @Override
    public void update(float deltaTime) {
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
            game.setScreen(new TitleScreen(game));
        }
    }

    /** セーブデータ読み込み失敗時のみ、エラーをユーザーに通知 */
    @Override
    public void present(float deltaTime) {
    }

    /** このスクリーンでの処理はない */
    @Override
    public void pause() {
    }

    /** このスクリーンでの処理はない */
    @Override
    public void resume() {
    }

    /** このスクリーンでの処理はない */
    @Override
    public void dispose() {
    }

    @Override
    public String toString() {
        return "LoadingScreen";
    }
}
