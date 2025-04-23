package mutata.com.github.MatematixProject.entity.token;


import mutata.com.github.MatematixProject.entity.User;

import java.time.LocalDateTime;

/**
 * Интерфейс, описывающий обязательные возможности Token'а, чтобы считаться таковым
 */
public interface Token {
    User getUser();
    String getUserName();
    void setUser(User user);
    LocalDateTime getExpirationDate();

    void setToken(String token);
    void setExpirationDate(LocalDateTime time);
    String getToken();


}
