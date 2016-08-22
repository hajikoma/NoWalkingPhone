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
    public final int MAN = 0;
    public final int GRANDMA = 1;
    public final int SCHOOL = 2;
    public final int GIRL = 3;
    public final int MANIA = 4;
    public final int VISITOR = 5;
    public final int CAR = 6;


    /** 倒した数 */
    public int beatenArray[];

    /** Walkerの種類ごとのインスタンスを格納する */
    public ArrayList<Walker> walkerList = new ArrayList<>();

    /** 出現テーブル */
    public int generateTable[] = new int[30];

    /** 置換テーブル */
    public int replaceTable[][] = {
            {MAN, MAN},
            {MAN, SCHOOL},
            {MAN, GIRL},
            {MAN, MAN},
            {MAN, SCHOOL},
            {MAN, MANIA},
            {MAN, MAN},
            {MAN, SCHOOL},
            {MAN, MANIA},
            {MAN, VISITOR},
            {MAN, SCHOOL},
            {MAN, CAR}
    };
    /** 置換テーブルのイテレータ的役割のインデックス */
    public int replaceIndex = 0;

    /** ランダムな数を得るのに使用 */
    private Random random = new Random();


    public WalkerManager() {
        Arrays.fill(generateTable, 0, 1, MAN);
    }


    public void addWalker(int index, Walker walker) {
        walkerList.add(index, walker);
    }


    public Walker getWalker(Rect appearRect) {
        int type = generateTable[random.nextInt(generateTable.length)];

        Walker newWalker;
        switch (type) {
            case MAN:
                newWalker = new ManWalker(Assets.walker_man, 200, null);
                break;
            case GRANDMA:
                newWalker = new GrandmaWalker(Assets.walker_grandma, 200, null);
                break;
            case SCHOOL:
                newWalker =  new SchoolWalker(Assets.walker_boy, 200, null);
                break;
            case GIRL:
                newWalker =  new GirlWalker(Assets.walker_girl, 200, null);
                break;
            case MANIA:
                newWalker = new ManiaWalker(Assets.walker_mania, 200, null);
                break;
            case VISITOR:
                newWalker =   new VisitorWalker(Assets.walker_visitor, 200, null);
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