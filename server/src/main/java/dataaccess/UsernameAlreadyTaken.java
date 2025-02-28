package dataaccess;
/**
 * Indicates the username is already taken
 */
public class UsernameAlreadyTaken extends DataAccessException {
    public UsernameAlreadyTaken(String message) {
        super(message);
    }
}
