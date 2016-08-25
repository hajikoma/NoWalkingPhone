package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Walkerの出現や状態を制御するクラス。
 */
public class WalkerManager {

    /** Walkerの種類別定数 */
    public static final int RANDOM = -1;
    public static final int MAN = 0;
    public static final int GRANDMA = 1;
    public static final int SCHOOL = 2;
    public static final int GIRL = 3;
    public static final int MANIA = 4;
    public static final int VISITOR = 5;
    public static final int CAR = 6;

    /** Walkerの同時出現上限数 */
    public int maxWalker;

    /** 出現テーブル */
    public int generateTable[] = new int[50];

    /** 置換テーブル */
    public int replaceTable[][] = {
            {MAN, MAN},
            {MAN, SCHOOL},
            {MAN, MAN},
            {MAN, GRANDMA},
            {MAN, MAN},
            {MAN, GIRL},
            {MAN, MAN},
            {MAN, MANIA},
            {MAN, MAN},
            {MAN, VISITOR},
            {MAN, MAN},
            {MAN, CAR}
    };

    /** 置換テーブルのイテレータ的役割のインデックス */
    public int replaceIndex = 0;

    /** 各Walkerの解禁状況（出現最低レベルに達し、一度は固定で出現しているかどうか） */
    public boolean appearedFlags[] = new boolean[]{true, false, false, false, false, false, false};

    /** ランダムな数を得るのに使用 */
    private Random random = new Random();


    public WalkerManager(int initMaxWalker)
    {
        maxWalker = initMaxWalker;
        Arrays.fill(generateTable, 0, 1, MAN);
    }


    /**
     * 指定のWalkerインスタンスを返す
     *
     * @param walkerType 取得するWalkerのタイプ。本クラスの定数が渡されることを想定している
     */
    public Walker getTheWalker(int walkerType, Rect appearRect) {

        Walker newWalker;
        switch (walkerType) {
            case MAN:
                newWalker = new ManWalker(Assets.walker_man, 200, null);
                break;
            case GRANDMA:
                newWalker = new GrandmaWalker(Assets.walker_grandma, 200, null);
                break;
            case SCHOOL:
                newWalker = new SchoolWalker(Assets.walker_boy, 200, null);
                break;
            case GIRL:
                newWalker = new GirlWalker(Assets.walker_girl, 200, null);
                break;
            case MANIA:
                newWalker = new ManiaWalker(Assets.walker_mania, 200, null);
                break;
            case VISITOR:
                newWalker = new VisitorWalker(Assets.walker_visitor, 200, null);
                break;
            case CAR:
                newWalker = new CarWalker(Assets.walker_car, 200, null);
                break;
            default:
                newWalker = new ManWalker(Assets.walker_man, 200, null);
                break;
        }
        newWalker.setLocation(appearRect);

        return newWalker;
    }


    /** ランダムにWalkerインスタンスを返す */
    public Walker getSomeWalker(Rect appearRect) {
        int type = WalkerManager.RANDOM;

        // 出現させて良いWalkerが出たら、インスタンス取得
        while (type == WalkerManager.RANDOM) {
            type = generateTable[random.nextInt(generateTable.length)];
            if (!appearedFlags[type]) {
                type = WalkerManager.RANDOM;
            }
        }

        return getTheWalker(type, appearRect);
    }


    public void replaceGenerateTable() {
        int from = replaceTable[replaceIndex][0];
        int to = replaceTable[replaceIndex][1];

        Arrays.sort(generateTable);
        int targetIndex = Arrays.binarySearch(generateTable, from);

        // 置換対象が存在する場合
        if (targetIndex != -1) {
            generateTable[targetIndex] = to;
        }

        replaceIndex++;
        if (replaceIndex >= replaceTable.length) {
            replaceIndex = 0;
        }
    }


    /** 全てのWalkerの状態を変更する */
    public void setAllWalkerState(ArrayList<Walker> walkers, Walker.ActionType toState) {
        for (Walker walker : walkers) {
            walker.setState(toState);
        }
    }
}