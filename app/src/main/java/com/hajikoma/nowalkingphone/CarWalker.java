package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「ながらスマホカー」を表すクラス。
 * タップでは（ほぼ）倒せない。
 */
public class CarWalker extends Walker {

    /**
     * {@inheritDoc}
     */
    public CarWalker(Pixmap visual, Integer colWidth, Rect location) {
        super(999, 9, 5, 20, visual, colWidth, location);
    }
}
