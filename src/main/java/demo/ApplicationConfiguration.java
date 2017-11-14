package demo;

import akka.actor.ActorSystem;
import akka.actor.Props;
import demo.akka.actor.ApplicationActor;
import demo.akka.actor.SenderActor;
import demo.akka.util.Route;
import demo.di.SpringExtension;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
class ApplicationConfiguration {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SpringExtension springExtension;

    @Bean
    public ActorSystem actorSystem() {
        final ActorSystem actorSystem = ActorSystem.create("ClusterSystem", akkaConfiguration());
        actorSystem.actorOf(Props.create(SenderActor.class), Route.getName(SenderActor.class, ""));
        actorSystem.actorOf(Props.create(ApplicationActor.class), Route.getName(ApplicationActor.class, ""));

        springExtension.initialize(applicationContext);
        return actorSystem;
    }

    @Bean
    public Config akkaConfiguration() {
        final String port = System.getProperty("server.akka.port");
        final String host = System.getProperty("server.akka.host");

        LOG.info("Port info: " + port + "\tHost info: " + host);

        final StringBuilder seedNodes = new StringBuilder("akka.cluster.seed-nodes = [");

        seedNodes.append("\"akka.tcp://ClusterSystem@")
                .append(System.getProperty("local.node.host")).append(":")
                .append(System.getProperty("local.node.port")).append("\"").append(",\n");

        seedNodes.append("\"akka.tcp://ClusterSystem@")
                .append(System.getProperty("remote.node.host")).append(":")
                .append(System.getProperty("remote.node.port")).append("\"");

        seedNodes.append("]\n");

        final StringBuilder confProps = new StringBuilder();
        confProps.append("akka.remote.netty.tcp.port=")
                .append(port).append("\n")
                .append("akka.remote.netty.tcp.host=")
                .append(host).append("\n")
                .append(seedNodes);

        return ConfigFactory.parseString(confProps.toString()).withFallback(ConfigFactory.load());
    }
}