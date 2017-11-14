package demo.akka.messages;

/**
 * @author <a href="mailto:vladyslav@dsi.io">Vladyslav Petrov</a>
 */
@DistributedMessage(action = Action.SEND_TO_ALL_NODES)
public class CleanDataMessage extends AbstractMessage {

    private int memberId;

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
}
