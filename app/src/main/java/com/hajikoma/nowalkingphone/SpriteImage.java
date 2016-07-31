package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * スプライト画像を扱いやすくするための抽象クラス。
 * スプライト画像セットから、目的の画像の座標矩形取得を便利に行える。
 */
abstract class SpriteImage {

    /** 描画のためのgraphicオブジェクト */
    protected Graphics gra;

    /** 画像 */
    private Pixmap visual;
    /** 一画像の幅 */
    private int colWidth;
    /** 一画像の高さ */
    private int rowHeight;
    /** 各画像の座標マップ。画像内で描画すべき矩形を表す */
    protected Rect[][] srcRects;
    /** 画像の描画先矩形座標 */
    protected Rect dstRect;


    /**
     * SpriteImageHandlerを生成する。
     *
     * @param gra       描画のためのgraphicオブジェクト
     * @param visual    Playerの画像セット（スプライト画像）
     * @param rowHeight visualの中の、一画像の高さ
     * @param colWidth  visualの中の、一画像の幅
     */
    public SpriteImage(Graphics gra, Pixmap visual, Integer rowHeight, Integer colWidth, Rect dstRect) {
        this.gra = gra;
        this.visual = visual;
        this.rowHeight = rowHeight;
        this.colWidth = colWidth;
        this.dstRect = dstRect;

        // 画像セット内の各画像の座標矩形配列を作成
        int totalRow = visual.getHeight() / rowHeight;
        int totalCol = visual.getWidth() / colWidth;
        srcRects = new Rect[totalRow][totalCol];
        for (int row_i = 0; row_i < totalRow; row_i++) {
            for (int col_i = 0; col_i < totalCol; col_i++) {
                srcRects[row_i][col_i] = new Rect(colWidth * col_i, rowHeight * row_i, colWidth * (col_i + 1), rowHeight * (row_i + 1));
            }
        }
    }


    protected void drawAction(int rowIndex, int colIndex){
        gra.drawPixmap(getVisual(), dstRect, srcRects[rowIndex][colIndex]);
    }


    public Pixmap getVisual() {
        return visual;
    }


    public int getRowHeight() {
        return rowHeight;
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
