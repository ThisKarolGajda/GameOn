package com.gameon.api.server.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class User {
    private UserId id;
    private boolean logged;
    private UserPrivilegeType privilege;

    public User(@JsonProperty("id") UserId id,
                @JsonProperty("logged") boolean logged,
                @JsonProperty("privilege") UserPrivilegeType privilege) {
        this.id = id;
        this.logged = logged;
        this.privilege = privilege;
    }

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static User fromUserId(UserId userId) {
        return new User(userId, false, UserPrivilegeType.AUTHENTICATION);
    }

    public UserId getId() {
        return id;
    }

    public boolean isLogged() {
        return logged;
    }

    public UserPrivilegeType getPrivilege() {
        return privilege;
    }

    public void setId(UserId id) {
        this.id = id;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public void setPrivilege(UserPrivilegeType privilege) {
        this.privilege = privilege;
    }
}
