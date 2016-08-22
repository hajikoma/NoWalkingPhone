package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩きオタク」を表すクラス。
 * 動きは遅いが、体力が高くあたると痛い。
 */
public class ManiaWalker extends Walker {

    /**
     * {@inheritDoc}
     */
    public ManiaWalker(Pixmap visual, Integer colWidth, Rect location) {
        super(2, 2, 3, 4, visual, colWidth, location);
    }
}
