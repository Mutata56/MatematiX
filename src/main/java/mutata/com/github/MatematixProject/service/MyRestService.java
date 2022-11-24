package mutata.com.github.MatematixProject.service;

import mutata.com.github.MatematixProject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyRestService {

    private final UserService userService;

    @Autowired
    public MyRestService(UserService service) {
        this.userService = service;
    }

    public List<User> getUsers() {
        return userService.findAll();
    }


    public User getUserByName(String name) {
        return userService.findByNameIgnoreCase(name);
    }

    public User getUserByEmail(String email) {
        return userService.findByEmailIgnoreCase(email);
    }

    public void delete(User user) {
        userService.delete(user);
    }

    public void save(User user) {
        userService.save(user);
    }
}
