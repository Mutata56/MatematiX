package mutata.com.github.MatematixProject.service;
import mutata.com.github.MatematixProject.dao.MyResponse;
import mutata.com.github.MatematixProject.dao.UserDAO;
import mutata.com.github.MatematixProject.entity.AvatarInfo;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.repository.UserRepository;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;

/**
 * Сервисный класс для работы с сущностью {@link User}.
 * <p>Обеспечивает операции создания, чтения, обновления и удаления пользователей в системе,
 * а также дополнительные методы для управления паролем, друзьями, активацией и пагинацией.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 * @see User
 * @see UserDAO
 */
@Service
@Transactional(readOnly = true)
public class UserService implements MyService<User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager manager;
    private final UserDAO userDAO;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param repository       репозиторий пользователей
     * @param userDAO          DAO для дополнительных операций поиска
     * @param encoder          кодировщик паролей
     * @param manager          менеджер сущностей для пользовательских запросов
     */
    @Autowired
    public UserService(UserRepository repository, UserDAO userDAO, PasswordEncoder encoder, EntityManager manager) {
        this.userRepository = repository;
        this.passwordEncoder = encoder;
        this.manager = manager;
        this.userDAO = userDAO;
    }

    /**
     * Сохраняет нового пользователя, шифруя его пароль перед сохранением.
     *
     * @param user объект пользователя для сохранения
     */
    @Transactional(readOnly = false)
    public void save(User user) {
        String pass = passwordEncoder.encode(user.getEncryptedPassword());
        user.setEncryptedPassword(pass);
        userRepository.save(user);
    }

    /**
     * Сохраняет изменения в объекте пользователя без повторного шифрования пароля.
     *
     * @param user объект пользователя для обновления
     */
    @Transactional(readOnly = false)
    public void saveWithoutPasswordEncryption(User user) {
        userRepository.save(user);
    }

    /**
     * Ищет пользователя по email (без учета регистра).
     *
     * @param email электронная почта для поиска
     * @return найденный пользователь или {@code null}
     */
    public User findByEmailIgnoreCase(String email) {
        return userRepository.findUserByEmailIgnoreCase(email).orElse(null);
    }

    public Long getCount(String email) {
        return userRepository.count();
    }

    /**
     * Ищет пользователя по имени (без учета регистра).
     *
     * @param name логин пользователя для поиска
     * @return найденный пользователь или {@code null}
     */
    public User findByNameIgnoreCase(String name) {
        return userRepository.findUserByNameIgnoreCase(name).orElse(null);
    }

    /**
     * Возвращает список всех зарегистрированных пользователей.
     *
     * @return список всех пользователей
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Возвращает страницу пользователей с указанным размером.
     *
     * @param currentPage   номер страницы (0-based)
     * @param itemsPerPage  число элементов на странице
     * @return страница пользователей
     */
    @Override
    public Page<User> findAllReturnPage(Integer currentPage, Integer itemsPerPage) {
        return userRepository.findAll(PageRequest.of(currentPage, itemsPerPage));
    }

    /**
     * Ищет и возвращает {@link MyResponse} с пользователями по указанным критериям.
     * <p>Метод делегирует поисковые запросы DAO в зависимости от поля {@code findBy}:</p>
     * <ul>
     *     <li>"name" — поиск по имени;</li>
     *     <li>"role" — поиск по роли;</li>
     *     <li>"blocked" — поиск по флагу блокировки;</li>
     *     <li>"email" — поиск по email;</li>
     *     <li>"activated" — поиск по флагу активации;</li>
     *     <li>"friends" — поиск всех друзей пользователя;</li>
     *     <li>"friendName" — поиск друзей по имени.</li>
     * </ul>
     *
     * @param currentPage  номер страницы (0-based)
     * @param itemsPerPage число элементов на странице
     * @param find         паттерн для поиска (значение или комбинация для друзей)
     * @param findBy       поле, по которому искать: "name", "role", "blocked", "email", "activated", "friends" или "friendName"
     * @return контейнер {@link MyResponse} с результатом поиска и общим числом совпадений
     */
    @Override
    public MyResponse<User> find(Integer currentPage, Integer itemsPerPage, String find, String findBy) {
        switch (findBy) {
            case "name":
                return userDAO.findWhereNameLike(currentPage, itemsPerPage, find);
            case "role":
                return userDAO.findWhereRoleLike(currentPage, itemsPerPage, find);
            case "blocked":
                return userDAO.findWhereBlockedLike(currentPage, itemsPerPage, find);
            case "email":
                return userDAO.findWhereEmailLike(currentPage, itemsPerPage, find);
            case "activated":
                return userDAO.findWhereEnabledLike(currentPage, itemsPerPage, find);
            case "friends":
                return findAllFriendsReturnPage(findByNameIgnoreCase(find.split(" ")[0]), currentPage, itemsPerPage);
            case "friendName":
                return userDAO.findFriendsWhereNameLike(find, currentPage, itemsPerPage);
            default:
                return null;
        }
    }

    /**
     * Возвращает страницу пользователей, отсортированную по заданному полю.
     * <p>Использует Spring Data JPA для формирования {@link Page} с учётом пагинации и направленного сортирования.</p>
     *
     * @param currentPage   индекс запрашиваемой страницы (0-based)
     * @param itemsPerPage  количество элементов, отображаемых на странице
     * @param sortBy        имя поля сущности {@link User}, по которому выполняется сортировка
     * @param sortDirection направление сортировки: "asc" для по возрастанию или "desc" для по убыванию
     * @return {@link Page}&lt;User&gt; — часть результатов с учётом пагинации и сортировки
     */
    @Override
    public Page<User> findAllSortedBy(Integer currentPage, Integer itemsPerPage, String sortBy, String sortDirection) {
        return userRepository.findAll(
                PageRequest.of(currentPage, itemsPerPage,
                        "asc".equals(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC,
                        sortBy)
        );
    }

    /**
     * Ищет пользователей по критериям с пост-фильтрацией результатов и применяет к ним сортировку на уровне контента.
     * <p>Сначала выполняется поиск через {@link #find(Integer, Integer, String, String)},
     * далее результаты сортируются по полю {@code sortBy} и направлению {@code sortDirection}.</p>
     *
     * @param currentPage   номер страницы (0-based)
     * @param itemsPerPage  число элементов на странице
     * @param find          паттерн для поиска (значение для поиска в соответствии с findBy)
     * @param findBy        поле, по которому искать (name, role, blocked, email, activated, friends, friendName)
     * @param sortBy        поле для сортировки результатов (name, blocked, activated, email, role, rating)
     * @param sortDirection направление сортировки: "asc" или "desc"
     * @return {@link MyResponse}&lt;User&gt; — контейнер с отсортированным списком пользователей и общим числом совпадений
     */
    @Override
    public MyResponse<User> findAndSort(Integer currentPage, Integer itemsPerPage, String find, String findBy, String sortBy, String sortDirection) {
        MyResponse<User> response = find(currentPage, itemsPerPage, find, findBy);
        switch (sortBy) {
            case "name":
                response.getContent().sort(
                        "asc".equals(sortDirection)
                                ? Comparator.comparing(User::getName)
                                : Comparator.comparing(User::getName).reversed()
                );
                break;
            case "blocked":
                response.getContent().sort(
                        "asc".equals(sortDirection)
                                ? Comparator.comparing(User::getBlocked)
                                : Comparator.comparing(User::getBlocked).reversed()
                );
                break;
            case "activated":
                response.getContent().sort(
                        "asc".equals(sortDirection)
                                ? Comparator.comparing(User::getEnabled)
                                : Comparator.comparing(User::getEnabled).reversed()
                );
                break;
            case "email":
                response.getContent().sort(
                        "asc".equals(sortDirection)
                                ? Comparator.comparing(User::getEmail)
                                : Comparator.comparing(User::getEmail).reversed()
                );
                break;
            case "role":
                response.getContent().sort(
                        "asc".equals(sortDirection)
                                ? Comparator.comparing(User::getRole)
                                : Comparator.comparing(User::getRole).reversed()
                );
                break;
            case "rating":
                response.getContent().sort(
                        "asc".equals(sortDirection)
                                ? Comparator.comparing(User::getRating)
                                : Comparator.comparing(User::getRating).reversed()
                );
                break;
        }
        return response;
    }

    /**
     * Удаляет указанного пользователя из базы.
     *
     * @param user объект пользователя для удаления
     */
    @Transactional(readOnly = false)
    public void delete(User user) {
        userRepository.delete(user);
    }

    /**
     * Ищет пользователя по логину и загружает список его закладок.
     *
     * @param userToFind логин пользователя
     * @return объект пользователя с загруженными {@link AvatarInfo}
     */
    public User findByNameIgnoreCaseAndLoadArticles(String userToFind) {
        User user = userRepository.findUserByNameIgnoreCase(userToFind).orElse(null);
        Hibernate.initialize(user.getArticles());
        return user;
    }

    /**
     * Выполняет настраиваемый запрос получая всех пользователей и их токены (reset и verification).<br>
     * Применяется для оптимизированных операций, не требующих стандартных методов репозитория.
     *
     * @return список всех пользователей с предзагруженными токенами
     */
    public List<User> customFindAll() {
        Session session = manager.unwrap(Session.class);
        Query<User> query = session.createQuery(
                "SELECT user FROM User user LEFT JOIN FETCH user.resetPasswordToken " +
                        "LEFT JOIN FETCH user.verificationToken", User.class
        );
        return query.getResultList();
    }

    /**
     * Блокирует пользователя, устанавливая флаг blocked.
     *
     * @param username логин пользователя для блокировки
     */
    @Transactional(readOnly = false)
    public void block(String username) {
        User user = userRepository.findUserByNameIgnoreCase(username).get();
        user.setBlocked((byte) 1);
        saveWithoutPasswordEncryption(user);
    }

    /**
     * Разблокирует пользователя, снимая флаг blocked.
     *
     * @param username логин пользователя для разблокировки
     */
    @Transactional(readOnly = false)
    public void unblock(String username) {
        User user = userRepository.findUserByNameIgnoreCase(username).get();
        user.setBlocked((byte) 0);
        saveWithoutPasswordEncryption(user);
    }

    /**
     * Активирует пользователя, устанавливая флаг enabled.
     *
     * @param username логин пользователя для активации
     */
    @Transactional(readOnly = false)
    public void activate(String username) {
        User user = userRepository.findUserByNameIgnoreCase(username).get();
        user.setEnabled((byte) 1);
        saveWithoutPasswordEncryption(user);
    }

    /**
     * Деактивирует пользователя, снимая флаг enabled.
     *
     * @param username логин пользователя для деактивации
     */
    @Transactional(readOnly = false)
    public void deactivate(String username) {
        User user = userRepository.findUserByNameIgnoreCase(username).get();
        user.setEnabled((byte) 0);
        saveWithoutPasswordEncryption(user);
    }

    /**
     * Возвращает общее число зарегистрированных пользователей.
     *
     * @return количество записей в таблице users
     */
    public Long getCount() {
        return userRepository.count();
    }

    /**
     * Ищет друзей пользователя и возвращает {@link MyResponse} с пагинацией.
     *
     * @param user          объект пользователя
     * @param page          номер страницы (0-based)
     * @param itemsPerPage  число элементов на странице
     * @return контейнер с друзьями и общим количеством
     */
    public MyResponse<User> findAllFriendsReturnPage(User user, int page, int itemsPerPage) {
        return userDAO.findAllFriendsReturnPage(user, PageRequest.of(page, itemsPerPage));
    }

    /**
     * Добавляет одного пользователя в друзья другого.
     * <p>Находит в репозитории двух пользователей по их логинам:
     * пользователя для добавления в друзья и текущего авторизованного пользователя.
     * Затем добавляет текущего пользователя в коллекцию друзей другого и сохраняет изменения
     * без повторного шифрования пароля.</p>
     *
     * @param name        логин пользователя, которого добавляют в друзья
     * @param currentName логин текущего (авторизованного) пользователя
     */
    @Transactional(readOnly = false)
    public void addFriend(String name, String currentName) {
        User user = userRepository.findUserByNameIgnoreCase(name).get();
        User cU = userRepository.findUserByNameIgnoreCase(currentName).get();
        user.getFriends().add(cU);
        saveWithoutPasswordEncryption(user);
    }

    /**
     * Удаляет пользователя из списка друзей текущего пользователя.
     * <p>Находит в репозитории авторизованного пользователя и пользователя для удаления,
     * затем удаляет их из взаимных списков друзей и сохраняет оба профиля
     * без повторной шифровки пароля.</p>
     *
     * @param name        логин пользователя, которого нужно удалить из друзей
     * @param currentName логин текущего (авторизованного) пользователя
     */
    @Transactional(readOnly = false)
    public void deleteFriend(String name, String currentName) {
        User user = userRepository.findUserByNameIgnoreCase(currentName).get();
        User cU = userRepository.findUserByNameIgnoreCase(name).get();
        user.getFriends().remove(cU);
        cU.getFriends().remove(user);
        saveWithoutPasswordEncryption(user);
        saveWithoutPasswordEncryption(cU);
    }
}