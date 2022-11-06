package mutata.com.github.service;


import mutata.com.github.dao.MyResponse;
import mutata.com.github.dao.UserDAO;
import mutata.com.github.entity.User;
import mutata.com.github.repository.UserRepository;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService implements MyService<User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final EntityManager manager;

    private final UserDAO userDAO;

    @Autowired
    public UserService(UserRepository repository, UserDAO userDAO, PasswordEncoder encoder, EntityManager manager) {
        this.userRepository = repository;
        this.passwordEncoder = encoder;
        this.manager = manager;
        this.userDAO = userDAO;
    }

    @Transactional(readOnly = false)
    public void save(User user) {

        String pass = passwordEncoder.encode(user.getEncryptedPassword());

        user.setEncryptedPassword(pass);
        userRepository.save(user);
    }
    @Transactional(readOnly = false)
    public void saveWithoutPasswordEncryption(User user) {
        userRepository.save(user);
    }

    public User findByEmailIgnoreCase(String email) {
        return userRepository.findUserByEmailIgnoreCase(email).orElse(null);
    }
    public User findByNameIgnoreCase(String name) {
        return userRepository.findUserByNameIgnoreCase(name).orElse(null);
    }
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findAllReturnPage(Integer currentPage, Integer itemsPerPage) {
        return userRepository.findAll(PageRequest.of(currentPage,itemsPerPage));
    }

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

    @Override
    public Page<User> findAllSortedBy(Integer currentPage, Integer itemsPerPage, String sortBy,String sortDirection) {
        return userRepository.findAll(PageRequest.of(currentPage,itemsPerPage,"asc".equals(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC,sortBy));
    }

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

    @Transactional(readOnly = false)
    public void delete(User user) {
        userRepository.delete(user);
    }

    public User findByNameIgnoreCaseAndLoadArticles(String currentUser) {
        User user = userRepository.findUserByNameIgnoreCase(currentUser).orElse(null);
        // Cannot be null thanks to Spring Security
        Hibernate.initialize(user.getArticles()); // Initialize
        return user;
    }

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
    public long getCount() {
        return userRepository.count();
    }
}
