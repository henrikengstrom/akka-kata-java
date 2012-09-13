/**
 *  Copyright (C) 2011-2012 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.processor.service;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.typesafe.akkademo.common.PlayerBet;
import com.typesafe.akkademo.common.RetrieveBets;

public class BettingProcessor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    /**
     * TASKS :
     * Send remote registration message to service
     * Create worker for dangerous task (using UnstableRepository actor)
     * Supervise worker -> handle errors
     * Send confirmation message back to Betting service
     */

    public void onReceive(Object message) {
        if (message instanceof PlayerBet) {
            // todo
        } else if (message instanceof RetrieveBets) {
            // todo
        } else {
            unhandled(message);
        }
    }
}