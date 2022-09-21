package mutata.com.github.service;

import mutata.com.github.entity.token.ResetPasswordToken;
import mutata.com.github.entity.token.Token;
import mutata.com.github.entity.User;
import mutata.com.github.repository.ResetPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
@Service
@Transactional(readOnly = true)
public class ResetPasswordTokenService implements TokenService {

    private final ResetPasswordTokenRepository repository;

    @Autowired
    public ResetPasswordTokenService(ResetPasswordTokenRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = false)
    public void createResetPasswordToken(User user, String token) {
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setToken(token);
        resetPasswordToken.setUser(user);
        repository.save(resetPasswordToken);
    }
    @Transactional(readOnly = false)
    public void deleteExpiredSince(LocalDateTime date) {
        repository.deleteByExpirationDateLessThan(date);
    }

    @Override
    public Token findByToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    @Override
    @Transactional(readOnly = false)
    public void delete(String token) {
        repository.deleteByToken(token);
    }
    @Transactional(readOnly = false)
    @Override
    public void save(Token token) {
        repository.save((ResetPasswordToken) token);
    }
}
