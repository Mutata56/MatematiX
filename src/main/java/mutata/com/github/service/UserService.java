package mutata.com.github.service;


import mutata.com.github.entity.User;
import mutata.com.github.repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository,PasswordEncoder encoder) {
        this.userRepository = repository;
        this.passwordEncoder = encoder;
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
}
