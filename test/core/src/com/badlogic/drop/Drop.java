package com.badlogic.drop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * @author kay
 * @date 2021/11/30
 */
public class Drop extends ApplicationAdapter {
    private Texture dropImage;
    private Texture bucketImage;
    private Sound dropSound;
    private Music rainMusic;

    private OrthographicCamera camera;
    private SpriteBatch batch;

    private Rectangle bucket;
    private Array<Rectangle> raindrops;
    private long lastDropTime;

    @Override
    public void create() {
        dropImage=new Texture(Gdx.files.internal("drops.png"));
        bucketImage=new Texture(Gdx.files.internal("bucket.png"));
        dropSound=Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        rainMusic=Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        rainMusic.setLooping(true);
        rainMusic.play();

        //camera spriteBatch
        camera=new OrthographicCamera();
        camera.setToOrtho(false,800,480);
        batch=new SpriteBatch();

        bucket=new Rectangle();
        bucket.x=800/2-64/2;
        bucket.y=20;
        bucket.width=64;
        bucket.height=64;
        raindrops=new Array<Rectangle>();
        spawnRainDrop();
    }
    private void spawnRainDrop(){
        Rectangle rain=new Rectangle();
        rain.x= MathUtils.random(0,800-64);
        rain.y=480;
        rain.width=64;
        rain.height=64;
        raindrops.add(rain);
        lastDropTime= TimeUtils.nanoTime();//记录当前时间
    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        batch.dispose();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0,0,0.2f,1);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bucketImage,bucket.x,bucket.y);
        for (Rectangle raindrop : raindrops) {
            batch.draw(dropImage,raindrop.x,raindrop.y);
        }
        //鼠标移动
        if(Gdx.input.isTouched()){
            Vector3 touchPos=new Vector3();
            touchPos.set(Gdx.input.getX(),Gdx.input.getY(),0);
            camera.unproject(touchPos);
            bucket.x=touchPos.x-64/2;
        }
        //键盘移动

        //何时新增雨滴
        if(TimeUtils.nanoTime()-lastDropTime>1000000000) spawnRainDrop();;
        Array.ArrayIterator<Rectangle> iterator = raindrops.iterator();
        while (iterator.hasNext()){
            Rectangle next = iterator.next();
            next.y-=200*Gdx.graphics.getDeltaTime();
            if(next.y+64<0)iterator.remove();
            if(next.overlaps(bucket)){
                dropSound.play();
                iterator.remove();
            }
        }
        batch.end();
    }
}
