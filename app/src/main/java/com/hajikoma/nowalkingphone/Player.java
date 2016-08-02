package com.hajikoma.nowalkingphone;

import android.graphics.Point;
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
        WALK,
        DAMAGE,
        STEP_LEFT,
        STEP_RIGHT,
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

    /** 現在位置（当たり判定のある矩形座標） */
    private Point hitArea = new Point(0, MayuGame.TARGET_WIDTH);
    /** Playerの描画先 */
    private Rect defaultDstArea = new Rect(260, 1000, 460, 1200);


    /**
     * Playerを生成する。
     *
     * @param gra       描画のためのgraphicオブジェクト
     * @param visual    Playerの画像セット（スプライト画像）
     * @param rowHeight visualの中の、一画像の高さ
     * @param colWidth  visualの中の、一画像の幅
     */
    public Player(Graphics gra, Pixmap visual, Integer rowHeight, Integer colWidth) {
        super(gra, visual, rowHeight, colWidth, new Rect(260, 1000, 460, 1200));

        health = HealthArray.get(2);
        state = ActionType.WALK;
    }


    /**
     * currentActionTypeに応じたアクションを取らせる
     *
     * @param deltaTime デルタ時間
     */
    public void action(float deltaTime) {

        actionTime += deltaTime;

        switch (state) {
            case WALK:
                walk();
                break;
            case DAMAGE:
                damage();
                break;
            case STEP_LEFT:
                stepLeft();
                break;
            case STEP_RIGHT:
                stepRight();
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
     * 左にステップをした
     */
    public void stepLeft() {
        if (hitArea.y == MayuGame.TARGET_WIDTH) {
            // 中央と右側の当たり判定をなくす
            hitArea.y = MayuGame.TARGET_WIDTH - 500;
            dstRect.left -= 220;
            dstRect.right -= 220;
        }

        if (actionTime <= 0.2f) {
            drawAction(0, 0);
        } else if (actionTime <= 0.4f) {
            drawAction(0, 1);
        } else if (actionTime <= 0.6f) {
            drawAction(0, 2);
        } else if (actionTime <= 0.8f) {
            drawAction(0, 1);
        } else if (actionTime <= 1.0f) {
            drawAction(0, 2);
        } else {
            hitArea.y = MayuGame.TARGET_WIDTH;
            resetDstArea();
            endAction();
        }
    }


    /**
     * 右にステップをした
     */
    public void stepRight() {
        if (hitArea.x == 0) {
            // 中央と左側の当たり判定をなくす
            hitArea.x = 500;
            dstRect.left += 220;
            dstRect.right += 220;
        }

        if (actionTime <= 0.2f) {
            drawAction(0, 0);
        } else if (actionTime <= 0.4f) {
            drawAction(0, 1);
        } else if (actionTime <= 0.6f) {
            drawAction(0, 2);
        } else if (actionTime <= 0.8f) {
            drawAction(0, 1);
        } else if (actionTime <= 1.0f) {
            drawAction(0, 2);
        } else {
            hitArea.x = 0;
            resetDstArea();
            endAction();
        }
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
        actionTime = 0.0f;
        hitArea.x = 0;
        hitArea.y = MayuGame.TARGET_WIDTH;
        resetDstArea();
    }


    private void resetDstArea(){
        dstRect.left = defaultDstArea.left;
        dstRect.right = defaultDstArea.right;
    }


    /**
     * 状態を取得する
     */
    public ActionType getState() {
        return state;
    }


    public Point getHitArea() {
        return hitArea;
    }


    public int getDamage() {
        return damage;
    }


    public void addDamage(int damage) {
        this.damage = damage;
    }
}
