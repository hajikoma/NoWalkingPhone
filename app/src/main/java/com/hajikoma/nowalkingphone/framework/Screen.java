package com.hajikoma.nowalkingphone.framework;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Rect;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.framework.Input.GestureEvent;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;

import java.util.LinkedHashMap;

public abstract class Screen {
    protected final Game game;

    public Screen(Game game) {
        this.game = game;
    }

    public abstract void update(float deltaTime);

    public abstract void present(float deltaTime);

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();

    /** 現在のスクリーンに応じた処理を行う場合に備え、toString()のオーバーライドを強制 */
    public abstract String toString();


    /** SharedPreferencesインスタンスを取得する */
    public SharedPreferences getSharedPreference() {
        return ((AndroidGame) game).getSharedPreferences("NoWalkingPhone", Context.MODE_PRIVATE);
    }

    /**
     * String型のSharedPreferencesの値を取得する。
     *
     * @param preferenceName Preferenceのキー名
     * @return Preferenceの値。Preferenceの取得に失敗した場合""
     */
    public String getStringFromPref(String preferenceName) {
        try {
            return getSharedPreference().getString(preferenceName, "");
        } catch (ExceptionInInitializerError e) {
            e.getCause().printStackTrace();
            return "";
        }
    }


    /**
     * Float型のSharedPreferencesの値を取得する。
     *
     * @param preferenceName Preferenceのキー名
     * @return Preferenceの値。Preferenceの取得に失敗した場合0.0f
     */
    public float getFloatFromPref(String preferenceName) {
        try {
            return getSharedPreference().getFloat(preferenceName, 0.0f);
        } catch (ExceptionInInitializerError e) {
            e.getCause().printStackTrace();
            return 0.0f;
        }
    }


    /**
     * Int型のSharedPreferencesの値を取得する。
     *
     * @param preferenceName Preferenceのキー名
     * @return Preferenceの値。Preferenceの取得に失敗した場合0
     */
    public int getIntFromPref(String preferenceName) {
        try {
            return getSharedPreference().getInt(preferenceName, 0);
        } catch (ExceptionInInitializerError e) {
            e.getCause().printStackTrace();
            return 0;
        }
    }


    /**
     * Long型のSharedPreferencesの値を取得する。
     *
     * @param preferenceName Preferenceのキー名
     * @return Preferenceの値。Preferenceの取得に失敗した場合0L
     */
    public long getLongFromPref(String preferenceName) {
        try {
            return getSharedPreference().getLong(preferenceName, 0L);
        } catch (ExceptionInInitializerError e) {
            e.getCause().printStackTrace();
            return 0L;
        }
    }


    /**
     * Boolean型のSharedPreferencesの値を取得する。
     *
     * @param preferenceName Preferenceのキー名
     * @return Preferenceの値。Preferenceの取得に失敗した場合false
     */
    public boolean getBoolFromPref(String preferenceName) {
        try {
            return getSharedPreference().getBoolean(preferenceName, false);
        } catch (ExceptionInInitializerError e) {
            e.getCause().printStackTrace();
            return false;
        }
    }


    /**
     * String型のSharedPreferencesの値を変更する。
     *
     * @param preferenceName Preferenceのキー名
     * @param val            Preferenceに格納する値
     */
    public void setStringToPref(String preferenceName, String val) {
        Editor editor = getSharedPreference().edit();
        editor.putString(preferenceName, val);
        editor.commit();
    }


    /**
     * float型のSharedPreferencesの値を変更する。
     *
     * @param preferenceName Preferenceのキー名
     * @param val            Preferenceに格納する値
     */
    public void setFloatToPref(String preferenceName, float val) {
        Editor editor = getSharedPreference().edit();
        editor.putFloat(preferenceName, val);
        editor.commit();
    }


    /**
     * int型のSharedPreferencesの値を変更する。
     *
     * @param preferenceName Preferenceのキー名
     * @param val            Preferenceに格納する値
     */
    public void setIntToPref(String preferenceName, int val) {
        Editor editor = getSharedPreference().edit();
        editor.putInt(preferenceName, val);
        editor.commit();
    }


