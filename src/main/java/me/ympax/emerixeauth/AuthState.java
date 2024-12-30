package me.ympax.emerixeauth;

public enum AuthState {
    Logged(3),
    LoggingIn(2),
    Registering(1);

    private final Integer id;

    AuthState(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }
}
