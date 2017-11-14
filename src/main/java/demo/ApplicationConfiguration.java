package demo;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.Cluster;
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
        final Config config = ConfigFactory.load();
        final String port = System.getProperty("server.akka.port");
        LOG.info("Port info: " + port);
        return ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
                .withFallback(config);
    }
}