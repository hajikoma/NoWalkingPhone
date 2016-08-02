package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩きスマホする者」を表すクラス。
 * 状態によってさまざまなアクションをする。
 */
public class SmaphoWalker extends Walker {


    /**
     * SmaphoWalkerを生成する。
     *
     * @param gra       描画のためのgraphicオブジェクト
     * @param visual    Walkerの画像セット（スプライト画像）
     * @param rowHeight visualの中の、一画像の高さ
     * @param colWidth  visualの中の、一画像の幅
     * @param location  描画先矩形座標
     */
    public SmaphoWalker(Graphics gra, String name, int hp, int speed, int power, String description, int point,
                        Pixmap visual, Integer rowHeight, Integer colWidth, Rect location) {
        super(gra, name, hp, speed, power, description, point, visual, rowHeight, colWidth, location);
    }


    /**
     * ふらふら歩く
     */
    @Override
    protected void walk() {
        if (actionTime <= 0.3f) {
            drawAction(1, 1);
            move(1, speed);
        } else if (actionTime <= 0.6f) {
            drawAction(1, 1);
            move(-1, speed);
        } else {
            loopAction();
            walk();
        }
    }
}
