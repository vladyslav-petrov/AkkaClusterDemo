package demo.controller;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
import demo.akka.actor.ApplicationActor;
import demo.akka.actor.UserActor;
import demo.akka.messages.CleanDataMessage;
import demo.akka.messages.DeleteUserMessage;
import demo.akka.messages.Message;
import demo.akka.messages.NewUserMessage;
import demo.akka.util.DistributedSender;
import demo.akka.util.Route;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MainController {

    private static final Logger LOG = Logger.getLogger(MainController.class);

    @Autowired
    private ActorSystem actorSystem;

    @Autowired
    private DistributedSender sender;

    @RequestMapping("/user/add/{memberId}")
    public String addUser(@PathVariable int memberId) {

        final String actorSystemName = Cluster.get(actorSystem).selfAddress().toString();

        Cluster.get(actorSystem).selfAddress();

        if (ApplicationActor.getActorSystem(memberId) == null) {
            final String path = Route.getName(UserActor.class, String.valueOf(memberId));
            actorSystem.actorOf(Props.create(UserActor.class), path);

            NewUserMessage message = new NewUserMessage();
            message.setMemberId(memberId);
            message.setSystemId(actorSystemName);
            sender.send(message);

            return String.format("Member id=%s was successfully added to actor system",
                    memberId);
        } else {
            return String.format("Member id=%s couldn't be added to ActorSystem = %s",
                    memberId,
                    actorSystem.name());
        }
    }

    @RequestMapping("/user/send/{memberId}/{message}")
    public String sendMessage(@PathVariable String memberId, @PathVariable String message) {
        try {
            final int id = Integer.parseInt(memberId);

            final Message msg = new Message();
            msg.setActorRef(Route.getActorRef(UserActor.class, memberId));
            msg.setMemberId(id);
            msg.setBody(message);

            LOG.info("Message sent: " + msg.toString());
            sender.send(msg);

            return "Message sent";
        } catch (NumberFormatException e) {
            return e.getLocalizedMessage();
        }
    }

    @RequestMapping("user/delete/{memberId}")
    public String removeUser(@PathVariable String memberId) {
        try {
            final int id = Integer.parseInt(memberId);

            final DeleteUserMessage msg = new DeleteUserMessage();
            msg.setActorRef(Route.getActorRef(UserActor.class, memberId));
            msg.setMemberId(id);
            sender.send(msg);

            final CleanDataMessage cleanDataMessage = new CleanDataMessage();
            cleanDataMessage.setMemberId(id);
            sender.send(cleanDataMessage);

            return "User removed";
        } catch (NumberFormatException e) {
            return e.getLocalizedMessage();
        }
    }

    @RequestMapping("user/list")
    public String getUserList() {
        final StringBuilder builder = new StringBuilder("<table>");
        builder.append("<tr>")
                .append("<th>").append("MemberId").append("</th>")
                .append("<th>").append("Actor system").append("</th>")
                .append("</tr>");

        for (Map.Entry<Integer, String> entry : ApplicationActor.getUsers().entrySet()) {
            builder.append("<tr>")
                    .append("<td>").append(entry.getKey()).append("</td>")
                    .append("<td>").append(entry.getValue()).append("</td>")
                    .append("</tr>");
        }
        builder.append("</table>");
        return builder.toString();
    }
}
