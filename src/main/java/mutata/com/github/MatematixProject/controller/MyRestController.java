package mutata.com.github.MatematixProject.controller;

import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.event.OnRegistrationCompleteEvent;
import mutata.com.github.MatematixProject.rest.UserNotFoundException;
import mutata.com.github.MatematixProject.service.MyRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/users")
// Need to disable CSRF token to make POST,PUT,DELETE Mappings Work
// Or Provide CSRF Token to the client
public class MyRestController {
    private final MyRestService service;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public MyRestController(MyRestService service,ApplicationEventPublisher publisher) {
        this.service = service;
        this.publisher = publisher;
    }

    @GetMapping(value = {"/",""})
    public List<User> showUsers() {
        List<User> users = service.getUsers();
        if(users ==  null)
            throw new UserNotFoundException("No users are present");
        return service.getUsers();
    }

    @GetMapping("/{name}")
    public User showUser(@PathVariable String name) {
        User user = getUser(name);
        return service.getUserByName(name);
    }

    @PostMapping("")
    public User createUser(@RequestBody User user, HttpServletRequest request) {
        //FIXME VALIDATE
        user.setEnabled((byte) 0);
        user.setRole("ROLE_USER");
        user.setResetPasswordToken(null);
        service.save(user);
        publisher.publishEvent(new OnRegistrationCompleteEvent(user,request.getContextPath()));
        return user;
    }

    @DeleteMapping("/{name}")
    public void deleteUser(@PathVariable String name) {
        User user = getUser(name);
        System.out.println(user);
        service.delete(user);
    }

    @PutMapping("")
    public  User updateUser(@RequestBody User user) {
        service.save(user);
        return user;
    }
    private  User getUser(String name) {
        User user = service.getUserByName(name);
        if(user == null)
            throw new UserNotFoundException("The user is not present");
        return user;
    }

}
