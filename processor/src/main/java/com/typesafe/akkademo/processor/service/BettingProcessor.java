/**
 *  Copyright (C) 2011-2012 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.processor.service;

import static akka.actor.SupervisorStrategy.*;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Function;
import akka.util.Duration;
import akka.util.FiniteDuration;
import com.typesafe.akkademo.common.PlayerBet;
import com.typesafe.akkademo.common.RegisterProcessor;
import com.typesafe.akkademo.common.RetrieveBets;
import com.typesafe.akkademo.processor.repository.DatabaseFailureException;

import java.util.concurrent.TimeUnit;

public class BettingProcessor extends UntypedActor {
    private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private Cancellable heartbeat;
    private ActorRef service;
    private ActorRef worker;

    public BettingProcessor() {
        service = context().actorFor(context().system().settings().config().getString("betting-service-actor"));
        worker = context().actorOf(new Props(ProcessorWorker.class), "theWorker");
        heartbeat = context().system().scheduler().schedule(
                Duration.Zero(),
                new FiniteDuration(2, TimeUnit.SECONDS),
                getSelf(),
                new RegisterProcessor());

    }

    @Override
    public void postStop() {
        heartbeat.cancel();
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    /**
     * Sets up the supervisor strategy to be applied to all child actors of this actor.
     */
    private static SupervisorStrategy strategy = new OneForOneStrategy(-1, Duration.Inf(),
            new Function<Throwable, Directive>() {
                @Override
                public Directive apply(Throwable t) {
                    if (t instanceof DatabaseFailureException) {
                        return restart();
                    } else if (t instanceof RuntimeException) {
                        return restart();
                    } else {
                        return escalate();
                    }
                }
            });

    public void onReceive(Object message) {
        if (message instanceof PlayerBet) {
            PlayerBet playerBet = (PlayerBet) message;
            worker.tell(playerBet, getSender());
        } else if (message instanceof RetrieveBets) {
            worker.tell((RetrieveBets) message, getSender());
        } else if (message instanceof RegisterProcessor) {
            service.tell((RegisterProcessor) message, getSelf());
        } else {
            unhandled(message);
        }
    }
}