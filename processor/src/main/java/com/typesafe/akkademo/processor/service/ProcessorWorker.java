/**
 * Copyright (C) 2011-2012 Typesafe <http://typesafe.com/>
 */
package com.typesafe.akkademo.processor.service;

import akka.actor.UntypedActor;
import com.typesafe.akkademo.common.ConfirmationMessage;
import com.typesafe.akkademo.common.PlayerBet;
import com.typesafe.akkademo.common.RetrieveBets;
import com.typesafe.akkademo.processor.repository.ReallyUnstableResource;
import com.typesafe.akkademo.processor.repository.UnstableResource;

public class ProcessorWorker extends UntypedActor {
    private UnstableResource resource;

    public ProcessorWorker() {
        resource = new ReallyUnstableResource();
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof PlayerBet) {
            PlayerBet playerBet = (PlayerBet) message;
            resource.save(playerBet.getId(), playerBet.getBet().getPlayer(), playerBet.getBet().getGame(), playerBet.getBet().getAmount());
            getSender().tell(new ConfirmationMessage(playerBet.getId()));
        } else if (message instanceof RetrieveBets) {
            getSender().tell(resource.findAll());
        } else {
            unhandled(message);
        }
    }
}
