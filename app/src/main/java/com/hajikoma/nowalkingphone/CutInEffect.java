package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 画像表示位置を切り替えて表示するエフェクトを扱うクラス。
 */
public class CutInEffect {

    /** カットイン画像 */
    private Pixmap image;
    /** カットイン背景エフェクト */
    private SpriteEffect backEffect;
    /** 描画中かどうか */
    private boolean isAnimate = false;
    /** 経過描画時間 */
    private float timer = 0.0f;
    /** 画像を表示させる幅 */
    private static final int DST_WIDTH = NoWalkingPhoneGame.TARGET_WIDTH;
    /** 画像を表示させる高さ */
    private static final int DST_HEIGHT = 240;
    /** 画像描画位置top */
    private static final int DST_TOP = 500;
    /** 画像描画位置left */
    private int left = -80;
    /** カットインエフェクト全体の表示時間 */
    private static final float EFFECT_TIME = 3.0f;


    /**
     * CutInEffectを生成する。
     * CutInEffect用visualは、一枚の画像のみ扱える。
     */
    public CutInEffect(Pixmap image, SpriteEffect backEffect) {
        this.image = image;
        this.backEffect = backEffect;
    }


    /**
     * CutInEffectを表示する準備をする。
     */
    public void on() {
        isAnimate = true;
        backEffect.on(new Rect(0, DST_TOP, DST_WIDTH, DST_TOP + DST_HEIGHT), 8);
    }


    /**
     * CutInEffectを実際に表示する。
     *
     * @return 表示したかどうか
     */
    public boolean play(float deltaTime) {
        if (!isAnimate) {
            return false;
        }

        timer += deltaTime;

        if (timer < EFFECT_TIME * 4 / 5) {
            backEffect.play(deltaTime);
            left += 2;
            drawImage(left);
            return true;
        } else if (timer < EFFECT_TIME) {
            // 残り1/5でスライドアウト
            backEffect.play(deltaTime);
            left += (int) Math.pow((double) (timer * 7), 2);
            drawImage(left);
            return true;
        }

        // 表示されなかった場合（Effect終了）
        isAnimate = false;
        backEffect.off();
        timer = 0.0f;
        left = -80;
        return false;
    }


    public boolean isAnimate() {
        return isAnimate;
    }


    protected void drawImage(int left) {
        NoWalkingPhoneGame.graphics.drawPixmap(image, left, DST_TOP, DST_WIDTH, DST_HEIGHT, 0, 0, image.getWidth(), image.getHeight());
    }
}
