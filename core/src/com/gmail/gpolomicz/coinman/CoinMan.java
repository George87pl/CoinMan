package com.gmail.gpolomicz.coinman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] man;
    Texture dizzy;
    int manState;
    int pause;
    float gravity = 0.2f;
    float velocity = 0;
    int manY = 0;
    Rectangle manRectangle;
    BitmapFont font;

    int score = 0;
    int gameState = 0;

    Random random;

    Texture coin;
    int coinCount;
    ArrayList<Integer> coinsXs = new ArrayList<Integer>();
    ArrayList<Integer> coinsYs = new ArrayList<Integer>();
    ArrayList<Rectangle> coinRectagles = new ArrayList<Rectangle>();

    Texture bomb;
    int bombCount;
    ArrayList<Integer> bombsXs = new ArrayList<Integer>();
    ArrayList<Integer> bombsYs = new ArrayList<Integer>();
    ArrayList<Rectangle> bombRectagles = new ArrayList<Rectangle>();


    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[4];
        manY = Gdx.graphics.getHeight() / 2;
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");
        dizzy = new Texture("dizzy-1.png");

        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");
        random = new Random();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);
    }

    public void makeCoin() {
        float height = random.nextFloat() * (Gdx.graphics.getHeight() - coin.getHeight());
        coinsYs.add((int) height);
        coinsXs.add(Gdx.graphics.getWidth());
    }

    public void makeBomb() {
        float height = random.nextFloat() * (Gdx.graphics.getHeight() - bomb.getHeight());
        bombsYs.add((int) height);
        bombsXs.add(Gdx.graphics.getWidth());
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {
            // GAME IS LIVE

            if (coinCount < 100) {
                coinCount++;
            } else {
                makeCoin();
                coinCount = 0;
            }

            coinRectagles.clear();
            for (int i = 0; i < coinsXs.size(); i++) {
                batch.draw(coin, coinsXs.get(i), coinsYs.get(i));
                coinsXs.set(i, coinsXs.get(i) - 4);
                coinRectagles.add(new Rectangle(coinsXs.get(i), coinsYs.get(i), coin.getWidth(), coin.getWidth()));
            }

            if (bombCount < 500) {
                bombCount++;
            } else {
                makeBomb();
                bombCount = 0;
            }

            bombRectagles.clear();
            for (int i = 0; i < bombsXs.size(); i++) {
                batch.draw(bomb, bombsXs.get(i), bombsYs.get(i));
                bombsXs.set(i, bombsXs.get(i) - 6);
                bombRectagles.add(new Rectangle(bombsXs.get(i), bombsYs.get(i), bomb.getWidth(), bomb.getWidth()));

            }

            if (Gdx.input.justTouched()) {
                velocity = -10;
            }


            if (pause < 4) {
                pause++;
            } else {
                pause = 0;
                if (manState < 3) {
                    manState++;
                } else {
                    manState = 0;
                }
            }


            velocity += gravity;
            manY -= velocity;

            if (manY <= 0) {
                manY = 0;
            }

        } else if (gameState == 0) {
            // Waiting to start
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            // GAME OVER
            if (Gdx.input.justTouched()) {
                gameState = 1;
                manY = Gdx.graphics.getHeight() / 2;
                score = 0;
                velocity = 0;
                coinsYs.clear();
                coinsXs.clear();
                coinRectagles.clear();
                coinCount = 0;
                bombsYs.clear();
                bombsXs.clear();
                bombRectagles.clear();
                bombCount = 0;
            }

        }

        if(gameState == 2) {
            batch.draw(dizzy, (Gdx.graphics.getWidth() / 2) - (man[manState].getWidth() / 2), manY);
        } else {
            batch.draw(man[manState], (Gdx.graphics.getWidth() / 2) - (man[manState].getWidth() / 2), manY);
        }
        manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());

        for (int i = 0; i < coinRectagles.size(); i++) {
            if (Intersector.overlaps(manRectangle, coinRectagles.get(i))) {
                score++;

                coinRectagles.remove(i);
                coinsXs.remove(i);
                coinsYs.remove(i);
                break;
            }
        }

        for (int i = 0; i < bombRectagles.size(); i++) {
            if (Intersector.overlaps(manRectangle, bombRectagles.get(i))) {

                bombRectagles.remove(i);
                bombsXs.remove(i);
                bombsYs.remove(i);
                gameState = 2;
                break;

            }
        }

        font.draw(batch, String.valueOf(score), 100, 200);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
