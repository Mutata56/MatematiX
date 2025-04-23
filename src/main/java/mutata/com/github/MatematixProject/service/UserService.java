package mutata.com.github.MatematixProject.service;


import mutata.com.github.MatematixProject.dao.MyResponse;
import mutata.com.github.MatematixProject.dao.UserDAO;
import mutata.com.github.MatematixProject.entity.AvatarInfo;
import mutata.com.github.MatematixProject.entity.Comment;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.repository.UserRepository;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;
/**
 * Сервис юзеров.
 * @see User
 * Transactional(readOnly = true) - данный сервис занимается только чтением, и никак не изменяет данные БД
 */
@Service
@Transactional(readOnly = true)
public class UserService implements MyService<User> {

    private final UserRepository userRepository;

    /**
     * Шифровщик паролей
     */

    private final PasswordEncoder passwordEncoder;

    private final EntityManager manager;
    /**
     * Data acess object - объект получения данных юзеров
     * @see UserDAO
     */
    private final UserDAO userDAO;

    @Autowired
    public UserService(UserRepository repository, UserDAO userDAO, PasswordEncoder encoder, EntityManager manager) {
        this.userRepository = repository;
        this.passwordEncoder = encoder;
        this.manager = manager;
        this.userDAO = userDAO;
    }

    /**
     * Сохранить юзера с шифровкой пароля
     * @param user - юзер, которого нужно сохранить
     */
    @Transactional(readOnly = false)
    public void save(User user) {

        String pass = passwordEncoder.encode(user.getEncryptedPassword()); // Пароль в зашифрованном виде

        user.setEncryptedPassword(pass); // Устанавливаем зашифрованный пароль
        userRepository.save(user);
    }

    /**
     * Сохранить юзера без шифровки пароля (Нужно в таких ситуациях как смена аватарки, какого либо состояния юзера, не связанного с паролем)
     * @param user - юзер, чье состояние мы сохраняем
     */
    @Transactional(readOnly = false)
    public void saveWithoutPasswordEncryption(User user) {
        userRepository.save(user);
    }

    /**
     * Найти юзера по почте
     * @param email - почта, по которой нужно найти юзера
     * @return юзер, найденный по почте либо null
     */

    public User findByEmailIgnoreCase(String email) {
        return userRepository.findUserByEmailIgnoreCase(email).orElse(null);
    }

    /**
     * Найти юзера по никнейму
     * @param name - никнейм, по которой нужно найти юзера
     * @return юзер, найденный по почте либо null
     */

    public User findByNameIgnoreCase(String name) {
        return userRepository.findUserByNameIgnoreCase(name).orElse(null);
    }

    /**
     * Найти всех юзеров
     * @return список всех юзеров на сайте (зарегестрированных)
     */

    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Найти всех юзеров на сайте. Сделать пагиацию
     * @param currentPage - страница, на которой нужно найти сущность
     * @param itemsPerPage - сколько сущностей должно быть на странице
     * @return страница с количеством юзеров itemsPerPage начиная со страницу currentPage
     */

    public Page<User> findAllReturnPage(Integer currentPage, Integer itemsPerPage) {
        return userRepository.findAll(PageRequest.of(currentPage,itemsPerPage));
    }

    /**
     * Найти всех юзеров на сайте по параметру findBy по паттерну find. Сделать пагиацию
     * @param currentPage - страница, на которой нужно найти сущность
     * @param itemsPerPage - сколько сущностей должно быть на странице
     * @param find - паттерн, по которому необходимо искать(Например Ivanov,32)
     * @param findBy - параметр, по которому необходимо искать(Например name,rating)
     * @return страница с количеством юзеров itemsPerPage начиная со страницу currentPage с поиском по параметру findBy по паттерну find
     */

    @Override
    public MyResponse<User> find(Integer currentPage, Integer itemsPerPage, String find, String findBy) {

        switch (findBy) {
            case "name":
                return userDAO.findWhereNameLike(currentPage,itemsPerPage,find);
            case "role":
                return userDAO.findWhereRoleLike(currentPage,itemsPerPage,find);
            case "blocked":
                return userDAO.findWhereBlockedLike(currentPage,itemsPerPage,find);
            case "email":
                return userDAO.findWhereEmailLike(currentPage,itemsPerPage,find);
            case "activated":
                return userDAO.findWhereEnabledLike(currentPage,itemsPerPage,find);
        }
        return null;
    }

