package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 複数画像を切り替えて表示するエフェクトを扱うクラス。
 */
public class SpriteEffect extends SpriteImage {

    /** 描画中かどうか */
    private boolean isAnimate = false;
    /** 経過描画時間 */
    private float timer = 0.0f;
    /** 各画像を表示させる開始秒のタイムテーブル */
    private float timeTable[];
    /** エフェクトをループさせる回数 */
    private int loopNumber;


    /**
     * Effectを生成する。
     * Effect用visualは、一行の画像セットのみ扱える。
     *
     * @param visual    Effectの画像セット（スプライト画像）
     * @param colWidth  visualの中の、一画像の幅
     * @param timeTable 各画像を表示させる開始秒の配列。
     *                  ex) visualに3枚画像があり、各0.2秒ずつ表示　→　 [0.2f, 0.4f, 0.6f]
     */
    public SpriteEffect(Pixmap visual, Integer colWidth, float timeTable[]) {
        super(visual, colWidth, null);
        this.timeTable = timeTable;
    }


    /**
     * Effectを表示する準備をする。
     * ループはしない。
     */
    public void on(Rect dstRect) {
        isAnimate = true;
        setLocation(dstRect);
        this.loopNumber = 0;
    }


    /**
     * Effectを表示する準備をする。
     * loopNumberの回数だけループする。
     */
    public void on(Rect dstRect, int loopNumber) {
        isAnimate = true;
        setLocation(dstRect);
        this.loopNumber = loopNumber;
    }


    /**
     * Effectを実際に表示する。
     */
    public void play(float deltaTime) {
        if (!isAnimate) {
            return;
        }

        addDelta(deltaTime);

        int colIndex;
        float tableTimeTotal = 0.0f;
        for (colIndex = 0; colIndex < timeTable.length; colIndex++) {
            tableTimeTotal += timeTable[colIndex];
            if (timer <= tableTimeTotal) {
                drawAction(colIndex);
                return;
            }
        }

        if (loopNumber >= 1) {
            timer = deltaTime;
            loopNumber--;
            drawAction(0);
            return;
        }

        // 表示されなかった場合（Effect終了）
        isAnimate = false;
        timer = 0.0f;
    }


    public void off() {
        isAnimate = false;
        timer = 0.0f;
        this.loopNumber = 0;
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
