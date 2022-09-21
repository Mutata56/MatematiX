package mutata.com.github.entity.token;


import mutata.com.github.entity.User;

import java.time.LocalDateTime;

public interface Token {
    User getUser();

    void setUser(User user);
    LocalDateTime getExpirationDate();

    void setToken(String token);
}
