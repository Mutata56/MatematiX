package mutata.com.github.service;

import mutata.com.github.entity.token.Token;

public interface TokenService {
    Token findByToken(String token);
    void delete(String token);

    void save(Token token);
}
