package demo.akka.messages;

/**
 * @author <a href="mailto:vladyslav@dsi.io">Vladyslav Petrov</a>
 */
@DistributedMessage(action = Action.SEND_TO_NODE)
public class DeleteUserMessage extends AbstractMessage {

    public int memberId;

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
}
