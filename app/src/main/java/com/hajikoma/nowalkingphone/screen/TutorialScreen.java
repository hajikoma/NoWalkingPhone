package com.hajikoma.nowalkingphone.screen;

import com.hajikoma.nowalkingphone.Assets;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Input.GestureEvent;
import com.hajikoma.nowalkingphone.framework.Pixmap;
import com.hajikoma.nowalkingphone.framework.Screen;

import java.util.ArrayList;
import java.util.List;

/**
 * 遊び方を説明する画像を、順に表示するスクリーン。
 */
public class TutorialScreen extends Screen {

    /** 共通して使用するインスタンス */
    private final Graphics gra;

    /** 固有画像 */
    private ArrayList<Pixmap> bgList = new ArrayList<>();

    /** 表示中のページ番号 */
    private int page = 1;


    /**
     * TutorialScreenを生成する
     */
    public TutorialScreen(Game game) {
        super(game);
        gra = game.getGraphics();

        // 固有グラフィックの読み込み
        bgList.add(gra.newPixmap("bg/bg_tutorial_1.jpg", PixmapFormat.RGB565));
        bgList.add(gra.newPixmap("bg/bg_tutorial_2.jpg", PixmapFormat.RGB565));
        bgList.add(gra.newPixmap("bg/bg_tutorial_3.jpg", PixmapFormat.RGB565));
    }


    /** メインループ内で呼ばれる。ループ内のためインスタンスの生成には慎重を期すこと。 */
    @Override
    public void update(float deltaTime) {
        List<GestureEvent> gestureEvents = game.getInput().getGestureEvents();
        game.getInput().getKeyEvents();

        gra.drawPixmap(bgList.get(page - 1), 0, 0);

        // タッチイベントの処理
        for (int gi = 0; gi < gestureEvents.size(); gi++) {
            GestureEvent ges = gestureEvents.get(gi);

            // 稀にgesが正しく格納されないことがあることへの対処
            if (ges == null) {
                continue;
            }

            if (ges.type == GestureEvent.GESTURE_SINGLE_TAP_UP) {
                if (page < bgList.size()) {
                    page++;
                    playSound(Assets.decision15, 1.0f);
                } else {
                    game.setScreen(new TitleScreen(game));
                }
                break;
            }
        }
    }


    @Override
    public void present(float deltaTime) {
    }

    /** 一時停止状態をセットする */
    @Override
    public void pause() {
    }


    /** 再開時の処理 */
    @Override
    public void resume() {
    }


    /** 固有の参照を明示的に切る */
    @Override
    public void dispose() {
    }


    @Override
    public String toString() {
        return "TutorialScreen";
    }
}
