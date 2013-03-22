/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.processor.repository;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.typesafe.akkademo.common.Bet;
import com.typesafe.akkademo.common.PlayerBet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ReallyUnstableResource implements UnstableResource {
    HashMap<Integer, Bet> bets = new HashMap<Integer, Bet>();
    File store = new File("persistent_store");
    Random randomizer = new Random();
    final Splitter splitter = Splitter.on(":");

    public ReallyUnstableResource() {
        init();
    }

    private void init() {
        try {
            Iterator<String> lines = Files.readLines(store, Charsets.UTF_8).iterator();
            while (lines.hasNext()) {
                PlayerBet playerBet = deserialize(lines.next());
                if (!bets.containsKey(playerBet.getId())) {
                    bets.put(playerBet.getId(), playerBet.getBet());
                }
            }
        } catch (IOException e) {
        }
    }

    @Override
    public void save(int idempotentId, String player, int game, int amount) {
        if (idempotentId % (randomizer.nextInt(10) + 10) == 0) throw new RuntimeException("Hey, I did not count on this happening...");
        if (idempotentId % (randomizer.nextInt(17) + 17) == 0) throw new DatabaseFailureException("Help! The database's gone haywire.");
        if (idempotentId % (randomizer.nextInt(121) + 50) == 0) System.exit(1);

        if (!bets.containsKey(idempotentId)) {
            persist(idempotentId, new Bet(player, game, amount));
        }
    }

    @Override
    public List<Bet> findAll() {
        return new ArrayList<Bet>(bets.values());
    }

    private PlayerBet deserialize(String line) {
        ImmutableList<String> pieces = ImmutableList.copyOf(splitter.split(line));
        return new PlayerBet(Integer.valueOf(pieces.get(0)), new Bet(pieces.get(1), Integer.valueOf(pieces.get(2)), Integer.valueOf(pieces.get(3))));
    }

    private String serialize(int id, Bet bet) {
        return id + ":" + bet.getPlayer() + ":" + bet.getGame() + ":" + bet.getAmount() + "\n";
    }

    private void persist(int id, Bet bet) {
        bets.put(id, bet);
        try {
            Files.append(serialize(id, bet), store, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
