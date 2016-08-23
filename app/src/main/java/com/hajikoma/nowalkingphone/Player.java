package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「操作するプレイヤー」を表すクラス。
 */
public class Player extends SpriteImage {


    /** アクションの種類を表す定数 */
    public enum ActionType {
        STANDBY,
        WALK,
        DAMAGE,
        STEP_LEFT,
        STEP_RIGHT,
        SMASH,
        DEAD
    }


    /** lifeの初期値 */
    private static final int INIT_LIFE = 30;
    /** 受けたダメージ量 */
    private int damage = 0;

    /** 現在のアクション */
    private ActionType state;
    /** アクションの経過時間 */
    private float actionTime = 0.0f;

    /** Playerの描画先 */
    private Rect defaultDstArea = new Rect(210, 800, 510, 1100);


    /**
     * Playerを生成する。
     *
     * @param visual   Playerの画像セット（スプライト画像）
     * @param colWidth visualの中の、一画像の幅
     */
    public Player(Pixmap visual, Integer colWidth) {
        super(visual, colWidth, new Rect(210, 800, 510, 1100));
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
        drawAction(0);
    }


    /**
     * 歩く
     */
    public void walk() {
        drawAction(0);
    }


    /**
     * ダメージを受けた
     */
    public void damage() {
        if (actionTime <= 0.2f) {
            move(2, 3);
            drawAction(0);
        } else if (actionTime <= 0.4f) {
            move(-2, 2);
            drawAction(0);
        } else if (actionTime <= 0.6f) {
            move(1, 2);
            drawAction(0);
        } else if (actionTime <= 0.8f) {
            move(-1, 1);
            drawAction(0);
        } else if (actionTime <= 1.0f) {
            move(0, 1);
            drawAction(0);
        } else {
            drawAction(0);
            resetDstArea();
            endAction();
        }
    }


    /**
     * 左にステップをした
     */
    public void stepLeft() {
        if (actionTime <= 0.2f) {
            move(2, 0);
            drawAction(2);
        } else if (actionTime <= 0.4f) {
            move(-2, 0);
            drawAction(2);
        } else if (actionTime <= 0.6f) {
            move(2, 0);
            drawAction(2);
        } else if (actionTime <= 0.8f) {
            move(-2, 0);
            drawAction(2);
        } else if (actionTime <= 1.0f) {
            move(2, 0);
            drawAction(2);
        } else {
            drawAction(2);
            resetDstArea();
            endAction();
        }
    }


    /**
     * 右にステップをした
     */
    public void stepRight() {
        if (actionTime <= 1.0f) {
            drawAction(1);
        } else {
            drawAction(0);
            resetDstArea();
            endAction();
        }
    }


    /**
     * スマッシュ
     */
    public void smash() {
        if (actionTime <= 0.4f) {
            move(0, 2);
            drawAction(1);
        } else if (actionTime <= 0.8f) {
            move(0, -2);
            drawAction(1);
        } else if (actionTime <= 1.2f) {
            move(0, 2);
            drawAction(1);
        } else {
            drawAction(1);
            resetDstArea();
            endAction();
        }
    }


    /**
     * 死亡
     */
    public void die() {
    }


    public void endAction() {
        setState(ActionType.WALK);
    }


    /**
     * レベルアップ時の処理
     */
    public void lvUp() {
        if (damage >= 1) {
            damage--; // life回復
        }
    }


    /**
     * 状態を変更する
     */
    public void setState(Player.ActionType actionType) {
        state = actionType;
        actionTime = 0.0f;
        resetDstArea();

        if (state == ActionType.STEP_LEFT) {
            dstRect.left -= 220;
            dstRect.right -= 220;
        } else if (state == ActionType.STEP_RIGHT) {
            dstRect.left += 220;
            dstRect.right += 220;
        } else if (state == ActionType.SMASH) {
            dstRect.left += 220;
            dstRect.right += 220;
        }
    }


    /**
     * 描画先矩形座標を初期位置に戻す
     */
    private void resetDstArea() {
        dstRect.left = defaultDstArea.left;
        dstRect.top = defaultDstArea.top;
        dstRect.right = defaultDstArea.right;
        dstRect.bottom = defaultDstArea.bottom;
    }


    /**
     * 描画先矩形座標をずらし、描画位置を変更する。
     */
    protected void move(int distanceX, int distanceY) {
        dstRect.left += distanceX;
        dstRect.right += distanceX;
        dstRect.top += distanceY;
        dstRect.bottom += distanceY;
    }


    /**
     * 状態を取得する
     */
    public ActionType getState() {
        return state;
    }


    public int getInitLife() {
        return INIT_LIFE;
    }


    public int getDamage() {
        return damage;
    }


    public void addDamage(int damage) {
        this.damage += damage;
    }
}
