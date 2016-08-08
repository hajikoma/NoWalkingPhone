package com.hajikoma.nowalkingphone;

/**
 * ループするalpha値を生成するヘルパークラス。
 */
public class AlphaGenerator {

    /** alpha値の変化方向（0方向か255方向か） */
    public boolean isReversing = false;
    /** alpha値 */
    private int alpha;
    /** alpha値の最小値兼初期値 */
    private int start;
    /** alpha値の最大値 */
    private int end;
    /** alpha値が一度に変化する量 */
    private int step;


    /** 指定の値の間・指定のジャンプ率でループさせるalpha値を取得する。 */
    public AlphaGenerator(int start, int end, int step) {
        setStart(start);
        setEnd(end);
        setStep(step);
    }


    /** 指定の値の間・指定のジャンプ率でループさせるalpha値を取得する。 */
    public int getAlpha() {
        if (isReversing) {
            alpha -= step;
            if (alpha <= start) {
                isReversing = false;
                alpha = start;
            }
        } else {
            alpha += step;
            if (alpha >= end) {
                isReversing = true;
                alpha = end;
            }
        }

        return alpha;
    }


    public void setStart(int start) {
        if (start < 0 || start > 255) {
            this.start = 255;
        } else {
            this.start = start;
        }
    }


    public void setEnd(int end) {
        if (end < 0 || end > 255) {
            this.end = 0;
        } else {
            this.end = end;
        }
    }


    public void setStep(int step) {
        if (step < 0 || step > 255) {
            this.step = 255;
        } else {
            this.step = step;
        }
    }
}
