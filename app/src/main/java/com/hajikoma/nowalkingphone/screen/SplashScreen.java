package com.hajikoma.nowalkingphone.screen;

import android.graphics.Color;

import com.hajikoma.nowalkingphone.NoWalkingPhoneGame;
import com.hajikoma.nowalkingphone.framework.Game;
import com.hajikoma.nowalkingphone.framework.Graphics;
import com.hajikoma.nowalkingphone.framework.Graphics.PixmapFormat;
import com.hajikoma.nowalkingphone.framework.Pixmap;
import com.hajikoma.nowalkingphone.framework.Screen;


/**
 * スプラッシュ画面
 * 表示後、自動的にタイトルに移動する
 */
public class SplashScreen extends Screen {

    private Game game;
    private Graphics gra;

    private Pixmap hajikoma;
    private Pixmap no_walking_phone;

    private float timer = 0.0f;


    public SplashScreen(Game game) {
        super(game);
        this.game = game;
        gra = game.getGraphics();

        hajikoma = gra.newPixmap("others/hajikoma.png", PixmapFormat.ARGB4444);
        no_walking_phone = gra.newPixmap("others/no_walking_phone.png", PixmapFormat.ARGB4444);
    }

    /** 次のスクリーンに処理を移す。 */
    @Override
    public void update(float deltaTime) {
        timer += deltaTime;

        if (timer < 3.0f) {
            gra.drawRect(0, 0, NoWalkingPhoneGame.TARGET_WIDTH, NoWalkingPhoneGame.TARGET_HEIGHT, Color.WHITE);
            gra.drawPixmap(hajikoma, 20, 500);
            gra.drawPixmap(no_walking_phone, 150, 1050);
        } else {
            game.setScreen(new TitleScreen(game));
        }
    }

    @Override
    public void present(float deltaTime) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public String toString() {
        return "SplashScreen";
    }
}
