package demo.akka.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import demo.akka.messages.DeleteSystemUserMessage;
import demo.akka.messages.Message;

/**
 * @author <a href="mailto:vladyslav@dsi.io">Vladyslav Petrov</a>
 */
public class UserActor extends UntypedActor {

    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    public UserActor() {
        ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
        mediator.tell(new DistributedPubSubMediator.Put(getSelf()), getSelf());
    }

    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof Message) {
            final Message m = (Message) msg;
            LOG.info(String.format(
                    "\n================================GOT================================\n" +
                            "\tMESSAGE:       %s\n" +
                            "\tRECEIVER:      %s\n",
                    m.getBody(),
                    getSelf().path()));

        } else if (msg instanceof DeleteSystemUserMessage) {
            LOG.info("User was successfully removed from actor system");
            getContext().system().stop(self());
        } else {
            unhandled(msg);
        }
    }
}
