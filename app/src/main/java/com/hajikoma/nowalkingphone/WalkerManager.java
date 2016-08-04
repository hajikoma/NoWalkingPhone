package com.hajikoma.nowalkingphone;

import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * ゲームのスコア・コンボ記録、Walkerの生成など、ゲームのパラメータを管理するクラス。
 */
public class WalkerManager {

    /** Walkerの種類別定数 */
    public final int BASIC = 0;
    public final int FRIEND = 1;
    public final int SCHOOL = 2;
    public final int WOMAN = 3;
    public final int MANIA = 4;
    public final int MONSTER = 5;
    public final int CAR = 6;


    /** 倒した数 */
    public int beatenArray[];

    /** Walkerの種類ごとのインスタンスを格納する */
    public ArrayList<Walker> walkerList = new ArrayList<>();

    /** 出現テーブル */
    public int generateTable[] = new int[50];

    /** 置換テーブル */
    public int replaceTable[][] = {
            {BASIC, FRIEND},
            {BASIC, SCHOOL},
            {BASIC, WOMAN},
            {BASIC, MANIA},
            {BASIC, MONSTER},
            {BASIC, CAR}
    };
    /** 置換テーブルのイテレータ的役割のインデックス */
    public int replaceIndex = 0;

    /** ランダムな数を得るのに使用 */
    private Random random = new Random();


    public WalkerManager() {
        beatenArray = new int[7];

        Arrays.fill(generateTable, 0, 1, BASIC);
        generateTable[44] = FRIEND;
        generateTable[45] = SCHOOL;
        generateTable[46] = WOMAN;
        generateTable[47] = MANIA;
        generateTable[48] = MONSTER;
        generateTable[49] = CAR;
    }


    public void addWalker(int index, Walker walker) {
        walkerList.add(index, walker);
    }


    public Walker getWalker(Rect appearRect) {
        int type = generateTable[random.nextInt(generateTable.length)];
        Walker newWalker = walkerList.get(type).clone();
        newWalker.setLocation(appearRect);

        return newWalker;
    }


    public void replaceGenerateTable() {
        int from = replaceTable[replaceIndex][0];
        int to = replaceTable[replaceIndex][1];
        int targetIndex = Arrays.binarySearch(generateTable, from);
        generateTable[targetIndex] = to;

        replaceIndex++;
        if (replaceIndex > replaceTable.length) {
            replaceIndex = 0;
        }
    }


}