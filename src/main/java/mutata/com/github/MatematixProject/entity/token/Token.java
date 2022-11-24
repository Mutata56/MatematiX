package mutata.com.github.MatematixProject.entity.token;


import mutata.com.github.MatematixProject.entity.User;

import java.time.LocalDateTime;

public interface Token {
    User getUser();

    void setUser(User user);
    LocalDateTime getExpirationDate();

    void setToken(String token);
}
