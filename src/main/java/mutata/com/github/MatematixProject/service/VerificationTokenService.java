package mutata.com.github.MatematixProject.service;



import mutata.com.github.MatematixProject.dao.MyResponse;
import mutata.com.github.MatematixProject.dao.VerificationTokenDAO;
import mutata.com.github.MatematixProject.entity.token.Token;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.entity.token.VerificationToken;
import mutata.com.github.MatematixProject.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Сервис, связанный с классом VerificationToken
 * @see VerificationToken
 * Transactional(readOnly = true) - данный сервис занимается только чтением, и никак не изменяет данные БД
 */

@Service
@Transactional(readOnly = true)
public class VerificationTokenService implements TokenService<VerificationToken> {

    private final VerificationTokenRepository repository;

    private final VerificationTokenDAO verificationTokenDAO;

    @Autowired
    public VerificationTokenService(VerificationTokenRepository repository, VerificationTokenDAO verificationTokenDAO) {
        this.repository = repository;
        this.verificationTokenDAO = verificationTokenDAO;
    }

    /**
     * Создание токена типа VerificationToken
     * @param user - пользователь, владеющий токеном
     * @param token - строковое представление токена
     */

    @Transactional(readOnly = false)
    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        repository.save(verificationToken);
    }

    /**
     * Найти токен по его строковому литералу
     * @param token - строкой литерал токена
     * @return - найденный токен
     */

    public Token findByToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    /**
     * Удалить токен по строковому литералу
     * @param token - строкой литерал токена
     */

    @Transactional(readOnly = false)
    public void delete(String token) {
        repository.deleteByToken(token);
    }

    /**
     * Сохранить данный токен
     * @param token - токен, который нужно сохранить
     */

    @Transactional(readOnly = false)
    @Override
    public void save(Token token) {
        repository.save((VerificationToken) token);
    }

    /**
     * Удаление токена которые считаюстя expired на момент date
     * @param date  - удалить все токены, чьи expiration date идут ДО даты
     */


    @Transactional(readOnly = false)
    public void deleteExpiredSince(LocalDateTime date) {
        repository.deleteByExpirationDateLessThan(date);
    }

    /**
     * Найти все токены
     */


    public List<VerificationToken> findAll() {
        return repository.findAll();
    }

    /**
     * Найти все токены, вернуть currentPage страницу с количеством itemsPerPage сущностями.
     * @return страница с токенами в количестве itemsPerPage на странице по номеру currentPage
     */

    public Page<VerificationToken> findAllReturnPage(Integer currentPage, Integer itemsPerPage) {
        return repository.findAll(PageRequest.of(currentPage,itemsPerPage));
    }

    @Override
    public MyResponse<VerificationToken> find(Integer currentPage, Integer itemsPerPage, String find, String findBy) {
        switch (findBy) {
            case "id":
                return verificationTokenDAO.findWhereIdLike(currentPage,itemsPerPage,find);
            case "token":
                return verificationTokenDAO.findWhereTokenLike(currentPage,itemsPerPage,find);
            case "user":
                return verificationTokenDAO.findWhereUserLike(currentPage,itemsPerPage,find);

        }
        return null;
    }

    @Override
    public Page<VerificationToken> findAllSortedBy(Integer currentPage, Integer itemsPerPage, String sortBy,String sortDirection) {
        return repository.findAll(PageRequest.of(currentPage,itemsPerPage,"asc".equals(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC,sortBy));
    }

    @Override
    public MyResponse findAndSort(Integer currentPage, Integer itemsPerPage, String find, String findBy, String sortBy,String sortDirection) {
        MyResponse<VerificationToken> response = find(currentPage,itemsPerPage,find,findBy);
        switch (sortBy) {
            case "id":
                response.getContent().sort("asc".equals(sortDirection) ? Comparator.comparing(VerificationToken::getId) : Comparator.comparing(VerificationToken::getId).reversed());
            case "token":
                response.getContent().sort("asc".equals(sortDirection) ? Comparator.comparing(VerificationToken::getToken) : Comparator.comparing(VerificationToken::getToken).reversed());
            case "user":
                response.getContent().sort("asc".equals(sortDirection) ? Comparator.comparing(VerificationToken::getUserName) : Comparator.comparing(VerificationToken::getUserName).reversed());
        }
        return response;
    }

    public VerificationToken findById(Long id) {
        return id == null ? null : repository.findById(id).orElse(null);
    }
}
