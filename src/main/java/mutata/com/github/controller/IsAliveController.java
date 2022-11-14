package mutata.com.github.controller;
import mutata.com.github.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@RequestMapping("/isAlive")
@Controller
public class IsAliveController {

    private  final UserUtils userUtils;

    @Autowired
    public IsAliveController(UserUtils userUtils) {
        this.userUtils = userUtils;
    }

    @GetMapping("/setAlive")
    public @ResponseBody boolean setAlive() {
        return userUtils.setAlive(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    @GetMapping("/isAlive")
    public @ResponseBody boolean isAlive() {
        return isUserAlive(SecurityContextHolder.getContext().getAuthentication().getName());
    }
    @GetMapping("/isUserAlive")
    public @ResponseBody boolean isUserAlive(@RequestParam(required = false ) String username) {
        return userUtils.isAlive(username);
    }
}
