package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩きリーマン」を表すクラス。
 * 特に特徴なし。
 */
public class ManWalker extends Walker {

    /**
     * {@inheritDoc}
     */
    public ManWalker(Pixmap visual, Integer colWidth, Rect location) {
        super(1, 3, 2, 2, visual, colWidth, location);
    }
}
