package com.hajikoma.nowalkingphone.screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.Settings;
import com.hajikoma.nowalkingphone.UserData;
import com.hajikoma.nowalkingphone.framework.Audio;
import com.hajikoma.nowalkingphone.framework.FileIO;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Input.GestureEvent;
import com.hajikoma.nowalkingphone.framework.Pixmap;
import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.Text;
import com.hajikoma.nowalkingphone.item.DefaultPicker;
import com.hajikoma.nowalkingphone.item.Enhancer;
import com.hajikoma.nowalkingphone.item.Item;
import com.hajikoma.nowalkingphone.item.Item.ItemCategory;
import com.hajikoma.nowalkingphone.item.Item.ItemType;
import com.hajikoma.nowalkingphone.item.Picker;

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
	/** ユーザーデータの読み込み失敗時のエラーインスタンス */
	private Exception error;


	/** LoadingScreenを生成し、データをセットアップする */
	public LoadingScreen(Game game) {
		super(game);
		this.game = game;
		gra = game.getGraphics();
		txt = game.getText();
		Audio aud = game.getAudio();

		//初回起動時と二回目以降でユーザーデータのロード処理の分岐
		if(checkPreferenceState("Launched")){
			//二回目以降の処理
			try {
				Assets.ud = Settings.load(game.getFileIO());
				isLoadOK = true;
			} catch (Exception e) {
				// 読み込み失敗時は初期値を使用
				error = e;
				Assets.ud = UserData.getUserData();
				isLoadOK = false;
			}
		}else{
			//初回起動時の処理
			Assets.ud = UserData.getUserData();
			Settings.save(game.getFileIO(), Assets.ud);
			isLoadOK = true;
			switchPreferenceFlag("Launched", true);
		}


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
		Assets.icons		= gra.newPixmap("others/icons.png",			PixmapFormat.ARGB4444);
		Assets.icon_button	= gra.newPixmap("others/icon_button.png",	PixmapFormat.ARGB4444);
		Assets.number		= gra.newPixmap("others/number.png",		PixmapFormat.ARGB4444);

		Assets.walker_boy   = gra.newPixmap("walker/walker_boy_right.png", PixmapFormat.ARGB4444);
		Assets.walker_car   = gra.newPixmap("walker/walker_car_right.png", PixmapFormat.ARGB4444);
		Assets.walker_girl   = gra.newPixmap("walker/walker_girl_right.png", PixmapFormat.ARGB4444);
		Assets.walker_grandma   = gra.newPixmap("walker/walker_grandma_right.png", PixmapFormat.ARGB4444);
		Assets.walker_man   = gra.newPixmap("walker/walker_man_right.png", PixmapFormat.ARGB4444);
		Assets.walker_mania   = gra.newPixmap("walker/walker_mania_right.png", PixmapFormat.ARGB4444);
		Assets.walker_visitor   = gra.newPixmap("walker/walker_visitor_right.png", PixmapFormat.ARGB4444);


		// サウンドの読み込み(アイテム使用時のサウンドは、trimScreenにて読み込む)
        Assets.voice_amai		= aud.newSound("sound/voice_amai.mp3");
        Assets.voice_nanto		= aud.newSound("sound/voice_nanto.mp3");
        Assets.voice_mieru	    = aud.newSound("sound/voice_mieru.mp3");
        Assets.voice_soko	    = aud.newSound("sound/voice_soko.mp3");
        Assets.voice_abunai	    = aud.newSound("sound/voice_abunai.mp3");
        Assets.voice_kamihitoe	= aud.newSound("sound/voice_kamihitoe.mp3");

        Assets.decision22		= aud.newSound("sound/decision22.mp3");
		Assets.decision15		= aud.newSound("sound/decision15.mp3");
		Assets.peoplePerformanceCheer1	= aud.newSound("sound/people-performance-cheer1.mp3");
		Assets.peopleStadiumCheer1	= aud.newSound("sound/people-stadium-cheer1.mp3");
		Assets.punchSwing1	= aud.newSound("sound/punch-swing1.mp3");
		Assets.bomb1	= aud.newSound("sound/bomb1.mp3");
		Assets.kickLow1	= aud.newSound("sound/kick-low1.mp3");
		Assets.punchMiddle2	= aud.newSound("sound/punch-middle2.mp3");
		Assets.magicElectron2	= aud.newSound("sound/magic-electron2.mp3");

		//バイブパターンのセットアップ
		Assets.vibShortOnce	= 50;
		Assets.vibLongOnce	= 150;
		Assets.vibShortRythem	= new long[]{50,50};
		Assets.vibLongRythem	= new long[]{150,150};

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
		if(!isLoadOK){
			List<GestureEvent> gestureEvents = game.getInput().getGestureEvents();
			game.getInput().getKeyEvents();

			for(int i = 0; i < gestureEvents.size(); i++){
				GestureEvent ges = gestureEvents.get(i);
				if(ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP){
					game.setScreen(new GameScreen(game));
				}
			}

		}else{
			game.setScreen(new GameScreen(game));
		}
	}

	/** セーブデータ読み込み失敗時のみ、エラーをユーザーに通知 */
	@Override
	public void present(float deltaTime) {
		if(!isLoadOK){
			gra.drawRoundRect(new Rect(60, 300, 60 + 600, 300 + 680), 15.0f, Color.LTGRAY);
			txt.drawText("セーブデータの読み込みに失敗しました。",	80, 380, 540,	Assets.map_style.get("title"));
			txt.drawText("error:" + error.getCause(),			80, 500, 540,	Assets.map_style.get("general"));
			txt.drawText("主な原因：",							80, 680, 540,	Assets.map_style.get("general"));
			txt.drawText("・SDカードが挿さっていない、または認識されていない",	100, 730, 520,	Assets.map_style.get("general"));
			txt.drawText("・何らかの操作によりセーブデータが消去された",			100, 810, 520,	Assets.map_style.get("general"));
			txt.drawText("初期データでゲームを開始します...",					80, 920, 540,	Assets.map_style.get("general"));
		}
	}

	/** このスクリーンでの処理はない */
	@Override
	public void pause() {}

	/** このスクリーンでの処理はない */
	@Override
	public void resume() {}

	/** このスクリーンでの処理はない */
	@Override
	public void dispose() {}

	@Override
	public String toString() {
		return "LoadingScreen";
	}
}
