package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩きおばあさん」を表すクラス。
 * 動きが遅くよく立ち止まるので、画面にいる時間が長い。
 */
public class GrandmaWalker extends Walker implements Cloneable {

    /**
     * {@inheritDoc}
     */
    public GrandmaWalker(Pixmap visual, Integer rowHeight, Integer colWidth, Rect location) {
        super("歩きおばあさん", "よく立ち止まる", 1, 2, 2, 3, visual, rowHeight, colWidth, location);
    }


    /**
     * GrandmaWalkerを複製する。
     */
    @Override
    public GrandmaWalker clone() {

        GrandmaWalker newWalker = null;

        /*ObjectクラスのcloneメソッドはCloneNotSupportedExceptionを投げる可能性があるので、try-catch文で記述(呼び出し元に投げても良い)*/
        try {
            newWalker = (GrandmaWalker) super.clone(); // 親クラスのcloneメソッドを呼び出す。親クラスの型で返ってくるので、自分自身の型でのキャスト
            newWalker.dstRect = null; // オブジェクト型変数なので、clone()では参照がコピーされる
            newWalker.isEnlarged = null;
            newWalker.isEnlarged = new boolean[]{false, false, false};
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newWalker;
    }


    /**
     * よく立ち止まる
     */
    @Override
    protected void walk() {
        if (dstRect.top <= 1200) {
            if (actionTime <= 0.3f) {
                drawAction(0, 0);
                move(-1, speed);
            } else if (actionTime <= 0.6f) {
                drawAction(0, 0);
                move(1, speed);
            } else {
                loopAction();
                walk();
            }

            // よく立ち止まる
            if (Math.random() > 0.99) {
                setState(ActionType.STOP);
            }
        } else {
            vanish();
        }
    }
}
