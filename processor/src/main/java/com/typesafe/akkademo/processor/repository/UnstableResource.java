/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.processor.repository;

import com.typesafe.akkademo.common.Bet;

import java.util.List;

public interface UnstableResource {
    public void save(int idempotentId, String player, int game, int amount);
    public List<Bet> findAll();
}