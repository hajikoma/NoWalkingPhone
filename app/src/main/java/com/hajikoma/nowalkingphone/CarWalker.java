package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「ながらスマホカー」を表すクラス。
 * タップでは（ほぼ）倒せない。
 */
public class CarWalker extends Walker implements Cloneable {

    /**
     * {@inheritDoc}
     */
    public CarWalker(Pixmap visual, Integer rowHeight, Integer colWidth, Rect location) {
        super("ながらスマホカー", "タップでは倒せない", 999, 9, 5, 20, visual, rowHeight, colWidth, location);
    }


    /**
     * CarWalkerを複製する。
     */
    @Override
    public CarWalker clone() {

        CarWalker newWalker = null;

        /*ObjectクラスのcloneメソッドはCloneNotSupportedExceptionを投げる可能性があるので、try-catch文で記述(呼び出し元に投げても良い)*/
        try {
            newWalker = (CarWalker) super.clone(); // 親クラスのcloneメソッドを呼び出す。親クラスの型で返ってくるので、自分自身の型でのキャスト
            newWalker.dstRect = null; // オブジェクト型変数なので、clone()では参照がコピーされる
            newWalker.isEnlarged = null;
            newWalker.isEnlarged = new boolean[]{false, false, false};
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newWalker;
    }
}
