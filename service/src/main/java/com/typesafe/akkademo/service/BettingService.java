/**
 *  Copyright (C) 2011-2012 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.service;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.Duration;
import com.typesafe.akkademo.common.*;

import java.util.HashMap;
import java.util.Map;
import static java.util.concurrent.TimeUnit.SECONDS;

public class BettingService extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private int sequence = 1;
    private ActorRef processor;
    private long lastUpdate = 0L;
    private static final long ACTIVE_PERIOD = 5000L;
    private static final String HANDLE_UNPROCESSED_BETS = "unhandledBets";
    private Cancellable scheduler;
    // Note: To make this solution (even) more bullet proof you would have to persist the incoming bets.
    private Map<Integer, Bet> bets = new HashMap<Integer, Bet>();

    @Override
    public void preStart() {
        scheduler = getContext().system().scheduler().schedule(
                Duration.create(5, SECONDS),
                Duration.create(3, SECONDS),
                getSelf(),
                HANDLE_UNPROCESSED_BETS);
    }

    @Override
    public void postStop() {
        scheduler.cancel();
    }

    public void onReceive(Object message) {
        if (message instanceof Bet) {
            PlayerBet playerBet = processBet((Bet) message);
            ActorRef p = getActiveProcessor();
            if (p != null) {
                p.tell(playerBet, getSelf());
            }
        } else if (message instanceof ConfirmationMessage) {
            handleProcessedBet(((ConfirmationMessage) message).getId());
        } else if (message instanceof RetrieveBets) {
            ActorRef p = getActiveProcessor();
            if (p != null) p.tell((RetrieveBets) message, getSender());
        } else if (message instanceof String) {
            if (((String) message).equals(HANDLE_UNPROCESSED_BETS)) {
                handleUnprocessedBets();
            }
        } else if (message instanceof RegisterProcessor) {
            registerProcessor(getSender());
        } else {
            unhandled(message);
        }
        // In the upcoming clustering we will be able to listen to remote clients and their status.
        // With this it will be possible to prevent sending messages to a client that is no longer available.
        // e.g. case RemoteClientDead (or similar) => processor = None
        // In this solution we use heartbeats instead.
    }

    private PlayerBet processBet(Bet bet) {
        sequence += 1;
        int id = sequence;
        bets.put(id, bet);
        return new PlayerBet(id, bet);
    }

    private void handleProcessedBet(int id) {
        log.info("processed bet: " + id);
        bets.remove(id);
    }

    private void registerProcessor(ActorRef processor) {
        this.processor = processor;
        lastUpdate = System.currentTimeMillis();
    }

    private ActorRef getActiveProcessor() {
        if ((System.currentTimeMillis() - lastUpdate) < ACTIVE_PERIOD)  {
           return processor;
        }

        return null;
    }

    private void handleUnprocessedBets() {
        // In a real world solution you should probably timestamp each message sent so that you do not
        // resend just sent messages -> takes some pressure off the processor.

        // Since this is just a demo we'll just treat all messages in the map as unhandled and resend them all.
        // Please make sure you understand that I can do this since the processor repository is idempotent!

        // To not flood the processor actor system you might want to use throttling. A good blog post about this van be found here:
        // http://letitcrash.com/post/28901663062/throttling-messages-in-akka-2

        log.info("handling unprocessed bets (size): " + bets.size());
        ActorRef p = getActiveProcessor();
        if (p != null) {
            for (Integer key : bets.keySet()) {
                p.tell(new PlayerBet(key, bets.get(key)), getSelf());
            }
        }
    }
}
