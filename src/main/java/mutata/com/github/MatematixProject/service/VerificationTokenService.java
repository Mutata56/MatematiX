package mutata.com.github.MatematixProject.service;



import mutata.com.github.MatematixProject.dao.MyResponse;
import mutata.com.github.MatematixProject.entity.token.Token;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.entity.token.VerificationToken;
import mutata.com.github.MatematixProject.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class VerificationTokenService implements TokenService<VerificationToken> {

    private final VerificationTokenRepository repository;


    @Autowired
    public VerificationTokenService(VerificationTokenRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = false)
    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        repository.save(verificationToken);
    }

    public Token findByToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    @Transactional(readOnly = false)
    public void delete(String token) {
        repository.deleteByToken(token);
    }

    @Transactional(readOnly = false)
    @Override
    public void save(Token token) {
        repository.save((VerificationToken) token);
    }

    @Transactional(readOnly = false)
    public void deleteExpiredSince(LocalDateTime date) {
        repository.deleteByExpirationDateLessThan(date);
    }


    public List<VerificationToken> findAll() {
        return repository.findAll();
    }

    public Page<VerificationToken> findAllReturnPage(Integer currentPage, Integer itemsPerPage) {
        return repository.findAll(PageRequest.of(currentPage,itemsPerPage));
    }

    @Override
    public MyResponse<VerificationToken> find(Integer currentPage, Integer itemsPerPage, String find, String findBy) {
        return null; // TODO
    }

    @Override
    public Page<VerificationToken> findAllSortedBy(Integer currentPage, Integer itemsPerPage, String sortBy,String sortDirection) {
        return null; // TODO
    }

    @Override
    public MyResponse findAndSort(Integer currentPage, Integer itemsPerPage, String find, String findBy, String sortBy,String sortDirection) {
        return null; // TODO
    }
}
