package demo.akka.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import demo.akka.messages.AbstractMessage;

import org.springframework.context.annotation.Scope;

/**
 * @author <a href="mailto:vladyslav@dsi.io">Vladyslav Petrov</a>
 */
@Scope("singleton")
public class SenderActor extends UntypedActor {

    public static final String USER_TOPIC = "user";

    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    private ActorRef mediator;
    private Cluster cluster = Cluster.get(getContext().system());

    public SenderActor() {
        mediator = DistributedPubSub.get(getContext().system()).mediator();
    }

    @Override
    public void onReceive(Object msg) {
        if (msg instanceof AbstractMessage) {
            LOG.info("Message received by SenderActor. Message: " + msg.toString());

            final AbstractMessage message = (AbstractMessage) msg;

            message.setActorSystem(cluster.selfAddress().toString());

            switch (message.getAction()) {
                case SEND_TO_NODE:
                    LOG.info("Message will be delivered to one of the actors");
                    mediator.tell(new DistributedPubSubMediator
                                    .Send(message.getActorRef(), msg, message.isLocalAffinity()),
                            getSender());
                    break;
                case SEND_TO_ALL_NODES:
                    LOG.info("Message will be delivered to all actors");
                    mediator.tell(new DistributedPubSubMediator
                                    .Publish(USER_TOPIC, msg),
                            getSender());
                    break;
            }
        } else {
            LOG.warning("Message was not delivered");
            unhandled(msg);
        }
    }
}