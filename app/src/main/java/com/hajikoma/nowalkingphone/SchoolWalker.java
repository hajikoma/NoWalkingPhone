package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩き小学生」を表すクラス。
 * 動きが速い。
 */
public class SchoolWalker extends Walker implements Cloneable {

    /**
     * {@inheritDoc}
     */
    public SchoolWalker(Pixmap visual, Integer rowHeight, Integer colWidth, Rect location) {
        super("歩き小学生", "ちょこまか動きが早い", 1, 5, 1, 3, visual, rowHeight, colWidth, location);
    }


    /**
     * SchoolWalkerを複製する。
     */
    @Override
    public SchoolWalker clone() {

        SchoolWalker newWalker = null;

        /*ObjectクラスのcloneメソッドはCloneNotSupportedExceptionを投げる可能性があるので、try-catch文で記述(呼び出し元に投げても良い)*/
        try {
            newWalker = (SchoolWalker) super.clone(); // 親クラスのcloneメソッドを呼び出す。親クラスの型で返ってくるので、自分自身の型でのキャスト
            newWalker.dstRect = null; // オブジェクト型変数なので、clone()では参照がコピーされる
            newWalker.isEnlarged = null;
            newWalker.isEnlarged = new boolean[]{false, false, false};
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newWalker;
    }
}
