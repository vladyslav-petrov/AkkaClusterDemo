package demo.akka.util;

import java.util.Set;

/**
 * @author <a href="mailto:vladyslav@dsi.io">Vladyslav Petrov</a>
 */
public class SystemData {

    public enum State {
        ASSOCIATED, DISASSOCIATED, REMOVED
    }

    private Set<Integer> users;

    private State state;

    public Set<Integer> getUsers() {
        return users;
    }

    public void setUsers(Set<Integer> users) {
        this.users = users;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isReached() {
        return (state == State.ASSOCIATED);
    }
}
