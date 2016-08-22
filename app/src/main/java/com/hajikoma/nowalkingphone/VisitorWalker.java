package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩き外人さん」を表すクラス。
 * 全体的に厄介な強敵。
 */
public class VisitorWalker extends Walker {

    /**
     * {@inheritDoc}
     */
    public VisitorWalker(Pixmap visual, Integer colWidth, Rect location) {
        super(3, 3, 3, 5, visual, colWidth, location);
    }
}
