/**
 *  Copyright (C) 2011-2012 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.service;

import akka.actor.Actor;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.akkademo.common.Bet;
import com.typesafe.akkademo.common.RetrieveBets;

public class BettingService extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * TASKS:
     * Create unique sequence/transaction number
     * Create PlayerBet and call betting processor (remotely)
     * Retrieve all bets from betting processor (remotely)
     * Handle timed out transactions (scheduler)
     * Handle registration message from betting processor
     * Handle crash of/unavailable betting processor
     * Keep any message locally until there is a processor service available
     */

    public void onReceive(Object message) {
        if (message instanceof Bet) {
            // todo
        } else if (message instanceof RetrieveBets) {
            // todo
        } else {
            unhandled(message);
        }
    }
}
