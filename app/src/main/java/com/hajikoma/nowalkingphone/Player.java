package com.hajikoma.nowalkingphone;

import android.graphics.Point;
import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「操作するプレイヤー」を表すクラス。
 */
public class Player extends SpriteImage {


    /** アクションの種類を表す定数 */
    public static enum ActionType {
        STANDBY,
        WALK,
        DAMAGE,
        STEP_LEFT,
        STEP_RIGHT,
        SMASH,
        DEAD
    }


    /** lifeの初期値 */
    private int initLife = 30;
    /** 受けたダメージ量 */
    private int damage = 0;

    /** 現在のアクション */
    private ActionType state;
    /** アクションの経過時間 */
    private float actionTime = 0.0f;

    /** 現在位置（当たり判定のある矩形座標） */
    private Point hitArea = new Point(0, NoWalkingPhoneGame.TARGET_WIDTH);
    /** Playerの描画先 */
    private Rect defaultDstArea = new Rect(220, 950, 500, 1250);


    /**
     * Playerを生成する。
     *
     * @param visual    Playerの画像セット（スプライト画像）
     * @param rowHeight visualの中の、一画像の高さ
     * @param colWidth  visualの中の、一画像の幅
     */
    public Player(Pixmap visual, Integer rowHeight, Integer colWidth) {
        super(visual, rowHeight, colWidth, new Rect(260, 1000, 460, 1200));
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
            case STANDBY:
                standby();
                break;
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
                smash();
                break;
            case DEAD:
                die();
                break;
        }
    }


    /**
     * 何もしていない
     */
    public void standby() {
        drawAction(0, 0);
    }


    /**
     * 歩く
     */
    public void walk() {
        if (actionTime <= 0.3f) {
            drawAction(0, 0);
        } else if (actionTime <= 0.6f) {
//            drawAction(0, 1);
            drawAction(0, 0);
        } else {
//            drawAction(0, 1);
            drawAction(0, 0);
            endAction();
        }
    }


    /**
     * ダメージを受けた
     */
    public void damage() {
        if (actionTime <= 0.3f) {
//            drawAction(2, 0);
            drawAction(0, 0);
        } else if (actionTime <= 0.6f) {
//            drawAction(2, 1);
            drawAction(0, 0);
        } else if (actionTime <= 0.9f) {
//            drawAction(2, 0);
            drawAction(0, 0);
        } else {
//            drawAction(2, 0);
            drawAction(0, 0);
            endAction();
        }
    }


    /**
     * 左にステップをした
     */
    public void stepLeft() {
        if (hitArea.y == NoWalkingPhoneGame.TARGET_WIDTH) {
            // 中央と右側の当たり判定をなくす
            hitArea.y = NoWalkingPhoneGame.TARGET_WIDTH - 500;
            dstRect.left -= 220;
            dstRect.right -= 220;
        }

        if (actionTime <= 0.2f) {
//            drawAction(1, 0);
            drawAction(0, 0);
        } else if (actionTime <= 0.4f) {
//            drawAction(1, 1);
            drawAction(0, 0);
        } else if (actionTime <= 0.6f) {
//            drawAction(1, 0);
            drawAction(0, 0);
        } else if (actionTime <= 0.8f) {
//            drawAction(1, 1);
            drawAction(0, 0);
        } else if (actionTime <= 1.0f) {
//            drawAction(1, 0);
            drawAction(0, 0);
        } else {
//            drawAction(1, 0);
            drawAction(0, 0);
            hitArea.y = NoWalkingPhoneGame.TARGET_WIDTH;
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
//            drawAction(1, 0);
            drawAction(0, 0);
        } else if (actionTime <= 0.4f) {
//            drawAction(1, 1);
            drawAction(0, 0);
        } else if (actionTime <= 0.6f) {
//            drawAction(1, 0);
            drawAction(0, 0);
        } else if (actionTime <= 0.8f) {
//            drawAction(1, 1);
            drawAction(0, 0);
        } else if (actionTime <= 1.0f) {
//            drawAction(1, 0);
            drawAction(0, 0);
        } else {
//            drawAction(1, 0);
            drawAction(0, 0);
            hitArea.x = 0;
            resetDstArea();
            endAction();
        }
    }


    /**
     * スマッシュ
     */
    public void smash() {
        if (actionTime <= 0.4f) {
//            drawAction(0, 1);
            drawAction(0, 0);
        } else if (actionTime <= 0.8f) {
//            drawAction(0, 1);
            drawAction(0, 0);
        } else if (actionTime <= 1.2f) {
//            drawAction(0, 1);
            drawAction(0, 0);
        } else {
//            drawAction(0, 1);
            drawAction(0, 0);
            endAction();
        }
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
    public void lvUp() {
        if(damage >= 1) {
            damage--; // life回復
        }
    }


    /**
     * 状態を変更する
     */
    public void setState(Player.ActionType actionType) {
        state = actionType;
        actionTime = 0.0f;
        hitArea.x = 0;
        hitArea.y = NoWalkingPhoneGame.TARGET_WIDTH;
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


    public int getInitLife() {
        return initLife;
    }


    public int getDamage() {
        return damage;
    }


    public void addDamage(int damage) {
        this.damage += damage;
    }
}
