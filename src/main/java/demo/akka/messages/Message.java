package demo.akka.messages;

/**
 * @author <a href="mailto:vladyslav@dsi.io">Vladyslav Petrov</a>
 */
@DistributedMessage(action = Action.SEND_TO_NODE)
public class Message extends AbstractMessage {

    private String body;

    private int memberId;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
}
