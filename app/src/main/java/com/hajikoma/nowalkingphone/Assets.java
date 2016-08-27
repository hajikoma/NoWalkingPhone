package com.hajikoma.nowalkingphone;


import android.graphics.Paint;

import com.hajikoma.nowalkingphone.framework.Pixmap;
import com.hajikoma.nowalkingphone.framework.Sound;


/**
 * Assetsフォルダのリソースを格納する変数のみで構成され、リソースの利用方法を一元化している。
 * 設計上は、各Screen共通で使用するリソースはLoadingScreenで読み込み、
 * 特定のScreenでのみ使用するリソースは各Screenのコンストラクタで読み込む。
 * NOTE:リソース一つにつき一つの変数を用意すること。Screen変更時のnullPointerExceptionと、
 * 大きな画像読み込み時のOutOfMemoryの抑制になる。
 */
public class Assets {

	public static Pixmap number;

	public static Pixmap walker_boy;
	public static Pixmap walker_car;
	public static Pixmap walker_girl;
	public static Pixmap walker_grandma;
	public static Pixmap walker_man;
	public static Pixmap walker_mania;
	public static Pixmap walker_visitor;

	public static Sound voice_amai;
	public static Sound voice_nanto;
	public static Sound voice_mieru;
	public static Sound voice_soko;
	public static Sound voice_abunai;
	public static Sound voice_douda;
	public static Sound voice_futtonjae;
	public static Sound voice_itete;
	public static Sound voice_kurae;

	public static Sound decision15;
	public static Sound peoplePerformanceCheer1;
	public static Sound peopleStadiumCheer1;
	public static Sound punchSwing1;
	public static Sound bomb1;
	public static Sound kickLow1;
	public static Sound punchMiddle2;
	public static Sound magicElectron2;
	public static Sound incorrect1;
	public static Sound setup1;


	public static int vibShortOnce;
	public static int vibLongOnce;

	public static Paint style_general_black;
	public static Paint style_general_black_big;
	public static Paint style_general_white;
	public static Paint style_general_white_big;


//各Screenで共有する変数。Screenが遷移しても値を保持しておきたい変数や、
//メモリの使用量が大きく、都度生成するのが望ましくない変数のみここに定義する。
	public static UserData ud;
	public static Score score;
	public static WalkerManager manager;
}
