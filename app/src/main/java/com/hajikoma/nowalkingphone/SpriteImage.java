package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 横一列のスプライト画像を扱いやすくするための抽象クラス。
 * スプライト画像セットから、目的の画像の座標矩形取得を便利に行える。
 */
abstract class SpriteImage {

    /** 画像 */
    private Pixmap visual;
    /** 一画像の幅 */
    private int colWidth;
    /** 各画像の座標マップ。画像内で描画すべき矩形を表す */
    protected Rect[] srcRects;
    /** 画像の描画先矩形座標 */
    protected Rect dstRect;


    /**
     * SpriteImageHandlerを生成する。
     *
     * @param visual    Playerの画像セット（横一列のスプライト画像）
     * @param colWidth  visualの中の、一画像の幅
     */
    public SpriteImage(Pixmap visual, Integer colWidth, Rect dstRect) {
        this.visual = visual;
        this.colWidth = colWidth;
        this.dstRect = dstRect;

        // 一画像の幅が、画像の幅より大きい
        if(colWidth > visual.getWidth()){
            colWidth = visual.getWidth();
        }

        // 画像セット内の各画像の座標矩形配列を作成
        int totalCol = visual.getWidth() / colWidth;
        srcRects = new Rect[totalCol];
            for (int col_i = 0; col_i < totalCol; col_i++) {
                srcRects[col_i] = new Rect(colWidth * col_i, 0, colWidth * (col_i + 1), visual.getHeight());
            }
    }


    protected void drawAction(int colIndex) {
        try {
            NoWalkingPhoneGame.graphics.drawPixmap(getVisual(), dstRect, srcRects[colIndex]);
        } catch (ArrayIndexOutOfBoundsException $e) {
            // 描画元矩形が、画像の範囲外のとき発生→確実に存在するindex0を描画
            drawAction(0);
        }
    }


    public Pixmap getVisual() {
        return visual;
    }


    public int getColWidth() {
        return colWidth;
    }


    public void setLocation(Rect location) {
        dstRect = location;
    }


    public Rect getLocation() {
        return dstRect;
    }
}
