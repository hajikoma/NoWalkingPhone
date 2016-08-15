package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;
import com.hajikoma.nowalkingphone.framework.impl.AndroidGame;

/**
 * エフェクトを扱うクラス。
 */
public class SmashEffect {

    /** 描画中かどうか */
    private boolean isAnimate = false;
    /** 経過描画時間 */
    private float timer = 0.0f;

    /** 画像 */
    private Pixmap visual1;
    private Pixmap visual2;
    /** 画像内で描画すべき矩形 */
    protected Rect srcRect;
    /** 画像の描画先矩形座標 */
    protected Rect dstRect;
    protected int dstBaseTop = 200;


    /**
     * SmashEffectを生成する。
     *
     * @param visual1 落雷中画像
     * @param visual2 落雷後画像
     */
    public SmashEffect(Pixmap visual1, Pixmap visual2) {
        this.visual1 = visual1;
        this.visual2 = visual2;
        this.dstRect = new Rect(100, dstBaseTop, 100 + visual1.getWidth(), dstBaseTop + visual1.getHeight());
        srcRect = new Rect(0, 0, visual1.getWidth(), visual1.getHeight());
    }


    /**
     * SmashEffect表示フラグを立てる。
     */
    public void on() {
        isAnimate = true;
    }

    /**
     * SmashEffectを表示する。
     */
    public void play(float deltaTime) {
        if (!isAnimate) {
            return;
        }

        addDelta(deltaTime);

        float fallTime = 0.2f;
        float upTime = 0.7f;
        float percentage;
        int height = visual1.getHeight();

        if (timer <= fallTime) {
            percentage = timer / fallTime;
            int bottom = (int) (height * percentage);
            srcRect.bottom = bottom;
            dstRect.bottom = dstBaseTop + bottom;
            AndroidGame.graphics.drawPixmap(visual1, dstRect, srcRect);
        } else if (timer > fallTime && timer <upTime) {
            srcRect.top = 0;
            dstRect.top = dstBaseTop;
            srcRect.bottom = height;
            dstRect.bottom = dstBaseTop + height;
            if(timer < fallTime + 0.05f){
                AndroidGame.graphics.drawPixmap(visual2, dstRect, srcRect);
            }else if(timer < fallTime + 0.1f){

            }else if(timer < fallTime + 0.15f){
                AndroidGame.graphics.drawPixmap(visual2, dstRect, srcRect);
            }else if(timer < fallTime + 0.2f){

            }else if(timer < fallTime + 0.25f){
                AndroidGame.graphics.drawPixmap(visual2, dstRect, srcRect);
            }else if(timer < fallTime + 0.3f){

            }else if(timer < fallTime + 0.35f){
                AndroidGame.graphics.drawPixmap(visual2, dstRect, srcRect);
            }else if(timer < fallTime + 0.4f){

            }else if(timer < fallTime + 0.45f){
                AndroidGame.graphics.drawPixmap(visual2, dstRect, srcRect);
            }
        } else {
            // 終了
            isAnimate = false;
            timer = 0.0f;
        }
    }


    /**
     * 経過時間を足す
     */
    public void addDelta(float deltaTime) {
        timer += deltaTime;
    }


    public boolean isAnimate() {
        return isAnimate;
    }
}
