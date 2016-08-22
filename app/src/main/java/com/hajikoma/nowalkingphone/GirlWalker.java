package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩き系女子」を表すクラス。
 * 気分屋で動きが読みづらい。
 */
public class GirlWalker extends Walker {

    /**
     * {@inheritDoc}
     */
    public GirlWalker(Pixmap visual, Integer colWidth, Rect location) {
        super(2, 3, 2, 4, visual, colWidth, location);
    }


    /**
     * 横移動が大きい
     * 速度がたまに変わる
     */
    @Override
    protected void walk() {
        if (dstRect.top <= 1200) {
            if (actionTime <= 0.3f) {
                drawAction(1);
                move(-3, speed);
            } else if (actionTime <= 0.6f) {
                drawAction(0);
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
