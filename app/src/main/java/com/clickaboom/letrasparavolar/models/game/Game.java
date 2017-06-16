package com.clickaboom.letrasparavolar.models.game;

import java.io.Serializable;

/**
 * Created by karen on 14/06/17.
 */

public class Game implements Serializable {
    public int id;
    public String title;
    public String subtitle;
    public int imgResource;
    public String gameType;
    public int btnColor;

    public Game(int id, String title, String subtitle, int imgResource, String gameType, int btnColor) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.imgResource = imgResource;
        this.gameType = gameType;
        this.btnColor = btnColor;
    }
}
