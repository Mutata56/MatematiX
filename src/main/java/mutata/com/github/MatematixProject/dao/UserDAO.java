package mutata.com.github.MatematixProject.dao;

import mutata.com.github.MatematixProject.entity.User;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * DAO для работы с сущностью User в базе данных.
 * <p>Предоставляет методы для поиска пользователей по различным критериям
 * (имя, роль, статус блокировки, email и т.д.),
 * а также для работы со списком друзей пользователя.</p>
 *
 * <p>Аннотация @Transactional(readOnly = true) указывает,
 * что все операции выполняются в транзакции только для чтения,
 * что оптимизирует производительность при выборках.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Component
@Transactional(readOnly = true)
public class UserDAO {

    /**
     * Менеджер сущностей для выполнения JPA-запросов.
     */
    private final EntityManager entityManager;

    /**
     * Внедрение EntityManager через конструктор.
     *
     * @param entityManager JPA EntityManager
     */
    @Autowired
    public UserDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Находит пользователей, у которых логин соответствует заданному шаблону.
     * <p>Шаблон оборачивается в проценты для оператора LIKE:</p>
     * <pre>pattern -> %pattern%</pre>
     * <p>Реализована пагинация через вызовы:</p>
     * <ul>
     *     <li>setFirstResult(currentPage * itemsPerPage)</li>
     *     <li>setMaxResults(itemsPerPage)</li>
     * </ul>
     *
     * @param currentPage   индекс страницы (0-based)
     * @param itemsPerPage  число пользователей на страницу
     * @param pattern       подстрока для поиска в имени пользователя
     * @return контейнер {@link MyResponse} с найденными пользователями и общим их числом
     */
    public MyResponse<User> findWhereNameLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        pattern = "%" + pattern + "%";
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery(
                "SELECT user FROM User user WHERE user.name LIKE :pattern", User.class);
        query.setParameter("pattern", pattern);
        query.setFirstResult(currentPage * itemsPerPage);
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery(
                "SELECT COUNT(user) FROM User user WHERE user.name LIKE :pattern", Long.class);
        countQuery.setParameter("pattern", pattern);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<>(query.getResultList(), total);
    }

    /**
     * Находит пользователей по роли (ROLE_ADMIN или ROLE_USER).
     * <p>Шаблон преобразуется в точное значение роли:</p>
     * <ul>
     *     <li>админ*, admin*, 1 -> ROLE_ADMIN</li>
     *     <li>все остальное -> ROLE_USER</li>
     * </ul>
     *
     * @param currentPage   индекс страницы (0-based)
     * @param itemsPerPage  число пользователей на страницу
     * @param pattern       текстовый признак роли
     * @return контейнер {@link MyResponse} с найденными пользователями и общим их числом
     */
    public MyResponse<User> findWhereRoleLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        pattern = pattern.startsWith("админ") || pattern.startsWith("admin") || pattern.startsWith("1")
                ? "ROLE_ADMIN" : "ROLE_USER";
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery(
                "SELECT user FROM User user WHERE user.role LIKE :pattern", User.class);
        query.setParameter("pattern", pattern);
        query.setFirstResult(currentPage * itemsPerPage);
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery(
                "SELECT COUNT(user) FROM User user WHERE user.role LIKE :pattern", Long.class);
        countQuery.setParameter("pattern", pattern);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<>(query.getResultList(), total);
    }

    /**
     * Находит пользователей по статусу блокировки.
     * <p>Паттерн интерпретируется как:</p>
     * <ul>
     *     <li>block*, заблок*, 1 -> blocked = 1</li>
     *     <li>иначе -> blocked = 0</li>
     * </ul>
     *
     * @param currentPage   индекс страницы (0-based)
     * @param itemsPerPage  число пользователей на страницу
     * @param pattern       текстовый признак блокировки
     * @return контейнер {@link MyResponse} с найденными пользователями и общим их числом
     */
    public MyResponse<User> findWhereBlockedLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        byte flag = (byte) (pattern.startsWith("block") || pattern.startsWith("заблок") || pattern.startsWith("1") ? 1 : 0);
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery(
                "SELECT user FROM User user WHERE user.blocked = :flag", User.class);
        query.setParameter("flag", flag);
        query.setFirstResult(currentPage * itemsPerPage);
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery(
                "SELECT COUNT(user) FROM User user WHERE user.blocked = :flag", Long.class);
        countQuery.setParameter("flag", flag);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<>(query.getResultList(), total);
    }

    /**
     * Находит пользователей по шаблону email.
     *
     * @param currentPage   индекс страницы (0-based)
     * @param itemsPerPage  число пользователей на страницу
     * @param pattern       подстрока для поиска в email
     * @return контейнер {@link MyResponse} с найденными пользователями и общим их числом
     */
    public MyResponse<User> findWhereEmailLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        pattern = "%" + pattern + "%";
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery(
                "SELECT user FROM User user WHERE user.email LIKE :pattern", User.class);
        query.setParameter("pattern", pattern);
        query.setFirstResult(currentPage * itemsPerPage);
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery(
                "SELECT COUNT(user) FROM User user WHERE user.email LIKE :pattern", Long.class);
        countQuery.setParameter("pattern", pattern);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<>(query.getResultList(), total);
    }

    /**
     * Находит пользователей по статусу enabled.
     *
     * @param currentPage   индекс страницы (0-based)
     * @param itemsPerPage  число пользователей на страницу
     * @param pattern       текстовый признак активности пользователя
     * @return контейнер {@link MyResponse} с найденными пользователями и общим их числом
     */
    public MyResponse<User> findWhereEnabledLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        byte flag = (byte) (pattern.startsWith("activ") || pattern.startsWith("активиро") || pattern.startsWith("1") ? 1 : 0);
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery(
                "SELECT user FROM User user WHERE user.enabled = :flag", User.class);
        query.setParameter("flag", flag);
        query.setFirstResult(currentPage * itemsPerPage);
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery(
                "SELECT COUNT(user) FROM User user WHERE user.enabled = :flag", Long.class);
        countQuery.setParameter("flag", flag);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<>(query.getResultList(), total);
    }

    /**
     * Возвращает страницу друзей для заданного пользователя.
     *
     * @param user  пользователь, чьих друзей запрашиваем
     * @param pageRequest объект пагинации (номер страницы и размер)
     * @return контейнер {@link MyResponse} с друзьями и общим их числом
     */
    public MyResponse<User> findAllFriendsReturnPage(User user, PageRequest pageRequest) {
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery(
                "SELECT friend FROM User u JOIN u.friends friend WHERE u.name = :username", User.class);
        query.setParameter("username", user.getName());
        query.setFirstResult(pageRequest.getPageNumber() * pageRequest.getPageSize());
        query.setMaxResults(pageRequest.getPageSize());
        long total = countOfFriends(user);
        return new MyResponse<>(query.getResultList(), total);
    }

    /**
     * Подсчитывает общее число друзей у пользователя.
     *
     * @param user пользователь, чьих друзей считаем
     * @return количество друзей
     */
    public Long countOfFriends(User user) {
        Session session = entityManager.unwrap(Session.class);
        Query countQuery = entityManager.createQuery(
                "SELECT COUNT(friend) FROM User u JOIN u.friends friend WHERE u.name = :username", Long.class);
        countQuery.setParameter("username", user.getName());
        return (Long) countQuery.getSingleResult();
    }

    /**
     * Ищет друзей пользователя по частичному совпадению имени и возвращает
     * отфильтрованный список вместе с общим количеством результатов.
     * <p>Метод использует HQL-запросы для выборки и подсчёта сущностей `User`,
     * которые являются друзьями указанного пользователя и чей `name` соответствует шаблону.</p>
     *
     * @param find          строка с именем пользователя и шаблоном поиска в формате "username pattern",
     *                      где `username` — имя пользователя, чьи друзья ищутся,
     *                      а `pattern` — часть имени друга для поиска (без символов %).
     * @param currentPage   индекс текущей страницы (0-based) для пагинации
     * @param itemsPerPage  количество элементов на одной странице
     * @return объект {@link MyResponse} содержащий:
     *         <ul>
     *             <li>список найденных друзей типа {@link User} для текущей страницы</li>
     *             <li>общее число всех совпадений (long) для вычисления количества страниц</li>
     *         </ul>
     */
    public MyResponse<User> findFriendsWhereNameLike(String find, Integer currentPage, Integer itemsPerPage) {
        Session session = entityManager.unwrap(Session.class);
        Query query = entityManager.createQuery(
                "SELECT friend FROM User u JOIN u.friends friend WHERE u.name = :username and friend.name LIKE :pattern", User.class);
        query.setParameter("username", find.split(" ")[0]);
        query.setParameter("pattern", "%" + find.split(" ")[1] + "%");
        query.setFirstResult(currentPage * itemsPerPage);
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery(
                "SELECT COUNT(friend) FROM User u JOIN u.friends friend WHERE u.name = :username and friend.name LIKE :pattern", Long.class);
        countQuery.setParameter("username", find.split(" ")[0]);
        countQuery.setParameter("pattern", "%" + find.split(" ")[1] + "%");
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<>(query.getResultList(), total);
    }


}