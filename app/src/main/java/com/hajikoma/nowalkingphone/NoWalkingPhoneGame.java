package com.hajikoma.nowalkingphone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;

import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;
import com.hajikoma.nowalkingphone.screen.GameScreen;
import com.hajikoma.nowalkingphone.screen.LoadingScreen;


/**
 * AndroidGameクラスを継承する、本ゲーム唯一のアクティビティ。（広告を除く）
 * プログラムの起動窓口（よってデフォルトインテントはこのクラスを設定）
 * 各低レベル処理を行うクラスインスタンスを格納し、スクリーンを切り替え時にはこのクラスが必ず渡される。
 * ライフサイクルに沿ってメインループを管理しデータを保存する。
 */
public class NoWalkingPhoneGame extends AndroidGame{

	/** 初期スクリーンを取得する(このクラスのonCreateで呼ばれる) */
	@Override
    public Screen getStartScreen(){
		return new LoadingScreen(this);
	}


	/**
	 * ブラウザインテントにURIを渡して起動する
	 * @param passURI ブラウザに渡すURI
	 */
	public void startBrowserApp(String passURI){
		Uri uri = Uri.parse(passURI);
		Intent i = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(i);
	}

	/**
	 * 「戻る」ハードウェアキーが押されたときに呼ばれ、現在のScreenに応じた処理を行う。
	 * 描画内容がリアルタイムで変わるScreen(ex.TrimScreen)では、ゲーム終了確認のアラートを表示
	 * それ以外のScreenでは、一画面前へ戻る
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if(event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
				showAlertDialog();
				return false;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	/** アプリ終了確認のアラートを表示する */
	private void showAlertDialog(){
		//描画を止める
		getRenderView().pause();
		getCurrentScreen().pause();

		AlertDialog.Builder aDB = new AlertDialog.Builder(this);
		aDB.setMessage("お手入れを終わる？");

		aDB.setNegativeButton("いいえ",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which){
						//描画を再開
						getCurrentScreen().resume();
						getRenderView().resume();
					}
				});
		aDB.setPositiveButton("はい",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which){
						finish();
					}
				});
		//aDB.setCancelable(true);
		AlertDialog aD = aDB.create();
		aD.show();
	}

}