    /**
     * Найти всех юзеров на сайте, отсортировать по параметру sortBy в направлении sortDirection. Сделать пагиацию
     * @param currentPage - страница, на которой нужно найти сущность
     * @param itemsPerPage - сколько сущностей должно быть на странице
     * @param sortBy - по какому параметру сортировать (например имя, рейтинг и т.д.)
     * @param sortDirection - направление сортировки (например возраст., убыв.)
     * @return страница с количеством юзеров itemsPerPage начиная со страницу currentPage с сортировкой по параметру sortBy и направлением сортировки sortDirection
     */
    @Override
    public Page<User> findAllSortedBy(Integer currentPage, Integer itemsPerPage, String sortBy,String sortDirection) {
        return userRepository.findAll(PageRequest.of(currentPage,itemsPerPage,"asc".equals(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC,sortBy));
    }

    /**
     * Найти всех юзеров на сайте по параметру findBy по паттерну find, отсортировать по параметру sortBy в направлении sortDirection. Сделать пагиацию
     * @param currentPage - страница, на которой нужно найти сущность
     * @param itemsPerPage - сколько сущностей должно быть на странице
     * @param find - паттерн, по которому нужно искать (например Иванов, 32)
     * @param findBy - по какому параметру искать (например имя, рейтинг и т.д.)
     * @param sortBy - по какому параметру сортировать (например имя, рейтинг и т.д.)
     * @param sortDirection - направление сортировки (например возраст., убыв.)
     * @return страница с количеством юзеров itemsPerPage начиная со страницу currentPage с сортировкой по параметру sortBy и направлением сортировки sortDirection
     */
    @Override
    public MyResponse<User> findAndSort(Integer currentPage, Integer itemsPerPage, String find, String findBy, String sortBy,String sortDirection) {
        MyResponse<User> response = find(currentPage,itemsPerPage,find,findBy);
        switch (sortBy) {
            case "name":
                response.getContent().sort("asc".equals(sortDirection) ? Comparator.comparing(User::getName) : Comparator.comparing(User::getName).reversed());
            case "blocked":
                response.getContent().sort("asc".equals(sortDirection) ? Comparator.comparing(User::getBlocked) : Comparator.comparing(User::getBlocked).reversed());
            case "activated":
                response.getContent().sort("asc".equals(sortDirection) ? Comparator.comparing(User::getEnabled) : Comparator.comparing(User::getEnabled).reversed());
            case "email":
                response.getContent().sort("asc".equals(sortDirection) ? Comparator.comparing(User::getEmail) : Comparator.comparing(User::getEmail).reversed());
            case "role":
                response.getContent().sort("asc".equals(sortDirection) ? Comparator.comparing(User::getRole) : Comparator.comparing(User::getRole).reversed());
        }
        return response;
    }

    /**
     * Удалить юзера по сущности
     * @param user - сущность, которую нужно удалить
     */
    @Transactional(readOnly = false)
    public void delete(User user) {
        userRepository.delete(user);
    }

    /**
     * Найти юзера по имени и загрузить его закладки
     * @param userToFind - юзер, по которому нужно искать в строковом литерале
     * @return
     */
    public User findByNameIgnoreCaseAndLoadArticles(String userToFind) {
        User user = userRepository.findUserByNameIgnoreCase(userToFind).orElse(null);
        // Cannot be null thanks to Spring Security
        Hibernate.initialize(user.getArticles()); // Получить статьи юзера (FetchType = Lazy)
        return user;
    }

    /**
     * Поиск всех юзеров с загрузкой всех токенов в систему. В данный момент не используется
     * @return список всех пользователей
     */

    public List<User> customFindAll() {
        Session session = manager.unwrap(Session.class);
        Query<User> query = session.createQuery("SELECT user FROM User user LEFT JOIN FETCH user.resetPasswordToken LEFT JOIN  FETCH user.verificationToken",User.class);

        return query.getResultList();
    }


    @Transactional(readOnly = false)
    public void block(String username) {
        User user = userRepository.findUserByNameIgnoreCase(username).get();
        user.setBlocked((byte) 1);
        saveWithoutPasswordEncryption(user);
    }

    @Transactional(readOnly = false)
    public void unblock(String username) {
        User user = userRepository.findUserByNameIgnoreCase(username).get();
        user.setBlocked((byte) 0);
        saveWithoutPasswordEncryption(user);
    }

    @Transactional(readOnly = false)
    public void activate(String username) {
        User user = userRepository.findUserByNameIgnoreCase(username).get();
        user.setEnabled((byte) 1);
        saveWithoutPasswordEncryption(user);

    }

    @Transactional(readOnly = false)
    public void deactivate(String username) {
        User user = userRepository.findUserByNameIgnoreCase(username).get();
        user.setEnabled((byte) 0);
        saveWithoutPasswordEncryption(user);
    }

    /**
     * Кол-во всех юзеров в БД
     */

    public long getCount() {
        return userRepository.count();
    }

    /**
     * Найти всех друзей юзера, сделать пагинацию.
     */

    public MyResponse<User> findAllFriendsReturnPage(User user, int page, int itemsPerPage) {
        return userDAO.findAllFriendsReturnPage(user,PageRequest.of(page,itemsPerPage));
    }

    /**
     * Метод добавления друга
     */

    @Transactional(readOnly = false)
    public void addFriend(String name, String currentName) {
        User user = userRepository.findUserByNameIgnoreCase(currentName).get();
        User cU = userRepository.findUserByNameIgnoreCase(name).get();
        user.getFriends().add(cU);
        cU.getFriends().add(user);
        System.err.println(user);
        System.err.println(cU);
        saveWithoutPasswordEncryption(user);
        saveWithoutPasswordEncryption(cU);
    }

    /**
     * Метод удаления друга
     */

    @Transactional(readOnly = false)
    public void deleteFriend(String name, String currentName) {
        User user = userRepository.findUserByNameIgnoreCase(currentName).get();
        User cU = userRepository.findUserByNameIgnoreCase(name).get();
        user.getFriends().remove(cU);
        cU.getFriends().remove(user);
        System.err.println(user);
        System.err.println(cU);
        saveWithoutPasswordEncryption(user);
        saveWithoutPasswordEncryption(cU);
    }
}
