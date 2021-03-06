package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩きおばあさん」を表すクラス。
 * 動きが遅くよく立ち止まるので、画面にいる時間が長い。
 */
public class GrandmaWalker extends Walker {

    /**
     * {@inheritDoc}
     */
    public GrandmaWalker(Pixmap visual, Integer colWidth, Rect location) {
        super(1, 2, 2, 3, visual, colWidth, location);
    }


    /**
     * よく立ち止まる
     */
    @Override
    protected void walk() {
        if (dstRect.top <= 1200) {
            if (actionTime <= 0.3f) {
                drawAction(1);
                move(-1, speed);
            } else if (actionTime <= 0.6f) {
                drawAction(0);
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
