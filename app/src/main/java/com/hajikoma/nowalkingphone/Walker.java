package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import com.hajikoma.nowalkingphone.framework.Pixmap;

/**
 * 「歩きスマホする者」を表すクラス。
 * 状態によってさまざまなアクションをする。
 */
public class Walker extends SpriteImage implements Cloneable {

    /** アクションの種類を表す定数 */
    public enum ActionType {
        STANDBY,
        WALK,
        STOP,
        DAMAGE,
        SMASHED,
        DEAD,
        CRASH,
        VANISH
    }

    /** 名前 */
    public String name;
    /** 解説文 */
    public String description;
    /** 初期耐久力 */
    protected int hp;
    /** 残り耐久力 */
    protected int life;
    /** 移動速度 */
    protected int speed;
    /** 衝突力 */
    protected int power;
    /** 得点 */
    protected int point;

    /** 現在のアクション */
    protected ActionType state;
    /** アクションの経過時間 */
    protected float actionTime = 0.0f;

    /** 拡大ポイントを通過したか */
    protected boolean isEnlarged[];


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
            case STOP:
                stop();
                break;
            case DAMAGE:
                damage();
                break;
            case SMASHED:
                smashed();
                break;
            case DEAD:
                die();
                break;
            case CRASH:
                crash();
                break;
            case VANISH:
                break;
            default:
                walk();
        }

        enlargeIfPoint();
    }


    /**
     * Walkerを生成する。
     *
     * @param visual    Walkerの画像セット（スプライト画像）
     * @param rowHeight visualの中の、一画像の高さ
     * @param colWidth  visualの中の、一画像の幅
     * @param location  描画先矩形座標
     */
    public Walker(String name, String description, int hp, int speed, int power, int point,
                  Pixmap visual, Integer rowHeight, Integer colWidth, Rect location) {
        super(visual, rowHeight, colWidth, location);
        this.name = name;
        this.description = description;
        this.hp = hp;
        this.speed = speed;
        this.power = power;
        this.point = point;

        life = hp;
        isEnlarged = new boolean[]{false, false, false};

        state = ActionType.WALK;
    }


    /**
     * Walkerを複製する。
     */
    @Override
    public Walker clone() {

        Walker newWalker = null;

        /*ObjectクラスのcloneメソッドはCloneNotSupportedExceptionを投げる可能性があるので、try-catch文で記述(呼び出し元に投げても良い)*/
        try {
            newWalker = (Walker) super.clone(); // 親クラスのcloneメソッドを呼び出す。親クラスの型で返ってくるので、自分自身の型でのキャスト
            newWalker.dstRect = null; // オブジェクト型変数なので、clone()では参照がコピーされる
            newWalker.isEnlarged = null;
            newWalker.isEnlarged = new boolean[]{false, false, false};
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newWalker;
    }


    /**
     * 何もしていない
     */
    public void standby() {
        drawAction(1, 0);
    }


    /**
     * ふらふら歩く
     */
    protected void walk() {
        if (dstRect.top <= 1200) {
            if (actionTime <= 0.3f) {
                drawAction(0, 0);
                move(1, speed);
            } else if (actionTime <= 0.6f) {
                drawAction(0, 0);
                move(-1, speed);
            } else {
                loopAction();
                walk();
            }

            // たまに立ち止まる
            if (Math.random() > 0.999) {
                setState(ActionType.STOP);
            }
        } else {
            vanish();
        }
    }


    /**
     * 立ち止まる
     */
    protected void stop() {
        if (actionTime <= 1.2f) {
            drawAction(0, 0);
        } else {
            drawAction(0, 0);
            endAction();
        }
    }


    /**
     * ダメージを受けた
     */
    protected void damage() {
        if (actionTime <= 0.1f) {
            drawAction(0, 0);
            move(10, 0);
        } else if (actionTime <= 0.2f) {
            drawAction(0, 0);
            move(-10, 0);
        } else if (actionTime <= 0.3f) {
            drawAction(0, 0);
            move(10, 0);
        } else if (actionTime <= 0.4f) {
            drawAction(0, 0);
            move(-10, 0);
        } else {
            drawAction(0, 0);
            endAction();
        }
    }


    /**
     * スマッシュを受けた
     */
    protected void smashed() {
        float fallTime = 0.3f;

        if (actionTime <= fallTime) {
            drawAction(0, 0);
        } else if (actionTime < fallTime + 0.05f) {
        } else if (actionTime < fallTime + 0.1f) {
            drawAction(0, 0);
        } else if (actionTime < fallTime + 0.15f) {
        } else if (actionTime < fallTime + 0.2f) {
            drawAction(0, 0);
        } else if (actionTime < fallTime + 0.25f) {
        } else if (actionTime < fallTime + 0.3f) {
            drawAction(0, 0);
        } else if (actionTime < fallTime + 0.35f) {
        } else if (actionTime < fallTime + 0.4f) {
            drawAction(0, 0);
        } else if (actionTime < fallTime + 0.45f) {
        } else {
            vanish();
        }
    }


    /**
     * 体力が尽きた
     */
    protected void die() {
        if (dstRect.bottom > 0) {
            drawAction(0, 0);
            move(0, -20);
            scale(-7);
        } else {
            vanish();
        }
    }


    /**
     * Playerと衝突した
     */
    protected void crash() {
        if (actionTime <= 0.1f) {
            drawAction(0, 0);
            move(10, 0);
        } else if (actionTime <= 0.2f) {
            drawAction(0, 0);
            move(-10, 0);
        } else if (actionTime <= 0.3f) {
            drawAction(0, 0);
            move(10, 0);
        } else if (actionTime <= 0.4f) {
            drawAction(0, 0);
            move(-10, 0);
        } else {
            vanish();
        }
    }


    /**
     * 画面から消えた（消える）
     * この状態では、Walkerインスタンスが破棄されることを期待している。
     */
    protected void vanish() {
        setState(ActionType.VANISH);
    }


    protected void loopAction() {
        actionTime = 0.0f;
    }


    protected void endAction() {
        state = ActionType.WALK;
        actionTime = 0.0f;
    }


    public void addDamage(int damage) {
        life -= damage;
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
     * 描画先矩形座標を拡大/縮小する。
     *
     * @param distance 拡大／縮小距離
     * @return 拡大／縮小後の描画先矩形座標
     */
    protected Rect scale(int distance) {
        distance = distance / 2;
        dstRect.left -= distance;
        dstRect.right += distance;
        dstRect.top -= distance;
        dstRect.bottom += distance;

        return dstRect;
    }


    /**
     * 一定距離で拡大する。
     */
    protected void enlargeIfPoint() {
        if (dstRect.bottom >= 500 && !isEnlarged[0]) {
            scale(50);
            isEnlarged[0] = true;
        } else if (dstRect.bottom >= 700 && !isEnlarged[1]) {
            scale(50);
            isEnlarged[1] = true;
        } else if (dstRect.bottom >= 900 && !isEnlarged[2]) {
            scale(50);
            isEnlarged[2] = true;
        }
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

    public int getPoint() {
        return point;
    }
}
