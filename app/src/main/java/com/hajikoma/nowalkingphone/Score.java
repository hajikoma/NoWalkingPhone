package com.hajikoma.nowalkingphone;

/**
 *  ゲーム中のスコアを管理するクラス。
 */
public class Score {

	/** ポイント */
	public int point = 0;
	/** 倒した数 */
	public int beatenArray[];

	public Score(int initWalkerNumber){
		beatenArray = new int[initWalkerNumber];
	}
}
