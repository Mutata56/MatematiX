package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.entity.token.Token;

public interface TokenService<T extends Token> extends MyService<T> {
    Token findByToken(String token);
    void delete(String token);

    void save(Token token);
}
