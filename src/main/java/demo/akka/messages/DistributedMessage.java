package demo.akka.messages;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author <a href="mailto:vladyslav@dsi.io">Vladyslav Petrov</a>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedMessage {
    Action action();
}