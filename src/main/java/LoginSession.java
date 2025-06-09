public class LoginSession {
    private String username;
    private String passwordHash;

    public LoginSession(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public boolean authenticate(String pw) {
        // compare stored hash to freshly hashed input
        return passwordHash.equals(Integer.toString(pw.hashCode()));
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
