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
            {BASIC, BASIC},
            {BASIC, SCHOOL},
            {BASIC, WOMAN},
            {BASIC, BASIC},
            {BASIC, SCHOOL},
            {BASIC, MANIA},
            {BASIC, BASIC},
            {BASIC, SCHOOL},
            {BASIC, MANIA},
            {BASIC, MONSTER},
            {BASIC, SCHOOL},
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

        Arrays.sort(generateTable);
        int targetIndex = Arrays.binarySearch(generateTable, from);

        // 置換対象が存在する場合
        if(targetIndex != -1){
            generateTable[targetIndex] = to;
        }

        replaceIndex++;
        if (replaceIndex >= replaceTable.length) {
            replaceIndex = 0;
        }
    }


    /** 全てのWalkerの状態を変更する */
    public void setAllWalkerState(ArrayList<Walker> walkers, Walker.ActionType toState){
        for (Walker walker : walkers) {
            walker.setState(toState);
        }
    }
}