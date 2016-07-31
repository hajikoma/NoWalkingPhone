package com.hajikoma.nowalkingphone;

import android.graphics.Rect;
import android.util.SparseArray;

import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「操作するプレイヤー」を表すクラス。
 */
public class Player extends SpriteImage {

    /** プレイヤーの健康（怪我）の状態 */
    public static SparseArray<String> HealthArray = new SparseArray<String>() {
        {
            put(2, "無傷");
            put(4, "かすり傷");
            put(6, "すり傷");
            put(8, "ねんざ");
            put(10, "死亡");
        }
    };

    /** アクションの種類を表す定数 */
    public static enum ActionType {
        STANDBY,
        WALK,
        DAMAGE,
        STEP,
        SMASH,
        DEAD
    }


    /** 受けたダメージ量 */
    private int damage = 0;
    /** 現在の健康（怪我）の状態 */
    private String health;

    /** 現在のアクション */
    private ActionType state;
    /** アクションの経過時間 */
    private float actionTime = 0.0f;


    /**
     * Playerを生成する。
     *
     * @param gra       描画のためのgraphicオブジェクト
     * @param visual    Playerの画像セット（スプライト画像）
     * @param rowHeight visualの中の、一画像の高さ
     * @param colWidth  visualの中の、一画像の幅
     * @param location  描画先矩形座標
     */
    public Player(Graphics gra, Pixmap visual, Integer rowHeight, Integer colWidth, Rect location) {
        super(gra, visual, rowHeight, colWidth, location);

        health = HealthArray.get(2);
        state = ActionType.STANDBY;
    }


    /**
     * currentActionTypeに応じたアクションを取らせる
     *
     * @param deltaTime デルタ時間
     */
    public void action(float deltaTime) {

        actionTime += deltaTime;

        switch (state) {
            case STANDBY:
                break;
            case WALK:
                walk();
                break;
            case DAMAGE:
                damage();
                break;
            case STEP:
                step();
                break;
            case SMASH:
                smashed();
                break;
            case DEAD:
                die();
                break;
        }
    }


    /**
     * 歩く
     */
    public void walk() {
        if (actionTime <= 0.3f) {
            drawAction(1, 0);
        } else if (actionTime <= 0.6f) {
            drawAction(1, 1);
        } else {
            endAction();
        }
    }


    /**
     * ダメージを受けた
     */
    public void damage() {
        if (actionTime <= 0.3f) {
            drawAction(0, 0);
        } else if (actionTime <= 0.6f) {
            drawAction(0, 1);
        } else if (actionTime <= 0.9f) {
            drawAction(0, 2);
        } else {
            endAction();
        }
    }


    /**
     * ステップをした
     */
    public void step() {
//        return srcRects[0][0];
    }


    /**
     * スマッシュを受けた
     */
    public void smashed() {
//        srcRects[0][0];
    }


    /**
     * 死亡
     */
    public void die() {
    }


    public void endAction() {
        state = ActionType.WALK;
        actionTime = 0.0f;
    }


    /**
     * 状態を変更する
     */
    public void setState(Player.ActionType actionType) {
        state = actionType;
    }


    /**
     * 状態を取得する
     */
    public ActionType getState() {
        return state;
    }


    public int getDamage() {
        return damage;
    }


    public void addDamage(int damage) {
        this.damage = damage;
    }
}
