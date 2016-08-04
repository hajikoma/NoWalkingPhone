package com.hajikoma.nowalkingphone;

/**
 * ゲーム中のスコアを管理するクラス。
 */
public class Score {

    /** スコア */
    public int score = 0;
    /** ゲームレベル */
    public int level = 1;
    /** コンボ数 */
    public int combo = 0;

    private int nextLvUpScore = 100;


    /**
     * scoreを加算する。加算後のscoreでLvUpの判定を行う。
     *
     * @param score 加算するscore
     * @return LvUpしたかどうか
     */
    public boolean addScore(int score) {
        this.score += score;

        if (this.score >= nextLvUpScore) {
            level++;
            nextLvUpScore += 100;
            return true;
        } else {
            return false;
        }
    }
}
