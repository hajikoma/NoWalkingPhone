package com.hajikoma.nowalkingphone.framework.impl;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.ad_stir.videoreward.AdstirVideoReward;
import com.ad_stir.videoreward.AdstirVideoRewardListener;
import com.hajikoma.nowalkingphone.DBManager;
import com.hajikoma.nowalkingphone.NoWalkingPhoneGame;
import com.hajikoma.nowalkingphone.framework.Audio;
import com.hajikoma.nowalkingphone.framework.FileIO;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Input;
import com.hajikoma.nowalkingphone.framework.Screen;
import com.hajikoma.nowalkingphone.framework.Text;
import com.hajikoma.nowalkingphone.framework.Vibrate;
import com.hajikoma.nowalkingphone.screen.GameScreen;

/**
 * Gameインターフェースを実装する。
 * ゲームプログラムに必要な各種モジュール（ループ、グラフィック、ミュージック、ファイル）のインスタンスを格納する。
 * 各スクリーンではスクリーン固有の描画やイベントを定義し、モジュールの操作はこのクラスを通じて一手に行う。
 * 実際には、唯一のアクティビティであるこのクラスからアクティブスクリーンを次々と切り替えてゲームを表現する。
 */
public abstract class AndroidGame extends Activity implements Game {

    /** X軸方向のターゲット画面解像度 */
    public static final int TARGET_WIDTH = 720;
    /** Y軸方向のターゲット画面解像度 */
    public static final int TARGET_HEIGHT = 1280;
    /** mainループスレッドを扱う処理するSurfaceView */
    private Handler mainThreadHandler = new Handler();
    /** 高速に描画を行うサブスレッド2つを処理するSurfaceView */
    private AndroidFastRenderView renderView;
    /** 画像処理モジュール */
    public static Graphics graphics;
    /** BGM,効果音モジュール */
    Game game;
    /** BGM,効果音モジュール */
    Audio audio;
    /** ユーザー入力処理モジュール */
    Input input;
    /** データ入出力モジュール */
    FileIO fileIO;
    /** データ入出力モジュール */
    public static Text text;
    /** バイブレーション処理モジュール */
    Vibrate vibrate;
    /** アクティブスクリーン */
    Screen screen;

    /** FirebaseDBへの接続を持つマネージャ */
    public DBManager dbManager;
    /** ネットワーク状態を扱うためのマネージャ */
    public ConnectivityManager conManager;

    /** 広告表示関係 */
    private static final String MEDIA_NO = "MEDIA-31008f2f";
    public boolean isAdLoaded = false;
    public boolean isRewarded = false;
    private AdstirVideoReward adstirVideoReward;


    /**
     * アクティビティ生成時に呼ばれる
     * 画面のセットアップ、フレームワークで使用するメンバの生成などを行う
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbManager = new DBManager(this);
        conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);


        //全画面表示に設定
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //画面解像度の違いを補正する
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        int frameBufferWidth = isLandscape ? TARGET_HEIGHT : TARGET_WIDTH;
        int frameBufferHeight = isLandscape ? TARGET_WIDTH : TARGET_HEIGHT;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth, frameBufferHeight, Config.RGB_565);
        float scaleX;
        float scaleY;

        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        scaleX = (float) frameBufferWidth / size.x;
        scaleY = (float) frameBufferHeight / size.y;

        // 各モジュールのインスタンス化
        game = this;
        renderView = new AndroidFastRenderView(this, frameBuffer);
        graphics = new AndroidGraphics(getAssets(), frameBuffer);
        fileIO = new AndroidFileIO(getAssets());
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, getRenderView(), scaleX, scaleY);
        text = new AndroidText(frameBuffer);
        vibrate = new AndroidVibrate(this);

        // スタート画面をセット（ここ以前にAndroidGameクラスのメンバの初期化は終わらせること）
        screen = getStartScreen();
        setContentView(getRenderView());

        // スクリーンロックをオフに設定
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 広告を読み込み
        // このアクティビティで使用するスポットIDについて、初期化処理
        int[] spotIds = {1};
        AdstirVideoReward.init(this, MEDIA_NO, spotIds);
        // スポットIDごとにインスタンスを生成します。ここでは1についてのみ生成
        adstirVideoReward = new AdstirVideoReward(this, MEDIA_NO, 1);
        // 上で定義したリスナーを登録
        adstirVideoReward.setAdstirVideoRewardListener(listener);
    }

    /** 再開時に呼ばれる */
    @Override
    public void onResume() {
        super.onResume();
        screen.resume();
        getRenderView().resume();
    }

