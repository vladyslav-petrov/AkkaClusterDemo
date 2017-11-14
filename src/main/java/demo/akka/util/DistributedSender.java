package demo.akka.util;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import demo.akka.actor.SenderActor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:vladyslav@dsi.io">Vladyslav Petrov</a>
 */
@Service
@Scope("singleton")
public class DistributedSender {

    @Autowired
    private ActorSystem actorSystem;

    public void send(Object message) {
        actorSystem.actorSelection(
                Route.getActorRef(SenderActor.class, ""))
                .tell(message, ActorRef.noSender());
    }
}
