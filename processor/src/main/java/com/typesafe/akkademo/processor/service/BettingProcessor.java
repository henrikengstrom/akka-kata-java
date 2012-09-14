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
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    Cancellable heartbeat;
    ActorRef service;
    ActorRef worker;

    public BettingProcessor() {
        service = context().actorFor(context().system().settings().config().getString("betting-service-actor"));
        worker = context().actorOf(new Props(ProcessorWorker.class), "theWorker");
        heartbeat = context().system().scheduler().schedule(Duration.Zero(), new FiniteDuration(1, TimeUnit.SECONDS), service, new RegisterProcessor());
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    @Override
    public void postStop() {
        heartbeat.cancel();
    }

    public void onReceive(Object message) {
        if (message instanceof PlayerBet) {
            worker.forward((PlayerBet) message, context());
        } else if (message instanceof RetrieveBets) {
            worker.forward((RetrieveBets) message, context());
        } else if (message instanceof RegisterProcessor) {
            service.tell((RegisterProcessor) message);
        } else {
            unhandled(message);
        }
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
}