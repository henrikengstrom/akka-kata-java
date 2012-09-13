/**
 *  Copyright (C) 2011-2012 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.processor.repository;

import com.typesafe.akkademo.common.Bet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ReallyUnstableResource implements UnstableResource {
    HashMap<Integer, Bet> bets = new HashMap<Integer, Bet>();
    File store = new File("persistent_store");
    Random randomizer = new Random();

    public ReallyUnstableResource() {
        init();
    }

    private void init() {
        // TODO (HE): LOAD FILE CONTENT
    }

    @Override
    public void save(int idempotentId, String player, int game, int amount) {
        if (idempotentId % (randomizer.nextInt(10) + 10) == 0) throw new RuntimeException("Hey, I did not count on this happening...");
        if (idempotentId % (randomizer.nextInt(17) + 17) == 0) throw new DatabaseFailureException("Help! The database's gone haywire.");
        if (idempotentId % (randomizer.nextInt(121) + 50) == 0) System.exit(1);

        if (!bets.containsKey(idempotentId)) {
            bets.put(idempotentId, new Bet(player, game, amount));
            // TODO (HE): ADD TO FILE
        }
    }

    @Override
    public List<Bet> findAll() {
        return new ArrayList<Bet>(bets.values());
    }
}
