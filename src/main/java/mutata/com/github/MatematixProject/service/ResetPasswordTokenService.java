package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.dao.MyResponse;
import mutata.com.github.MatematixProject.dao.ResetPasswordDAO;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.entity.token.ResetPasswordToken;
import mutata.com.github.MatematixProject.entity.token.Token;
import mutata.com.github.MatematixProject.repository.ResetPasswordTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * Сервис для управления токенами сброса пароля пользователя.
 * <p>Предоставляет методы создания, поиска,
 * удаления и пагинации {@link ResetPasswordToken}.</p>
 *
 * <p>Аннотация {@link Service} помечает класс как компонент Spring,
 * а {@link Transactional}(readOnly = true) указывает,
 * что по умолчанию методы не изменяют БД.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see ResetPasswordToken
 * @see ResetPasswordTokenRepository
 * @see ResetPasswordDAO
 */
@Service
@Transactional(readOnly = true)
public class ResetPasswordTokenService implements TokenService<ResetPasswordToken> {

    /**
     * Репозиторий для CRUD-операций с токенами сброса пароля.
     */
    private final ResetPasswordTokenRepository repository;

    /**
     * DAO для кастомных запросов фильтрации и поиска токенов.
     */
    private final ResetPasswordDAO resetPasswordDAO;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param repository       репозиторий токенов сброса
     * @param resetPasswordDAO DAO для поиска и фильтрации
     */
    @Autowired
    public ResetPasswordTokenService(ResetPasswordTokenRepository repository,
                                     ResetPasswordDAO resetPasswordDAO) {
        this.repository = repository;
        this.resetPasswordDAO = resetPasswordDAO;
    }

    /**
     * Создаёт и сохраняет новый токен сброса пароля для пользователя.
     *
     * @param user  пользователь, которому будет назначен токен
     * @param token строковое значение токена
     */
    @Transactional(readOnly = false)
    public void createResetPasswordToken(User user, String token) {
        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setToken(token);
        resetPasswordToken.setUser(user);
        repository.save(resetPasswordToken);
    }

    /**
     * Удаляет из БД все токены, срок действия которых истёк до указанного времени.
     *
     * @param date удалять токены с expirationDate <= date
     */
    @Transactional(readOnly = false)
    public void deleteExpiredSince(LocalDateTime date) {
        repository.deleteByExpirationDateLessThan(date);
    }

    /**
     * Ищет токен по его строковому представлению.
     *
     * @param token строка токена
     * @return найденный токен или null, если не найден
     */
    @Override
    public Token findByToken(String token) {
        return repository.findByToken(token).orElse(null);
    }

    /**
     * Удаляет токен из БД по строковому представлению.
     *
     * @param token строка токена для удаления
     */
    @Override
    @Transactional(readOnly = false)
    public void delete(String token) {
        repository.deleteByToken(token);
    }

    /**
     * Сохраняет или обновляет переданный токен в БД.
     *
     * @param token объект токена для сохранения
     */
    @Transactional(readOnly = false)
    @Override
    public void save(Token token) {
        repository.save((ResetPasswordToken) token);
    }

    /**
     * Возвращает список всех токенов сброса пароля.
     *
     * @return список всех токенов
     */
    public List<ResetPasswordToken> findAll() {
        return repository.findAll();
    }

    /**
     * Получает страницу токенов сброса пароля без фильтрации.
     *
     * @param currentPage  номер страницы (0-based)
     * @param itemsPerPage количество токенов на странице
     * @return страница токенов
     */
    @Override
    public Page<ResetPasswordToken> findAllReturnPage(Integer currentPage, Integer itemsPerPage) {
        return repository.findAll(PageRequest.of(currentPage, itemsPerPage));
    }

    /**
     * Поиск токенов по заданному полю и паттерну.
     *
     * @param currentPage  номер страницы (0-based)
     * @param itemsPerPage количество элементов на странице
     * @param find         паттерн поиска
     * @param findBy       имя поля ("id", "token", "user")
     * @return контейнер найденных токенов и общее число записей
     */
    @Override
    public MyResponse<ResetPasswordToken> find(Integer currentPage, Integer itemsPerPage,
                                               String find, String findBy) {
        switch (findBy) {
            case "id":
                return resetPasswordDAO.findWhereIdLike(currentPage, itemsPerPage, find);
            case "token":
                return resetPasswordDAO.findWhereTokenLike(currentPage, itemsPerPage, find);
            case "user":
                return resetPasswordDAO.findWhereUserLike(currentPage, itemsPerPage, find);
            default:
                return new MyResponse<>(List.of(), 0);
        }
    }

    /**
     * Получает страницу токенов с сортировкой по указанному полю.
     *
     * @param currentPage   номер страницы (0-based)
     * @param itemsPerPage  количество элементов на странице
     * @param sortBy        поле для сортировки
     * @param sortDirection направление сортировки ("asc"/"desc")
     * @return страница отсортированных токенов
     */
    @Override
    public Page<ResetPasswordToken> findAllSortedBy(Integer currentPage, Integer itemsPerPage,
                                                    String sortBy, String sortDirection) {
        Sort.Direction dir = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return repository.findAll(PageRequest.of(currentPage, itemsPerPage, dir, sortBy));
    }

    /**
     * Комбинированный поиск и сортировка токенов в памяти.
     *
     * @param currentPage   номер страницы (0-based)
     * @param itemsPerPage  количество элементов на странице
     * @param find          паттерн поиска
     * @param findBy        поле поиска
     * @param sortBy        поле сортировки
     * @param sortDirection направление сортировки ("asc"/"desc")
     * @return контейнер фильтрованных и отсортированных токенов
     */
    @Override
    public MyResponse<ResetPasswordToken> findAndSort(Integer currentPage, Integer itemsPerPage,
                                                      String find, String findBy,
                                                      String sortBy, String sortDirection) {
        MyResponse<ResetPasswordToken> response = find(currentPage, itemsPerPage, find, findBy);
        boolean asc = "asc".equalsIgnoreCase(sortDirection);
        Comparator<ResetPasswordToken> comparator;
        switch (sortBy) {
            case "id":
                comparator = Comparator.comparing(ResetPasswordToken::getId);
                break;
            case "token":
                comparator = Comparator.comparing(ResetPasswordToken::getToken);
                break;
            case "user":
                comparator = Comparator.comparing(ResetPasswordToken::getUserName);
                break;
            default:
                return response;
        }
        response.getContent().sort(asc ? comparator : comparator.reversed());
        return response;
    }

    @Override
    public Long getCount() {
        return repository.count();
    }

    /**
         * Находит токен по его ID.
         *
         * @param id идентификатор токена
         * @return токен или null, если не найден
         */
        public ResetPasswordToken findById(Long id) {
            return id == null ? null : repository.findById(id).orElse(null);
        }
    }