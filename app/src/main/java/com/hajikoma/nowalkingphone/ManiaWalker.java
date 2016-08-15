package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩きオタク」を表すクラス。
 * 動きは遅いが、体力が高くあたると痛い。
 */
public class ManiaWalker extends Walker implements Cloneable {

    /**
     * {@inheritDoc}
     */
    public ManiaWalker(Pixmap visual, Integer rowHeight, Integer colWidth, Rect location) {
        super("歩きオタク", "のろいが丈夫", 2, 2, 3, 4, visual, rowHeight, colWidth, location);
    }


    /**
     * ManiaWalkerを複製する。
     */
    @Override
    public ManiaWalker clone() {

        ManiaWalker newWalker = null;

        /*ObjectクラスのcloneメソッドはCloneNotSupportedExceptionを投げる可能性があるので、try-catch文で記述(呼び出し元に投げても良い)*/
        try {
            newWalker = (ManiaWalker) super.clone(); // 親クラスのcloneメソッドを呼び出す。親クラスの型で返ってくるので、自分自身の型でのキャスト
            newWalker.dstRect = null; // オブジェクト型変数なので、clone()では参照がコピーされる
            newWalker.isEnlarged = null;
            newWalker.isEnlarged = new boolean[]{false, false, false};
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newWalker;
    }
}
