package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩き小学生」を表すクラス。
 * 動きが速い。
 */
public class SchoolWalker extends Walker {

    /**
     * {@inheritDoc}
     */
    public SchoolWalker(Pixmap visual, Integer colWidth, Rect location) {
        super(1, 5, 1, 3, visual, colWidth, location);
    }
}
