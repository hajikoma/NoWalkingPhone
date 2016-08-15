package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩き系女子」を表すクラス。
 * 気分屋で動きが読みづらい。
 */
public class GirlWalker extends Walker implements Cloneable {

    /**
     * {@inheritDoc}
     */
    public GirlWalker(Pixmap visual, Integer rowHeight, Integer colWidth, Rect location) {
        super("歩き系女子", "動きが読みづらい", 2, 3, 2, 4, visual, rowHeight, colWidth, location);
    }


    /**
     * GirlWalkerを複製する。
     */
    @Override
    public GirlWalker clone() {

        GirlWalker newWalker = null;

        /*ObjectクラスのcloneメソッドはCloneNotSupportedExceptionを投げる可能性があるので、try-catch文で記述(呼び出し元に投げても良い)*/
        try {
            newWalker = (GirlWalker) super.clone(); // 親クラスのcloneメソッドを呼び出す。親クラスの型で返ってくるので、自分自身の型でのキャスト
            newWalker.dstRect = null; // オブジェクト型変数なので、clone()では参照がコピーされる
            newWalker.isEnlarged = null;
            newWalker.isEnlarged = new boolean[]{false, false, false};
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newWalker;
    }


    /**
     * 横移動が大きい
     * 速度がたまに変わる
     */
    @Override
    protected void walk() {
        if (dstRect.top <= 1200) {
            if (actionTime <= 0.3f) {
                drawAction(0, 0);
                move(-3, speed);
            } else if (actionTime <= 0.6f) {
                drawAction(0, 0);
                move(3, speed);
            } else {
                loopAction();
                walk();
            }

            // たまに立ち止まる
            double random = Math.random();
            if (random > 0.998) {
                setState(ActionType.STOP);
            }

            // たまに速度が変わる
            if (random > 0.99 && speed > 1) {
                speed = 1;
            } else if (random < 0.01 && speed < 3) {
                speed = 3;
            }
        } else {
            vanish();
        }
    }
}
