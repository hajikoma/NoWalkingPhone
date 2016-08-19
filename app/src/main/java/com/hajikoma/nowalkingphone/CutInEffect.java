package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 画像表示位置を切り替えて表示するエフェクトを扱うクラス。
 */
public class CutInEffect {

    /** カットイン画像 */
    private Pixmap image;
    /** カットイン文字 */
    private String text;
    /** 描画中かどうか */
    private boolean isAnimate = false;
    /** 経過描画時間 */
    private float timer = 0.0f;
    /** 画像を表示させる幅 */
    private int dstWidth;
    /** 画像を表示させる高さ */
    private int dstHeight;
    /** 画像描画位置top */
    private int dstTop = 600;
    /** カットインエフェクトの折り返し時間 */
    private static final float HALF_TIME = 1.5f;


    /**
     * CutInEffectを生成する。
     * CutInEffect用visualは、一枚の画像のみ扱える。
     */
    public CutInEffect(Pixmap image, String text, int dstWidth, int dstHeight) {
        this.image = image;
        this.text = text;
        this.dstWidth = dstWidth;
        this.dstHeight = dstHeight;
    }


    /**
     * CutInEffectを表示する準備をする。
     */
    public void on() {
        isAnimate = true;
    }


    /**
     * CutInEffectを実際に表示する。
     */
    public void play(float deltaTime) {
        if (!isAnimate) {
            return;
        }

        timer += deltaTime;

        if (timer < HALF_TIME) {
            int left = (int) Math.pow((double) ((HALF_TIME - timer) * 30), 2);
            drawImage(left);
            drawText(left);
            return;
        } else if(timer < HALF_TIME + HALF_TIME){
            int left = (int) Math.pow((double) ((timer - HALF_TIME) * 30), 2);
            drawImage(left);
            drawText(left);
            return;
        }

        // 表示されなかった場合（Effect終了）
        isAnimate = false;
        timer = 0.0f;
    }


    public boolean isAnimate() {
        return isAnimate;
    }


    protected void drawImage(int left) {
        NoWalkingPhoneGame.graphics.drawPixmap(image, left, dstTop, dstWidth, dstHeight, 0, 0, image.getWidth(), image.getHeight());
    }


    protected void drawText(int left) {
        NoWalkingPhoneGame.text.drawText(text, left + dstWidth, dstTop, 2000, Assets.style_general_black);
    }
}