    /** 一時停止時に呼ばれる */
    @Override
    public void onPause() {
        super.onPause();
        getRenderView().pause();
        screen.pause();

        if (isFinishing())
            screen.dispose();
    }

    /** AndroidFastRenderViewインスタンスを取得する */
    @Override
    public AndroidFastRenderView getRenderView() {
        return renderView;
    }

    /** Inputインスタンスを取得する */
    @Override
    public Input getInput() {
        return input;
    }

    /** FileIOインスタンスを取得する */
    @Override
    public FileIO getFileIO() {
        return fileIO;
    }

    /** Graphicsインスタンスを取得する */
    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    /** Audioインスタンスを取得する */
    @Override
    public Audio getAudio() {
        return audio;
    }

    /** Textインスタンスを取得する */
    @Override
    public Text getText() {
        return text;
    }

    /** Vibrateインスタンスを取得する */
    @Override
    public Vibrate getVibrate() {
        return vibrate;
    }

    /**
     * 表示するスクリーンを設定する。既存のスクリーンは一時停止後、破棄される。
     *
     * @param screen アクティブに設定するスクリーン
     */
    @Override
    public void setScreen(Screen screen) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen must not be null");
        }

        this.screen.pause();
        this.screen.dispose();
        System.gc();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }

    /**
     * 現在のスクリーンを取得する
     *
     * @return アクティブな現在のスクリーン
     */
    public Screen getCurrentScreen() {
        return screen;
    }


    /**
     * mainスレッドにrunの処理を依頼する
     *
     * @param run 処理を書いたrunオブジェクト
     */
    public void postRunnable(final Runnable run) {
        mainThreadHandler.post(run);
    }


    /**
     * 何かしらのネットワークに接続しているかどうかを取得する
     */
    public boolean isNetworkConnected() {
        NetworkInfo info = conManager.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }


    /** 広告のリスナーの定義 */
    private AdstirVideoRewardListener listener = new AdstirVideoRewardListener() {
        public void onLoad(int spot_no) {
            Log.e("AD", "onLoad");
            isAdLoaded = true;
        }

        public void onFailed(int spot_no) {
            Log.e("AD", "onFailed");
            isAdLoaded = false;
            prepareAd();
        }

        public void onStart(int spot_no) {
            Log.e("AD", "onStart");
            isAdLoaded = false;
        }

        public void onStartFailed(int spot_no) {
            Log.e("AD", "onStartFailed");
            isAdLoaded = false;
            prepareAd();
        }

        public void onFinished(int spot_no) {
            Log.e("AD", "onFinished");
        }

        public void onReward(int spot_no) {
            Log.e("AD", "onReward");
            isRewarded = true;
        }

        public void onRewardCanceled(int spot_no) {
            Log.e("AD", "onRewardCanceled");
        }

        public void onClose(int spot_no) {
            Log.e("AD", "onClose");
            prepareAd();
            if (isRewarded) {
                game.setScreen(new GameScreen(game, true));
            } else {
                ((NoWalkingPhoneGame) game).showConfirmDialog(
                        "コンティニューに失敗しました",
                        "広告が正しく終了しませんでした。最初からゲームをスタートします…",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                game.setScreen(new GameScreen(game, false));
                            }
                        }
                );
            }
        }
    };


    /**
     * 広告を読み込む
     * 広告データの受信に時間がかかるため、準備と表示のメソッドを分離している
     */
    public void prepareAd() {
        adstirVideoReward.load();
    }


    /**
     * 広告を表示する
     * 広告データの受信に時間がかかるため、準備と表示のメソッドを分離している
     */
    public void showAd() {
        if (!isAdLoaded) {
            prepareAd();
        }
        adstirVideoReward.showRewardVideo();
    }
}
