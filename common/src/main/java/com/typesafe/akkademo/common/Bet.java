/**
 *  Copyright (C) 2011-2013 Typesafe, Inc <http://typesafe.com>
 */
package com.typesafe.akkademo.common;

import java.io.Serializable;

public class Bet implements Serializable {
    private final String player;
    private final int game;
    private final int amount;

    public Bet(String player, int game, int amount) {
        this.player = player;
        this.game = game;
        this.amount = amount;
    }

    public String getPlayer() {
        return player;
    }

    public int getGame() {
        return game;
    }

    public int getAmount() {
        return amount;
    }
}


