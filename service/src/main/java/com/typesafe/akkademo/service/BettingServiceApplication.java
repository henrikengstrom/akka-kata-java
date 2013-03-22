/**
 *  Copyright (C) 2011-2013 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.service;

import akka.actor.Props;
import akka.actor.ActorSystem;
import com.typesafe.config.ConfigFactory;

public class BettingServiceApplication {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("BettingServiceActorSystem", ConfigFactory.load());
        system.actorOf(new Props(BettingService.class), "bettingService");
    }
}