/**
 *  Copyright (C) 2011-2013 Typesafe, Inc <http://typesafe.com>
 */
package com.typesafe.akkademo.client;

import java.util.concurrent.TimeUnit;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.typesafe.akkademo.common.Bet;
import com.typesafe.akkademo.common.RetrieveBets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.List;

public class BetClient {

    public static void main(String[] args) {
        System.out.println("*** STARTING TEST OF BETTING APPLICATION");
        BetClient client = new BetClient();
        ActorSystem system = client.init();

        try {
            if (args.length > 0 && args[0].equals("send")) {
                client.sendMessages(system);
            } else {
                client.retrieveMessages(system);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            system.shutdown();
        }
    }

    private ActorSystem init() {
        Config config = ConfigFactory.parseString
                ("akka { \n" +
                    "actor { \n" +
                        "provider = \"akka.remote.RemoteActorRefProvider\" \n" +
                    "} \n"  +
                    "remote { \n" +
                        "netty { \n" +
                            "hostname = \"127.0.0.1\" \n" +
                            "port = 2661 \n" +
                        "} \n" +
                    "} \n" +
                "}");
        return ActorSystem.create("TestActorSystem", config);
    }

    private void sendMessages(ActorSystem system) {
        ActorRef service = getService(system);
        for (int i = 0; i < 200; i++) {
            service.tell(new Bet("ready_player_one", i % 10 + 1, i % 100 + 1));
        }
        System.out.println("*** SENDING OK");
    }

    private void retrieveMessages(ActorSystem system) throws Exception {
        ActorRef service = getService(system);
        Timeout timeout = new Timeout(Duration.create(2, TimeUnit.SECONDS));
        Future<Object> fBets = Patterns.ask(service, new RetrieveBets(), timeout);
        List<Bet> bets = (List<Bet>) Await.result(fBets, timeout.duration());
        assert bets.size() == 200;
        System.out.println("*** TESTING OK");
    }

    private ActorRef getService(ActorSystem system) {
        return system.actorFor("akka://BettingServiceActorSystem@127.0.0.1:2552/user/bettingService");
    }
}
