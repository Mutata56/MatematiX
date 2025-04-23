package mutata.com.github.MatematixProject.dao;

import mutata.com.github.MatematixProject.entity.User;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

/**
 * DAO - data access object - объект, используемый для получения информации из БД, в данном случае это ДАО объект для пользоваетелей. Связан с БД users.
 * Transactional(readOnly = true) - Аннотация указывает, что метод выполняется в транзакции «только для чтения». Такие транзакции используются, когда операции с базой данных включают только чтение данных и не изменяют их состояние.
 * Component - указание, что данный класс является компонентом в контексте Spring
 */
@Component
@Transactional(readOnly = true)
public class UserDAO {
    /**
     * Объект, который будет поставлять сессии
     */
    private final EntityManager entityManager;

    @Autowired
    public UserDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Найти таких пользователей, у которых имя подходит под паттерн pattern.
     * @param currentPage - текущая страница в пагинации.
     * @param itemsPerPage - кол-во пользователей на страницу в пагинации.
     * @param pattern - паттерн, по которому стоит искать пользователей в БД.
     * @return DAO объект, связанный с классом User. Внутри общее кол-во пользователей, подходящих под паттерн, и сами пользователи в количестве itemsPerPage
     */

    public MyResponse<User> findWhereNameLike(Integer currentPage,Integer itemsPerPage,String pattern) {
        pattern = "%" + pattern + "%";
        Session session = entityManager.unwrap(Session.class); // Открытие сессии
        Query query = entityManager.createQuery("SELECT user FROM User user WHERE user.name LIKE :pattern",User.class); // MySQL запрос для получения пользователей с именем, подходящим под паттерн, указание типа результата посредством User.class
        query.setParameter("pattern",pattern); // Установка параметра (паттерн)
        query.setFirstResult((currentPage) * itemsPerPage); // Первые учитываемые результаты будут идти с номера currentPage * itemsPerPage
        query.setMaxResults(itemsPerPage); // Всего может быть itemsPerPage результатов
        Query countQuery = entityManager.createQuery("SELECT COUNT(user) FROM User user WHERE user.name LIKE :pattern",Long.class); // MySQL запрос для подсчёта кол-во пользователей с именем, подходяящим под паттерн, указание типа результата посредством Long.class
        countQuery.setParameter("pattern",pattern); // Установка параметра (паттерн)
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<User>(query.getResultList(),total); // Возвращение DAO<User>
    }

    /**
     * Найти таких пользователей, у которых роль подходит под паттерн pattern.
     * @param currentPage - текущая страница в пагинации.
     * @param itemsPerPage - кол-во пользователей на страницу в пагинации.
     * @param pattern - паттерн, по которому стоит искать пользователей в БД.
     * @return DAO объект, связанный с классом User. Внутри общее кол-во пользователей, подходящих под паттерн, и сами пользователи в количестве itemsPerPage
     */
    public MyResponse<User> findWhereRoleLike(Integer currentPage,Integer itemsPerPage,String pattern) {
        pattern = pattern.startsWith("админ") || pattern.startsWith("1") || pattern.startsWith("admin") ? "ROLE_ADMIN" : "ROLE_USER";
        Session session = entityManager.unwrap(Session.class); // Открытие сессии
        Query query = entityManager.createQuery("SELECT user FROM User user WHERE user.role LIKE :pattern",User.class);
        query.setParameter("pattern",pattern);
        query.setFirstResult((currentPage) * itemsPerPage); // Первые учитываемые результаты будут идти с номера currentPage * itemsPerPage
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery("SELECT COUNT(user) FROM User user WHERE user.role LIKE :pattern",Long.class);
        countQuery.setParameter("pattern",pattern);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<User>(query.getResultList(),total); // Возвращение DAO<User>
    }

    /**
     * Найти таких пользователей, у которых состояние "заблокирован" (blocked) подходит под паттерн pattern.
     * @param currentPage - текущая страница в пагинации.
     * @param itemsPerPage - кол-во пользователей на страницу в пагинации.
     * @param pattern - паттерн, по которому стоит искать пользователей в БД.
     * @return DAO объект, связанный с классом User. Внутри общее кол-во пользователей, подходящих под паттерн, и сами пользователи в количестве itemsPerPage
     */

