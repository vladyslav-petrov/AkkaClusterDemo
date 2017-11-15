package demo.akka.actor;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.Member;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import demo.akka.messages.CleanDataMessage;
import demo.akka.messages.DeleteSystemUserMessage;
import demo.akka.messages.NewSystemUserMessage;
import demo.akka.messages.NewUserMessage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static akka.cluster.ClusterEvent.MemberRemoved;
import static akka.cluster.ClusterEvent.ReachableMember;
import static akka.cluster.ClusterEvent.UnreachableMember;
import static akka.cluster.ClusterEvent.initialStateAsEvents;
import static demo.akka.actor.SenderActor.USER_TOPIC;

/**
 * @author <a href="mailto:vladyslav@dsi.io">Vladyslav Petrov</a>
 */
public class ApplicationActor extends UntypedActor {

    private final LoggingAdapter LOG = Logging.getLogger(getContext().system(), this);

    private static final Map<Integer, String> USERS = new HashMap<>();

    private static final Set<Integer> SYSTEM_USERS = new HashSet<>();

    private Cluster cluster = Cluster.get(getContext().system());

    public ApplicationActor() {
        ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
        mediator.tell(new DistributedPubSubMediator.Subscribe(USER_TOPIC, getSelf()), getSelf());
    }

    @Override
    public void preStart() {
        cluster.subscribe(getSelf(),
                initialStateAsEvents(),
                MemberEvent.class,
                MemberUp.class,
                ReachableMember.class,
                UnreachableMember.class);
    }

    @Override
    public void postStop() {
        cluster.unsubscribe(getSelf());
    }

    @Override
    public void onReceive(Object msg) throws Throwable {
        if (msg instanceof NewUserMessage) {
            NewUserMessage message = (NewUserMessage) msg;
            USERS.put(message.getMemberId(), message.getSystemId());
        } else if (msg instanceof NewSystemUserMessage) {
            NewSystemUserMessage message = (NewSystemUserMessage) msg;
            SYSTEM_USERS.add(message.getMemberId());
        } else if (msg instanceof DeleteSystemUserMessage) {
            DeleteSystemUserMessage message = (DeleteSystemUserMessage) msg;
            SYSTEM_USERS.remove(message.getMemberId());
        } else if (msg instanceof CleanDataMessage) {
            CleanDataMessage message = (CleanDataMessage) msg;
            USERS.remove(message.getMemberId());
        } else if (msg instanceof MemberUp) {
            MemberUp message = (MemberUp) msg;
            LOG.info("[Member up event] - member: " + message.member().toString());
        } else if (msg instanceof MemberRemoved) {
            Member member = ((MemberRemoved) msg).member();
            LOG.info("[Member removed event] - member: " + member.toString());

            final String systemName = member.address().toString();
            for (Integer user : getSystemUsers(systemName)) {
                USERS.remove(user);
                LOG.info("User [{}] removed from {} actor system", user, systemName);
            }
        } else if (msg instanceof ReachableMember) {
            ReachableMember message = (ReachableMember) msg;
            LOG.info("[Member becomes reachable] Member: " + message.member().toString());
        }
        LOG.info(" =================== [Message]   " + msg.toString());
    }

    public static Map<Integer, String> getUsers() {
        return USERS;
    }

    public static Set<Integer> getSystemUsers() {
        return SYSTEM_USERS;
    }

    public static Set<Integer> getSystemUsers(String actorSystem) {
        final Set<Integer> userSet = new HashSet<>();
        for (Map.Entry<Integer, String> entrySet : USERS.entrySet()) {
            if (entrySet.getValue().equals(actorSystem)) {
                userSet.add(entrySet.getKey());
            }
        }
        return userSet;
    }

    public static String getActorSystem(int userId) {
        return USERS.get(userId);
    }
}
