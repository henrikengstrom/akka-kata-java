# Akka Kata in Java

This repository contains an Akka kata that can be used whenever you feel like doing some Akka Karate related training.

## Prerequisites

* A computer
* An installed OS
* Java
* Maven
* [Git](http://git-scm.com/downloads) _(not mandatory)_

## Getting Started (Git installed)

So you decided to install Git (or already had it installed). Smart move!
Open a terminal and type:

```
> git clone git://github.com/henrikengstrom/akka-kata-java.git
```

## Getting Started (manually - Git unavailable)

Open a browser and point it to:

https://github.com/henrikengstrom/akka-kata-java/downloads

Select your preferred flavor of compression (zip or tar.gz), download and extract onto your machine.

### Maven

To compile the project with Maven:

```
> mvn compile
```

## Eclipse the project

See [Eclipse Guide](http://maven.apache.org/guides/mini/guide-ide-eclipse.html)

## IntelliJ the project

Open IntelliJ, select `File` -> `Open Project…` and point to the root pom.xml in the project (i.e. the `akka-kata-java/pom.xml` file).

## The Kata ("osu sensei")

The aim with this kata is to show some core elements of Akka:
* Remoting
* Supervision
* Some Akka patterns

To showcase the elements above we have selected to implement a simple betting application - or at least provide a skeleton of such an application.
The implemented application should simulate a transacted system, i.e. it should handle a crash of a JVM.
We will discuss pros and cons of alternative implementations during the meetup.

The application you create will run in two different JVMs (and actor systems). One "node", called _betting service_, receives bet messages from a client,
creates a transaction number and sends this message to the other "node" _betting processor_. The betting service keeps track of messages sent and should also
handle confirmation messages from the betting processor. It also handles re-sending of messages that have not been confirmed. 

The task of the betting processor is to spawn workers that do the dangerous job (in this case interacting with an unstable service), 
supervise these workers and send back confirmation that a task has been performed. 

The _betting service_ should be able to function without any available _betting processor_, i.e. should it receive bet(s) before the _betting processor_ has
registered it should keep these bets locally and send them as soon as a _betting processor_ becomes available.

Sometimes your servers crash(!) and therefore you should design with this in mind. Sending too many bets to the _betting processor_ will cause
it (the JVM) to crash. It is an essential part of this kata to make sure that the _betting service_ can handle such a crash.

We will provide some alternative implementations to show how to solve the different tasks/assignments raised in the code (see comments in provided code). 

## Starting The Parts of the Kata

You should run the service, processor and client in that order to make sure that this kata runs as intended.

Start the service

```
> cd <project_home>/service
> mvn exec:java -Dexec.mainClass="com.typesafe.akkademo.service.BettingServiceApplication"
```

The next step is to start the processor

```
> cd <project_home>/processor
> mvn exec:java -Dexec.mainClass="com.typesafe.akkademo.processor.BettingProcessorApplication"
```

Finally you should run the client. Start off by sending bets to the service

```
> cd <project_home>/client
> mvn exec:java -Dexec.mainClass="com.typesafe.akkademo.client.BetClient" -Dexec.args="send"
```

The final step is to retrieve the bets from the service

```
> cd <project_home>/client
> mvn exec:java -Dexec.mainClass="com.typesafe.akkademo.client.BetClient"
```

That's it!

## Green Belt Akka

For a small collection of akka information useful for this kata see [Green Belt Akka](https://github.com/henrikengstrom/akka-kata-java/blob/master/GREEN_BELT_AKKA.md)

## Solution Proposal

When you feel that you have accomplished all tasks specified in the code there is a solution proposal to be found in a branch called [solution](http://github.com/henrikengstrom/akka-kata-java/tree/solution). We recommend that you try to solve the kata before  looking at this proposal though. 

## Authors

* Henrik Engström : [@h3nk3](http://twitter.com/h3nk3)
* Björn Antonsson : [@bantonsson](http://twitter.com/bantonsson)
