package mutata.com.github.controller;



import mutata.com.github.dao.MyResponse;
import mutata.com.github.entity.User;
import mutata.com.github.event.OnRegistrationCompleteEvent;
import mutata.com.github.service.*;
import mutata.com.github.util.Toastr;
import mutata.com.github.util.UserUtils;
import mutata.com.github.util.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final UserValidator userValidator;

    private final UserUtils userUtils;
    @Autowired
    public AdminController(ApplicationEventPublisher publisher,UserValidator userValidator, UserService userService,UserUtils userUtils) {
        this.userService = userService;
        this.userValidator = userValidator;
        this.publisher = publisher;
        this.userUtils = userUtils;
    }

    @GetMapping(value={"/createUser","/users/create","/create/user"})
    public String showCreateUserPage(Model model) {
        model.addAttribute("user",new User());
        return "/admin/createUser";
    }
    @GetMapping(value = "/deleteUser")
    public String deleteUser(@RequestParam(required = false) Optional<String> name,Model model) {
        if(name.isPresent()) {
            String res = name.get();
            User user = res.contains("@") ? userService.findByEmailIgnoreCase(res) : userService.findByNameIgnoreCase(res);
            if(user != null) {
                model.addAttribute("success",true);
                userService.delete(user);
            } else {
                model.addAttribute("success",false);
                model.addAttribute("message","Такого пользователя не существует");
            }
        }
        return "/admin/deleteUser";
    }
    @GetMapping("/editUser")
    public String showEditUserPage(Model model) {
        model.addAttribute("user",new User());
        return "/admin/editUser";
    }

    @PostMapping(value = "/editUser")
    public String editUser(@ModelAttribute(name = "user") @Valid User editedUser,BindingResult result,Model model) {

        if(result.hasErrors() || userService.findByNameIgnoreCase(editedUser.getName()) == null) {
            Toastr.addErrorsToModel(model,result);
            return showEditUserPage(model);
        } else {
            userService.save(editedUser);
            model.addAttribute("success",true);
        }
        return "/admin/editUser";
    }

    @PostMapping(value={"/createUser","/users/create","/create/user"})
    public String createNewUser(@ModelAttribute @Valid User user, BindingResult result, Model model, HttpServletRequest request) {
        userValidator.validate(user,result);
        if(result.hasErrors()) {
            Toastr.addErrorsToModel(model,result);
        } else {
            model.addAttribute("success",true);
            userService.save(user);
            if(user.getEnabled() == 0)
                publisher.publishEvent(new OnRegistrationCompleteEvent(user,request.getContextPath()));
        }
        return "/admin/createUser";
    }
    @GetMapping(value = {"/index","/",""})
    public String showAdminPanel(CsrfToken token, Model model, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String findBy,
                                 @RequestParam(required = false) Integer currentPage, @RequestParam(required = false) Integer itemsPerPage,
                                 @RequestParam(required = false) String find,
                                 @RequestParam(required = false) String sortDirection) {
        model.addAttribute("sortBy",sortBy);
        model.addAttribute("findBy",findBy);
        model.addAttribute("csrfToken",token.getToken());
        sortBy = "noSort".equals(sortBy) ? null : sortBy;
        paginationUser(userService,itemsPerPage,currentPage,sortBy,find,findBy,sortDirection,model);

        return "/admin/index";
    }

    private void paginationUser(UserService service,Integer itemsPerPage,Integer currentPage,String sortBy,String find,String findBy,String sortDirection,Model model) {

        currentPage = currentPage == null || currentPage <= 0  ? 1 : currentPage;
        itemsPerPage = itemsPerPage == null ? 15 : itemsPerPage;
        sortDirection = sortDirection == null ? "asc" : sortDirection;
        model.addAttribute("currentPage",currentPage);
        model.addAttribute("itemsPerPage",itemsPerPage);
        model.addAttribute("totalUsers",userService.getCount());
        Page<User> page = null;
        if(sortBy == null || sortBy.isEmpty()) {
            if(find == null || find.isEmpty()) {
                page = service.findAllReturnPage(currentPage - 1,itemsPerPage);
                model.addAttribute("objects",page.getContent());
                model.addAttribute("total",page.getTotalPages());
            } else {
                MyResponse<User> response = service.find(currentPage - 1,itemsPerPage,find,findBy);
                model.addAttribute("objects",response.getContent());
                model.addAttribute("total",(int) Math.ceil(response.getTotal() / (itemsPerPage * 1.0)));
            }
        } else {
            if(find == null || find.isEmpty()) {
                page = service.findAllSortedBy(currentPage - 1,itemsPerPage,sortBy,sortDirection);
                model.addAttribute("objects",page.getContent());
                model.addAttribute("total",page.getTotalPages());
            } else {
                MyResponse<User> response =  service.findAndSort(currentPage - 1,itemsPerPage,find,findBy,sortBy,sortDirection);
                model.addAttribute("objects",response.getContent());
                model.addAttribute("total",(int) Math.ceil(response.getTotal() / (itemsPerPage * 1.0)));
            }
        }

        model.addAttribute("sortDirection",sortDirection);

    }

    @PostMapping("/block/{username}")
    public @ResponseBody String block(@PathVariable String username) {
        userService.block(username);
        return "";
    }

    @PostMapping("/unblock/{username}")
    public @ResponseBody String unblock(@PathVariable String username) {
        userService.unblock(username);
        return "";
    }

    @PostMapping("/activate/{username}")
    public @ResponseBody String activate(@PathVariable String username) {
        userService.activate(username);
        return "";
    }

    @PostMapping("/deactivate/{username}")
    public @ResponseBody String deactivate(@PathVariable String username) {
        userService.deactivate(username);
        return "";
    }

    @GetMapping("/ajax/loadUser")
    public @ResponseBody String[] loadUserAjax(@RequestParam(required = false) String name) {
        if(name == null)
            return new String[0];
        User user = userService.findByNameIgnoreCase(name);
        if(user == null)
            return new String[0];
        String[] data = new String[6];
        data[0] = name;
        data[1] = user.getEmail();
        data[2] = user.getEncryptedPassword();
        data[3] = user.getEnabled() + "";
        data[4] = user.getBlocked() + "";
        data[5] = user.getRole();
        return data;
    }
    @GetMapping("/ajax/doesTheUserExist")
    public @ResponseBody boolean doesTheUserExistAjax(@RequestParam(required = false) String name) {
        return name == null || userService.findByNameIgnoreCase(name) != null;
    }
    private class JavaScriptUser {
        public final String name;
        public final String email;
        public final byte blocked;
        public final byte enabled;
        public final String role;
        JavaScriptUser(User user) {
            this.name = user.getName();
            this.blocked = user.getBlocked();
            this.email = user.getEmail();
            this.enabled = user.getEnabled();
            this.role = user.getRole();
        }

        @Override
        public String toString() {
            return "JavaScriptUser{" +
                    "name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", blocked=" + blocked +
                    ", enabled=" + enabled +
                    ", role='" + role + '\'' +
                    '}';
        }
    }
    @GetMapping("/ajax/process")
    public @ResponseBody List<JavaScriptUser> processAjax(@RequestParam(required = false) String sortBy, @RequestParam(required = false) String findBy,
                                                    @RequestParam(required = false) String sortDirection,@RequestParam(required = false) Integer itemsPerPage,
                                                    @RequestParam(required = false) Integer currentPage,@RequestParam(required = false) String find,
                                                    Model model) {
        sortBy = "noSort".equals(sortBy) ? null : sortBy;
        paginationUser(userService,itemsPerPage,currentPage,sortBy,find,findBy,sortDirection,model);
        // Getting The users
        List<User> users = (List<User>) model.getAttribute("objects");
        List<JavaScriptUser> list = new ArrayList<>();
        if(users != null)
            users.forEach(c -> list.add(new JavaScriptUser(c)));
        return list;
    }

}
