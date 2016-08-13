package com.hajikoma.nowalkingphone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.KeyEvent;

import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;
import com.hajikoma.nowalkingphone.screen.LoadingScreen;


/**
 * AndroidGameクラスを継承する、本ゲーム唯一のアクティビティ。（広告を除く）
 * プログラムの起動窓口（よってデフォルトインテントはこのクラスを設定）
 * 各低レベル処理を行うクラスインスタンスを格納し、スクリーンを切り替え時にはこのクラスが必ず渡される。
 * ライフサイクルに沿ってメインループを管理しデータを保存する。
 */
public class NoWalkingPhoneGame extends AndroidGame {

    // 汎用文字サイズ
    public static final int FONT_SIZE_GENERAL = 40;
    public static final int FONT_SIZE_S = 60;
    public static final int FONT_SIZE_M = 80;
    public static final int FONT_SIZE_L = 100;
    public static final int FONT_SIZE_XL = 120;

    /** 広告表示アクティビティ */
    private Intent adIntent;


    /** 初期スクリーンを取得する(このクラスのonCreateで呼ばれる) */
    @Override
    public Screen getStartScreen() {
        return new LoadingScreen(this);
    }


    /**
     * ブラウザインテントにURIを渡して起動する
     *
     * @param passURI ブラウザに渡すURI
     */
    public void startBrowserApp(String passURI) {
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
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                showYesNoDialog(
                        "終了確認",
                        "スマ歩に屈しちゃうの？",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //描画を再開
                                getCurrentScreen().resume();
                                getRenderView().resume();
                            }
                        }
                );
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    /**
     * ボタン「はい」だけを持つダイアログを表示する
     * メソッド内でまずsurfaceviewによる描画をpauseするため、再開するにはcallback内でscreen.resume()を呼ぶこと。
     */
    public void showConfirmDialog(
            String title,
            String message,
            DialogInterface.OnClickListener positiveCallBack) {
        //描画を止める
        getRenderView().pause();
        getCurrentScreen().pause();

        AlertDialog.Builder aDB = new AlertDialog.Builder(this);
        // 戻るボタンやダイアログ外のタップでダイアログを閉じないようにする（callbackが実行されないため）
        aDB.setCancelable(false);
        aDB.setTitle(title);
        aDB.setMessage(message);
        aDB.setPositiveButton("はい", positiveCallBack);

        // aDB.setCancelable(true);
        AlertDialog aD = aDB.create();
        aD.show();
    }


    /**
     * ボタン「はい」「いいえ」を持つダイアログを表示する
     * メソッド内でまずsurfaceviewによる描画をpauseするため、再開するにはcallback内でscreen.resume()を呼ぶこと。
     */
    public void showYesNoDialog(
            String title,
            String message,
            DialogInterface.OnClickListener positiveCallBack,
            DialogInterface.OnClickListener negativeCallBack) {
        // 描画を止める
        getRenderView().pause();
        getCurrentScreen().pause();

        AlertDialog.Builder aDB = new AlertDialog.Builder(this);
        // 戻るボタンやダイアログ外のタップでダイアログを閉じないようにする（callbackが実行されないため）
        aDB.setCancelable(false);
        aDB.setTitle(title);
        aDB.setMessage(message);
        aDB.setPositiveButton("はい", positiveCallBack);
        aDB.setNegativeButton("いいえ", negativeCallBack);

        // aDB.setCancelable(true);
        AlertDialog aD = aDB.create();
        aD.show();
    }


    /**
     * ボタン「はい」「いいえ」「後で」を持つダイアログを表示する
     * メソッド内でまずsurfaceviewによる描画をpauseするため、再開するにはcallback内でscreen.resume()を呼ぶこと。
     */
    public void showYesNoLaterDialog(
            String title,
            String message,
            DialogInterface.OnClickListener positiveCallBack,
            DialogInterface.OnClickListener negativeCallBack,
            DialogInterface.OnClickListener laterCallBack) {
        // 描画を止める
        getRenderView().pause();
        getCurrentScreen().pause();

        AlertDialog.Builder aDB = new AlertDialog.Builder(this);
        // 戻るボタンやダイアログ外のタップでダイアログを閉じないようにする（callbackが実行されないため）
        aDB.setCancelable(false);
        aDB.setTitle(title);
        aDB.setMessage(message);
        aDB.setPositiveButton("はい", positiveCallBack);
        aDB.setNegativeButton("いいえ", negativeCallBack);
        aDB.setNeutralButton("あとで", laterCallBack);

        // aDB.setCancelable(true);
        AlertDialog aD = aDB.create();
        aD.show();
    }


    /**
     * 広告表示アクティビティを生成（準備）する。
     * 広告データの受信に時間がかかるため、準備と表示のメソッドを分離している
     */
    public void adActivityPrepare() {
        if (adIntent == null) {
            adIntent = new Intent(getApplicationContext(), AdActivity.class);
        }
    }


    /**
     * 広告表示アクティビティを開始する
     * 広告データの受信に時間がかかるため、準備と表示のメソッドを分離している
     *
     * @throws NullPointerException 事前にadActivityPrepareメソッドを呼んでいない場合スロー
     */
    public void adActivityForward() {
        if (adIntent != null) {
            startActivity(adIntent);

            adIntent = null;
        } else {
            throw new NullPointerException("adActivityPrepareメソッドを事前に呼び出してください");
        }
    }
}

