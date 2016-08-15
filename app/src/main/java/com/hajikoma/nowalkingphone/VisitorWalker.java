package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩き外人さん」を表すクラス。
 * 全体的に厄介な強敵。
 */
public class VisitorWalker extends Walker implements Cloneable {

    /**
     * {@inheritDoc}
     */
    public VisitorWalker(Pixmap visual, Integer rowHeight, Integer colWidth, Rect location) {
        super("歩き外人さん", "全体的に強い", 3, 3, 3, 5, visual, rowHeight, colWidth, location);
    }


    /**
     * VisitorWalkerを複製する。
     */
    @Override
    public VisitorWalker clone() {

        VisitorWalker newWalker = null;

        /*ObjectクラスのcloneメソッドはCloneNotSupportedExceptionを投げる可能性があるので、try-catch文で記述(呼び出し元に投げても良い)*/
        try {
            newWalker = (VisitorWalker) super.clone(); // 親クラスのcloneメソッドを呼び出す。親クラスの型で返ってくるので、自分自身の型でのキャスト
            newWalker.dstRect = null; // オブジェクト型変数なので、clone()では参照がコピーされる
            newWalker.isEnlarged = null;
            newWalker.isEnlarged = new boolean[]{false, false, false};
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newWalker;
    }
}