    /**
     * long型のSharedPreferencesの値を変更する。
     *
     * @param preferenceName Preferenceのキー名
     * @param val            Preferenceに格納する値
     */
    public void setLongToPref(String preferenceName, long val) {
        Editor editor = getSharedPreference().edit();
        editor.putLong(preferenceName, val);
        editor.commit();
    }


    /**
     * boolean型のSharedPreferencesの値を変更する。
     *
     * @param preferenceName Preferenceのキー名
     * @param val            Preferenceに格納する値
     */
    public void setBoolToPref(String preferenceName, boolean val) {
        Editor editor = getSharedPreference().edit();
        editor.putBoolean(preferenceName, val);
        editor.commit();
    }


    /**
     * イベントの座標が矩形領域内にあるかどうかを判定するヘルパー。
     * スワイプ動作（onScrollイベント）の場合、イベントの開始地点と現在位置の両方を判定する。
     *
     * @param e      判定するイベント
     * @param x      矩形領域左上座標x
     * @param y      矩形領域左上座標y
     * @param width  矩形領域の幅
     * @param height 矩形領域の高さ
     * @return イベントが矩形領域内で発生している場合true、領域外の場合false
     */
    public static boolean isBounds(GestureEvent e, int x, int y, int width, int height) {
        if (e.x1 > x && e.x1 < x + width && e.y1 > y && e.y1 < y + height) {
            return true;
        } else if (e.type == GestureEvent.GESTURE_SCROLL) {
            if (e.x2 > x && e.x2 < x + width && e.y2 > y && e.y2 < y + height) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    /**
     * イベントの座標が矩形領域内にあるかどうかを判定するヘルパー。
     * スワイプ動作（onScrollイベント）の場合、イベントの開始地点と現在位置の両方を判定する。
     * expansionの値分、判定する対象領域が拡張される。
     *
     * @param e         判定するイベント
     * @param x         矩形領域左上座標x
     * @param y         矩形領域左上座標y
     * @param width     矩形領域の幅
     * @param height    矩形領域の高さ
     * @param expansion pickerの持つ当たり判定の拡大値
     * @return イベントが矩形領域内で発生している場合true、領域外の場合false
     */
    public static boolean isBounds(GestureEvent e, int x, int y, int width, int height, int expansion) {
        return isBounds(e, x - expansion, y - expansion, width + expansion * 2, height + expansion * 2 + 15);
    }


    /**
     * イベントの座標が矩形領域内にあるかどうかを判定するヘルパー。
     *
     * @param e         判定するイベント
     * @param rect      判定する領域
     * @param expansion 当たり判定の拡大値
     * @return イベントが矩形領域内で発生している場合true、領域外の場合false
     */
    public static boolean isBounds(GestureEvent e, Rect rect, int expansion) {
        int left = rect.left - expansion;
        int width = rect.right + expansion - left;
        int top = rect.top - expansion;
        int height = rect.bottom + expansion - top;

        return isBounds(e, left, top, width, height);
    }


    /**
     * イベントの座標が矩形領域内にあるかどうかを判定するヘルパー。
     *
     * @param e    判定するイベント
     * @param rect 判定する領域
     * @return イベントが矩形領域内で発生している場合true、領域外の場合false
     */
    public static boolean isBounds(GestureEvent e, Rect rect) {
        if (rect.contains(e.x1, e.y1)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 渡された効果音セットから、ランダムで効果音を再生する。
     * 設定でミュートになっている場合は再生しない。
     *
     * @param soundMap 再生する効果音の、キー：再生基準値（0.0～1.0の間）　値：Soundインスタンス のMAP
     * @param volume   音量
     * @return 再生した場合true、ミュートで再生しなかった場合false
     */
    public static boolean playSoundRandom(LinkedHashMap<Double, Sound> soundMap, float volume) {
        double rand = Math.random();
        for (Double key : soundMap.keySet()) {
            if (rand < key) {
                return playSound(soundMap.get(key), volume);
            }
        }

        // 効果音が再生されなかった（soundMapのキー設定が誤っている）
        return false;
    }


    /**
     * 効果音を再生する。設定でミュートになっている場合は再生しない。
     *
     * @param sound  再生する効果音
     * @param volume 音量
     * @return 再生した場合true、ミュートで再生しなかった場合false
     */
    public static boolean playSound(Sound sound, float volume) {
        if (Assets.ud.isMute()) {
            return false;
        } else {
            sound.play(volume);
            return true;
        }
    }


    /**
     * BGMを再生する。設定でミュートになっている場合は再生しない。
     *
     * @param music  再生するBGM
     * @return 再生した場合true、ミュートで再生しなかった場合false
     */
    public static boolean playMusic(Music music) {
        if (Assets.ud.isMute()) {
            if(music.isPlaying()){
                music.stop();
            }
            return false;
        } else {
            music.play();
            return true;
        }
    }


    /**
     * 振動（バイブレーション）させる。設定でバイブOFFになっている場合は再生しない。
     *
     * @param vib          Vibrateインスタンス
     * @param milliseconds 振動させる時間（ミリ秒）
     * @return 振動させた場合true、バイブOFFにより振動させなかった場合false
     */
    public static boolean doVibrate(Vibrate vib, int milliseconds) {
        if (Assets.ud.isVibe()) {
            vib.vibrate(milliseconds);
            return true;
        } else {
            return false;
        }
    }


    /**
     * 繰り返し振動（バイブレーション）させる。設定でバイブOFFになっている場合は再生しない。
     *
     * @param vib     Vibrateインスタンス
     * @param pattern 振動パターン
     * @param repeat  繰り返し振動させる回数
     * @return 振動させた場合true、バイブOFFにより振動させなかった場合false
     */
    public static boolean doVibrate(Vibrate vib, long[] pattern, int repeat) {
        if (!Assets.ud.isVibe()) {
            return false;
        } else {
            vib.vibrate(pattern, repeat);
            return true;
        }
    }


    /**
     * 画像を使って数字を描画する
     *
     * @param drawNumber 描画する数字
     * @param atHeightPx 描画する数値一つ一つの高さ（ピクセル）
     * @param x          描画座標x
     * @param y          描画座標y
     * @param maxDigit   最大表示桁数
     */
    public static void drawGraphicalNumber(int drawNumber, int atHeightPx, int x, int y, int maxDigit) {
        if (drawNumber < 0) {
            drawNumber = 0;
        }

        String numStr = String.valueOf(drawNumber);
        int srcX, offsetX = 0;
        int atWidthPx = atHeightPx * 3 / 4;    //数字一つの解像度は30px＊40px。

        for (int digit = 1; digit <= maxDigit; digit++) {
            // 表示指定,桁数に数値の長さが満たない場合、足りない分だけ描画位置をオフセット
            if (digit <= maxDigit - numStr.length()) {
                offsetX += atWidthPx;
                if ((maxDigit - digit) % 3 == 0) {
                    offsetX += atHeightPx / 3;    //カンマの解像度は10px*40pxで、数字の横幅/3
                }
                continue;
            }

            // 数値を一つずつ取り出して描画
            int charIndex = digit - (maxDigit - numStr.length()) - 1;
            int num = Integer.valueOf(String.valueOf(numStr.charAt(charIndex)));
            srcX = num * 30;
            AndroidGame.graphics.drawPixmap(Assets.number, x + offsetX, y, atWidthPx, atHeightPx, srcX, 0, 30, 40);
            offsetX += atWidthPx;

            // 3桁の区切りカンマを描画
            if (digit != maxDigit && (maxDigit - digit) % 3 == 0) {
                srcX = 300;
                AndroidGame.graphics.drawPixmap(Assets.number, x + offsetX, y, atWidthPx / 3, atHeightPx, srcX, 0, 10, 40);
                offsetX += atHeightPx / 3;    //カンマの解像度は10px*40pxで、数字の横幅/3
            }
        }
    }
}
