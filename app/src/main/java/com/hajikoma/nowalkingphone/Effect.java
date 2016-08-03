package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * エフェクトを扱うクラス。
 */
public class Effect extends SpriteImage {

    /** 描画中かどうか */
    private boolean isAnimate = false;
    /** 経過描画時間 */
    private float animateTime = 0.0f;
    /** 各画像を表示させる開始秒のタイムテーブル */
    private float timeTable[];


    /**
     * Effectを生成する。
     * Effect用visualは、一行の画像セットのみ扱える。
     *
     * @param gra       描画のためのgraphicオブジェクト
     * @param visual    Effectの画像セット（スプライト画像）
     * @param colWidth  visualの中の、一画像の幅
     * @param timeTable 各画像を表示させる開始秒の配列。
     *                  ex) visualに3枚画像があり、各0.2秒ずつ表示　→　 [0.2f, 0.4f, 0.6f]
     */
    public Effect(Graphics gra, Pixmap visual, Integer colWidth, Rect location, float timeTable[]) {
        super(gra, visual, visual.getHeight(), colWidth, location);
        this.timeTable = timeTable;
    }


    /**
     * Effectを表示する。
     *
     * @return 画像内で描画すべき矩形座標。Effect終了時はnull
     */
    public Rect on() {
        isAnimate = true;

        int colIndex;
        float tableTimeTotal = 0.0f;
        for (colIndex = 0; colIndex < timeTable.length; colIndex++) {
            tableTimeTotal += timeTable[colIndex];
            if (animateTime <= tableTimeTotal) {
                return srcRects[0][colIndex];
            }
        }

        return null;
    }


    public void off() {
        isAnimate = false;
        animateTime = 0.0f;
    }


    /**
     * 経過時間を足す
     */
    public void addDelta(float deltaTime) {
        animateTime += deltaTime;
    }


    public boolean isAnimate() {
        return isAnimate;
    }
}
