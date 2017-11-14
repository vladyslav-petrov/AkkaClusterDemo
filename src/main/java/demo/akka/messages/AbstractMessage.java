package demo.akka.messages;

import org.reflections.Reflections;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:vladyslav@dsi.io">Vladyslav Petrov</a>
 */
public abstract class AbstractMessage implements Serializable {

    private static final long serialVersionUID = -7669987291643738192L;

    private static final Map<Class<? extends AbstractMessage>, Action> ACTIONS;

    private String actorRef;

    private String actorSystem;

    private boolean localAffinity = true;

    static {
        ACTIONS = new HashMap<>();
        Reflections reflections = new Reflections("demo.akka.messages");
        Set<Class<? extends AbstractMessage>> subTypes = reflections.getSubTypesOf(AbstractMessage.class);

        for (Class<? extends AbstractMessage> cls : subTypes) {
            Deprecated deprecated = cls.getAnnotation(Deprecated.class);
            if (deprecated == null) {
                DistributedMessage message = cls.getAnnotation(DistributedMessage.class);
                if (message != null) {
                    Action action = message.action();
                    ACTIONS.put(cls, action);
                }
            }
        }
    }

    public String getActorRef() {
        return actorRef;
    }

    public void setActorRef(String actorRef) {
        this.actorRef = actorRef;
    }

    public Action getAction() {
        return ACTIONS.get(this.getClass());
    }

    public String getActorSystem() {
        return actorSystem;
    }

    public void setActorSystem(String actorSystem) {
        this.actorSystem = actorSystem;
    }

    public boolean isLocalAffinity() {
        return localAffinity;
    }

    public void setLocalAffinity(boolean localAffinity) {
        this.localAffinity = localAffinity;
    }

    @Override
    public String toString() {
        return "AbstractMessage{" +
                "actorRef='" + actorRef + '\'' +
                ", actorSystem='" + actorSystem + '\'' +
                ", action='" + getAction() + '\'' +
                ", localAffinity=" + localAffinity +
                '}';
    }
}
