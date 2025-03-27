package dataaccess;

public record DbProperties(String databaseName, String user, String password, String connectionUrl) {
}
