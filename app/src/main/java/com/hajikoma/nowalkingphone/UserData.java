package com.hajikoma.nowalkingphone;

import java.io.Serializable;

import com.hajikoma.nowalkingphone.item.DefaultPicker;

/**
 * ユーザーのデータを格納するクラス。 データの保存時にはこのクラスを直列化する。
 * データがおかしくなるとユーザーは大変なストレスを感じるため、このクラスの設計は データの安全性を優先して設計する。
 * ・シングルトンなクラスとする（インスタンスの取得はgetUserDataメソッドで行う）
 * ・全てのフィールド変数をカプセル化し、規定範囲外の値が入らないようにする
 * ・staticな列挙型による定数管理 ・例外処理の追加
 * またこのクラスインスタンスへのアクセス数は極力少ない方が良い。
 */
public class UserData implements Serializable {

	/** serialVersionUID(YYYYMMDDHHMM+L) */
	private static final long serialVersionUID = 201502212356L;

    /** 唯一のUserDataインスタンス */
    private static UserData ud = new UserData();

    /** トータルポイント */
    private int totalPoint = 0;
    /** 最大コンボ数 */
    private int maxCombo = 0;
    /** 倒したWalkerの数 */
	private int beatCount = 0;

    /** 設定の状況（ミュート） */
    private boolean mute = false;
    /** 設定の状況（バイブレーション） */
	private boolean vibe = true;


    /**
	 * コンストラクタ。このクラスはシングルトンなクラスなので、このメソッドは外部から利用できない。
     * ミッションとアイテムの初期化を行う。
     */
    private UserData(){
    }


    /** UserDataインスタンスを取得する。複数回このメソッドを実行しても返されるインスタンスは同一である。 */
    public static UserData getUserData(){
        return ud;
    }


    /**
     * ゲームの結果を格納する。
     *
     * @param sc Scoreインスタンス
     */
    public void setGameResult(Score sc) {
        totalPoint += sc.score;

        if(maxCombo < sc.maxCombo){
            maxCombo = sc.maxCombo;
        }

        beatCount += sc.beatCount;
    }



    public int getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(int totalPoint) {
        this.totalPoint = totalPoint;
    }

    public int getBeatCount() {
        return beatCount;
    }

    public void setBeatCount(int beatCount) {
        this.beatCount = beatCount;
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public void setMaxCombo(int maxCombo) {
        this.maxCombo = maxCombo;
    }

	public boolean isMute() {
		return mute;
	}

	public void mute() {
		mute = true;
	}

	public void unMute() {
		mute = false;
	}

	public boolean isVibeOn() {
		return vibe;
	}

	public void vibeOn() {
		vibe = true;
	}

	public void vibeOff() {
		vibe = false;
	}
}
