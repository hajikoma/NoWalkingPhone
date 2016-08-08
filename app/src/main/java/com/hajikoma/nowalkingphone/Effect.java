package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * エフェクトを扱うクラス。
 */
public class Effect extends SpriteImage {

    /** 描画中かどうか */
    private boolean isAnimate = false;
    /** 経過描画時間 */
    private float timer = 0.0f;
    /** 各画像を表示させる開始秒のタイムテーブル */
    private float timeTable[];


    /**
     * Effectを生成する。
     * Effect用visualは、一行の画像セットのみ扱える。
     *
     * @param visual    Effectの画像セット（スプライト画像）
     * @param colWidth  visualの中の、一画像の幅
     * @param timeTable 各画像を表示させる開始秒の配列。
     *                  ex) visualに3枚画像があり、各0.2秒ずつ表示　→　 [0.2f, 0.4f, 0.6f]
     */
    public Effect(Pixmap visual, Integer colWidth, float timeTable[]) {
        super(visual, visual.getHeight(), colWidth, null);
        this.timeTable = timeTable;
    }


    /**
     * Effectを表示する。
     */
    public void on(Rect dstRect) {
        isAnimate = true;
        setLocation(dstRect);

        int colIndex;
        float tableTimeTotal = 0.0f;
        for (colIndex = 0; colIndex < timeTable.length; colIndex++) {
            tableTimeTotal += timeTable[colIndex];
            if (timer <= tableTimeTotal) {
                drawAction(0, colIndex);
                return;
            }
        }

        // 表示されなかった場合
        isAnimate = false;
        timer = 0.0f;
    }

    /**
     * Effectを表示する。
     */
    public void play(float deltaTime) {
        if(!isAnimate){
            return;
        }

        addDelta(deltaTime);

        int colIndex;
        float tableTimeTotal = 0.0f;
        for (colIndex = 0; colIndex < timeTable.length; colIndex++) {
            tableTimeTotal += timeTable[colIndex];
            if (timer <= tableTimeTotal) {
                drawAction(0, colIndex);
                return;
            }
        }

        // 表示されなかった場合（Effect終了）
        isAnimate = false;
        timer = 0.0f;
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