    public MyResponse<User> findWhereBlockedLike(Integer currentPage,Integer itemsPerPage,String pattern) {
        byte patternFindBy = pattern.startsWith("block") || pattern.startsWith("заблок") || pattern.startsWith("1") ? (byte) 1 : (byte) 0;
        Session session = entityManager.unwrap(Session.class); // Открытие сессии
        Query query = entityManager.createQuery("SELECT user FROM User user WHERE user.blocked = :pattern",User.class);
        query.setParameter("pattern",patternFindBy);
        query.setFirstResult((currentPage) * itemsPerPage); // Первые учитываемые результаты будут идти с номера currentPage * itemsPerPage
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery("SELECT COUNT(user) FROM User user WHERE user.blocked = :pattern",Long.class);
        countQuery.setParameter("pattern",patternFindBy);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<User>(query.getResultList(),total); // Возвращение DAO<User>
    }
    /**
     * Найти таких пользователей, у которых почта подходит под паттерн pattern.
     * @param currentPage - текущая страница в пагинации.
     * @param itemsPerPage - кол-во пользователей на страницу в пагинации.
     * @param pattern - паттерн, по которому стоит искать пользователей в БД.
     * @return DAO объект, связанный с классом User. Внутри общее кол-во пользователей, подходящих под паттерн, и сами пользователи в количестве itemsPerPage
     */

    public MyResponse<User> findWhereEmailLike(Integer currentPage,Integer itemsPerPage,String pattern) {
        pattern = "%" + pattern + "%";
        Session session = entityManager.unwrap(Session.class); // Открытие сессии
        Query query = entityManager.createQuery("SELECT user FROM User user WHERE user.email LIKE :pattern",User.class);
        query.setParameter("pattern",pattern);
        query.setFirstResult((currentPage) * itemsPerPage); // Первые учитываемые результаты будут идти с номера currentPage * itemsPerPage
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery("SELECT COUNT(user) FROM User user WHERE user.email LIKE :pattern",Long.class);
        countQuery.setParameter("pattern",pattern);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<User>(query.getResultList(),total); // Возвращение DAO<User>
    }

    /**
     * Найти таких пользователей, у которых состояние "активирован" (enabled) подходит под паттерн pattern.
     * @param currentPage - текущая страница в пагинации.
     * @param itemsPerPage - кол-во пользователей на страницу в пагинации.
     * @param pattern - паттерн, по которому стоит искать пользователей в БД.
     * @return DAO объект, связанный с классом User. Внутри общее кол-во пользователей, подходящих под паттерн, и сами пользователи в количестве itemsPerPage
     */

    public MyResponse<User> findWhereEnabledLike(Integer currentPage, Integer itemsPerPage, String pattern) {
        byte patternFindBy = pattern.startsWith("activ") || pattern.startsWith("активиро") || pattern.startsWith("1") ? (byte) 1 : (byte) 0;
        Session session = entityManager.unwrap(Session.class); // Открытие сессии
        Query query = entityManager.createQuery("SELECT user FROM User user WHERE user.enabled = :pattern",User.class);
        query.setParameter("pattern",patternFindBy);
        query.setFirstResult((currentPage) * itemsPerPage); // Первые учитываемые результаты будут идти с номера currentPage * itemsPerPage
        query.setMaxResults(itemsPerPage);
        Query countQuery = entityManager.createQuery("SELECT COUNT(user) FROM User user WHERE user.enabled = :pattern",Long.class);
        countQuery.setParameter("pattern",patternFindBy);
        long total = (Long) countQuery.getSingleResult();
        return new MyResponse<User>(query.getResultList(),total); // Возвращение DAO<User>
    }

    /**
     * Найти таких пользователей, у которых роль подходит под паттерн pattern.
     * @param user - пользователь, у которого ищем друзей
     * @param of - объект "страница" в данном случае используется лишь для определния первых результатов MySQL запроса
     * @return DAO объект, связанный с классом User. Внутри общее кол-во пользователей, подходящих под паттерн, и сами пользователи в количестве itemsPerPage
     */

    public MyResponse<User> findAllFriendsReturnPage(User user, PageRequest of) {
        Session session = entityManager.unwrap(Session.class); // Открытие сессии
        Query query = entityManager.createQuery("SELECT friend FROM User user JOIN user.friends friend WHERE user.name = :username");
        query.setParameter("username",user.getName());
        query.setFirstResult((of.getPageNumber()) * of.getPageSize()); // Первые учитываемые результаты будут идти с номера getPageNumber * getPageSize
        query.setMaxResults(of.getPageSize());
        return new MyResponse<User>(query.getResultList(),countOfFriends(user)); // Возвращение DAO<User>
    }

    /**
     * Найти таких пользователей, у которых роль подходит под паттерн pattern.
     * @param user - пользователь, чьих друзей мы считаем
     * @return DAO объект, связанный с классом User. Внутри общее кол-во пользователей, подходящих под паттерн, и сами пользователи в количестве itemsPerPage
     */

    public Long countOfFriends(User user) {
        Session session = entityManager.unwrap(Session.class); // Открытие сессии
        Query countQuery = entityManager.createQuery("SELECT COUNT(friend) FROM User user JOIN user.friends friend WHERE user.name = :username");
        countQuery.setParameter("username",user.getName());
        long total = (Long) countQuery.getSingleResult();
        return total;
    }
}
