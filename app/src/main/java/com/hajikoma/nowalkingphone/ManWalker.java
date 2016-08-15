package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩きリーマン」を表すクラス。
 * 特に特徴なし。
 */
public class ManWalker extends Walker implements Cloneable {

    /**
     * {@inheritDoc}
     */
    public ManWalker(Pixmap visual, Integer rowHeight, Integer colWidth, Rect location) {
        super("歩きリーマン", "特徴がないのが特徴", 1, 3, 2, 2, visual, rowHeight, colWidth, location);
    }


    /**
     * ManWalkerを複製する。
     */
    @Override
    public ManWalker clone() {

        ManWalker newWalker = null;

        /*ObjectクラスのcloneメソッドはCloneNotSupportedExceptionを投げる可能性があるので、try-catch文で記述(呼び出し元に投げても良い)*/
        try {
            newWalker = (ManWalker) super.clone(); // 親クラスのcloneメソッドを呼び出す。親クラスの型で返ってくるので、自分自身の型でのキャスト
            newWalker.dstRect = null; // オブジェクト型変数なので、clone()では参照がコピーされる
            newWalker.isEnlarged = null;
            newWalker.isEnlarged = new boolean[]{false, false, false};
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newWalker;
    }
}
