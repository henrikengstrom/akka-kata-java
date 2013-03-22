/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.processor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.akkademo.common.RegisterProcessor;
import com.typesafe.akkademo.processor.service.BettingProcessor;
import com.typesafe.config.ConfigFactory;

public class BettingProcessorApplication {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("BettingProcessorActorSystem", ConfigFactory.load());
        ActorRef bettingProcessor = system.actorOf(new Props(BettingProcessor.class), "bettingProcessor");
    }
}