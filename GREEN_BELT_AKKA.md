# Green Belt Akka

This doucment will briefly introduce some important parts of Akka _specific for the implementation of this kata_. 

The official Akka documentation is really good (if we may say so ourselves): [Akka Docs](http://doc.akka.io/docs/akka/2.1.2/)  

_Please note_ that this document describes parts of Akka very briefly and we refer to the original documentation for an in-depth description of Akka.

## Akka Essentials

Below is a brief introduction of some concepts you will need for this kata with some pointers to where you can read more.

### Actor Systems

An actor system is, among other things, the context in which actors operate. You can have multiple actor systems within the same JVM.

See [Actor Systems](http://doc.akka.io/docs/akka/2.1.2/general/actor-systems.html)

**Creating ActorSystems**

```
ActorSystem system = ActorSystem.create("MyActorSystem");
```

If you want to use a specific configuration file the syntax is:

```
Config config = ConfigFactory.parseString("…"); // replace "…" with the real configuration settings

ActorSystem system = ActorSystem.create("MyActorSystem", config);
```

### Working with Actors

See [Actors](http://doc.akka.io/docs/akka/2.1.2/java/actors.html)


**Creating actors**

In the system context, called top level actors (to be used sparsely)

```
ActorRef myActor = system.actorOf(new Props(MyActor.class), "myActorName");
```

In the actor context, called children (i.e. when you're inside an actor)

```
ActorRef myActor = getContext().actorOf(new Props(MyActor.class), "myActorName");
```

**Sending messages**

Fire and forget

```
myActor.tell("A message");

myActor.tell("Another message", getSelf());   // sender reference is passed along

myActor.tell("Yet another msg", getSender()); // original sender reference is passed along
```

As futures (has performance implications)

```
import akka.pattern.Patterns;
import scala.concurrent.duration.Duration;
import akka.util.Timeout;

Timeout timeout = new Timeout(Duration.parse("2 seconds"))

Future<Object> myFuture = Patterns.ask(myActor, "A message", timeout);
```

**Receiving messages**

```
public class MyActor extends UntypedActor {
 
    public void onReceive(Object message) {
        if (message instanceof String) {
 			String msg = (String) message;
 			if (msg.equals("A message")) {
 				System.out.println("Received the message");
 			} else if (msg.equals("Another message")) {
 			    System.out.println("Received that other message");
 			}
        } else {
            unhandled(message);
        }
    }
}  
```
**Replying**

```
public class MyActor extends UntypedActor {
 
    public void onReceive(Object message) {
        if (message instanceof String) {
            String msg = (String) message;
            if (msg.equals("A message")) {
                getSender().tell("Got it")
            }
        } else {
            unhandled(message);
        }
    }
}  
```


**Supervising actors**

See [Fault Tolerance](http://doc.akka.io/docs/akka/2.1.2/java/fault-tolerance.html)

```
private static SupervisorStrategy strategy = new OneForOneStrategy(-1, Duration.Inf(),
    new Function<Throwable, Directive>() {
        @Override
        public Directive apply(Throwable t) {
            if (t instanceof Exception) {
               return restart(); // stop(); escalate();
            }
        }
    });
    
@Override
public SupervisorStrategy supervisorStrategy() {
    return strategy;
}

 
```

### Misc Tasks

**Scheduling messages**

To schedule a message send sometime in the future, once or repeatedly use the scheduler.

See [Scheduler](http://doc.akka.io/docs/akka/2.1.2/java/scheduler.html)

```
import scala.concurrent.duration.Duration;
import static java.util.concurrent.TimeUnit.SECONDS;

Cancallable heartbeat = getContext().system().scheduler().schedule(
    Duration.Zero(),
    Duration.create(2, SECONDS),
    myActor,
    "Every other second message",
    getContext().system().dispatcher());

```
