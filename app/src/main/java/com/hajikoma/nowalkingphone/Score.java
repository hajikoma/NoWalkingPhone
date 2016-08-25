package com.hajikoma.nowalkingphone;

/**
 * ゲーム中のスコアや状態を保持・管理するクラス。
 */
public class Score {

    /** スコア */
    public int score = 0;
    /** ゲームレベル */
    public int level = 1;
    /** コンボ数 */
    public int combo = 0;
    /** 最大コンボ数 */
    public int maxCombo = 0;
    /** 倒したWalkerの数 */
    public int beatCount = 0;
    /** 次のレベルアップスコア */
    private int nextLvUpScore = 10;
    /** 最大スコア */
    private static final int MAX_SCORE = 999999999;


    /**
     * Walkerを倒した時のscore、comboの処理を行う。
     * ・scoreとcomboを加算する。
     * ・加算後のscoreでLvUpの判定を行う。
     *
     * @param score 加算するscore
     * @return LvUpしたかどうか
     */
    public boolean beatWalker(int score) {
        boolean isLvUp = false;

        this.beatCount++;
        combo++;
        if (combo > maxCombo) {
            maxCombo = combo;
        }

        this.score += score + combo / 10;
        if (this.score > MAX_SCORE) {
            this.score = MAX_SCORE;
        }

        while (this.score >= nextLvUpScore) {
            level++;
            nextLvUpScore += 10 + level;
            isLvUp = true;
        }

        return isLvUp;
    }
}
