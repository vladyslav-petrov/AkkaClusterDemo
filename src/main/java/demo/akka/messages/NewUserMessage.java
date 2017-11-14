package demo.akka.messages;


/**
 * @author <a href="mailto:vladyslav@dsi.io">Vladyslav Petrov</a>
 */
@DistributedMessage(action = Action.SEND_TO_ALL_NODES)
public class NewUserMessage extends AbstractMessage {

    private int memberId;

    private String systemId;

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}