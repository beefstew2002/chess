package model;

public record UserData(String username, String password, String email) {
    public boolean equals (UserData otherUser) {
        return username.equals(otherUser.username()) /*I skipped password checking because they're hashed teehee*/ && email.equals(otherUser.email());
    }
}
