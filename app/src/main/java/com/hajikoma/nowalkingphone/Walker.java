package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩く者」を表すクラス。
 * 状態によってさまざまなアクションをする。
 */
public class Walker extends SpriteImage {

    /** アクションの種類を表す定数 */
    public static enum ActionType {
        STANDBY,
        WALK,
        DAMAGE,
        SMASHED,
        DEAD
    }

    /** 名前 */
    private String name;
    /** 初期耐久力 */
    private int hp;
    /** 残り耐久力 */
    private int life;
    /** 移動速度 */
    private int speed;
    /** 衝突力 */
    private int power;
    /** 解説文 */
    private String description;
    /** 得点 */
    private int point;

    /** 現在のアクション */
    private ActionType state;
    /** アクションの経過時間 */
    private float actionTime = 0.0f;

    /** 拡大ポイントを通過したか */
    private boolean isEnlarged[] = new boolean[3];


    /**
     * Walkerを生成する。
     *
     * @param gra       描画のためのgraphicオブジェクト
     * @param visual    Walkerの画像セット（スプライト画像）
     * @param rowHeight visualの中の、一画像の高さ
     * @param colWidth  visualの中の、一画像の幅
     * @param location  描画先矩形座標
     */
    public Walker(Graphics gra, String name, int hp, int speed, int power, String description, int point,
                  Pixmap visual, Integer rowHeight, Integer colWidth, Rect location) {
        super(gra, visual, rowHeight, colWidth, location);
        this.name = name;
        this.hp = hp;
        this.speed = speed;
        this.power = power;
        this.description = description;
        this.point = point;

        life = hp;
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
                break;
            case WALK:
                walk();
                break;
            case DAMAGE:
                damage();
                break;
            case DEAD:
                die();
                break;
        }

        if(dstRect.bottom >= 600 && !isEnlarged[0]){
            scale(20);
            isEnlarged[0] = true;
        }else if(dstRect.bottom >= 800 && !isEnlarged[1]){
            scale(20);
            isEnlarged[1] = true;
        }else if(dstRect.bottom >= 1000 && !isEnlarged[2]){
            scale(20);
            isEnlarged[2] = true;
        }
    }


    /**
     * 歩く
     */
    public void walk() {
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


    /**
     * ダメージを受けた
     */
    public void damage() {
        if (actionTime <= 0.1f) {
            drawAction(1, 1);
            move(10, 0);
        } else if (actionTime <= 0.2f) {
            drawAction(1, 1);
            move(-10, 0);
        } else if (actionTime <= 0.3f) {
            drawAction(1, 1);
            move(10, 0);
        } else if (actionTime <= 0.4f) {
            drawAction(1, 1);
            move(-10, 0);
        } else {
            endAction();
        }
    }


    /**
     * スマッシュを受けた
     */
    public void smashed() {
        //return srcRects[0][0];
    }


    /**
     * 画面から消える
     */
    public void die() {
        if (dstRect.bottom > 0) {
            drawAction(1, 1);
            move(0, 0);
            scale(7);
        }
    }


    public void loopAction() {
        actionTime = 0.0f;
    }


    public void endAction() {
        state = ActionType.WALK;
        actionTime = 0.0f;
    }


    public void addDamage(int damage) {
        life -= damage;
    }


    /**
     * 描画先矩形座標をずらし、描画位置を変更する。
     */
    private void move(int distanceX, int distanceY) {
        dstRect.left += distanceX;
        dstRect.right += distanceX;
        dstRect.top += distanceY;
        dstRect.bottom += distanceY;
    }


    /**
     * 描画先矩形座標を拡大/縮小する。
     *
     * @param distance 拡大／縮小距離
     * @return 拡大／縮小後の描画先矩形座標
     */
    private Rect scale(int distance) {
        distance = distance / 2;
        dstRect.left -= distance;
        dstRect.right += distance;
        dstRect.top -= distance;
        dstRect.bottom += distance;

        return dstRect;
    }


    /**
     * 状態を変更する
     */
    public void setState(Walker.ActionType actionType) {
        state = actionType;
        actionTime = 0.0f;
    }


    /**
     * 状態を取得する
     */
    public ActionType getState() {
        return state;
    }


    public String getName() {
        return name;
    }

    public int getHp() {
        return hp;
    }

    public int getLife() {
        return life;
    }

    public float getSpeed() {
        return speed;
    }

    public int getPower() {
        return power;
    }

    public String getDescription() {
        return description;
    }

}
