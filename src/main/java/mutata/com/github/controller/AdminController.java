package mutata.com.github.controller;



import mutata.com.github.dao.MyResponse;
import mutata.com.github.entity.User;
import mutata.com.github.event.OnRegistrationCompleteEvent;
import mutata.com.github.service.*;
import mutata.com.github.util.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final ResetPasswordTokenService resetPasswordTokenService;

    private final ApplicationEventPublisher publisher;

    private final UserValidator userValidator;
    @Autowired
    public AdminController(ApplicationEventPublisher publisher,UserValidator userValidator, UserService userService, VerificationTokenService verificationTokenService, ResetPasswordTokenService resetPasswordTokenService) {
        this.userService = userService;
        this.resetPasswordTokenService = resetPasswordTokenService;
        this.verificationTokenService = verificationTokenService;
        this.userValidator = userValidator;
        this.publisher = publisher;
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
            System.out.println(res);
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
         class JavaScriptUser {
             public final String name;
             public final String email;
             public final String password;
             public final byte blocked;
             public final byte enabled;
             public final String role;
             JavaScriptUser(User user) {
                 this.name = user.getName();
                 this.blocked = user.getBlocked();
                 this.email = user.getEmail();
                 this.enabled = user.getEnabled();
                 this.role = user.getRole();
                 this.password = user.getEncryptedPassword();
             }
        }
        model.addAttribute("users",userService.findAll().stream().map(JavaScriptUser::new).toList());
        model.addAttribute("user",new User());
        return "/admin/editUser";
    }
    @PostMapping(value = "/editUser")
    public String editUser(@ModelAttribute @Valid User editedUser,BindingResult result,Model model) {
        if(result.hasErrors()) {
            handleErrors(model,result); // FIXME IF THE USER DOESN'T EXIST
        } else {
            userService.save(editedUser);
            model.addAttribute("success",true);
        }
        return "/admin/editUser";
    }
    private void handleErrors(Model model, BindingResult result) {
        List<ObjectError> errors = result.getAllErrors();
        StringBuilder builder = new StringBuilder();
        errors.forEach(err -> builder.append(err.getDefaultMessage()).append("|"));
        model.addAttribute("message",builder.toString());
        model.addAttribute("success",0);
    }
    @PostMapping(value={"/createUser","/users/create","/create/user"})
    public String createNewUser(@ModelAttribute @Valid User user, BindingResult result, Model model, HttpServletRequest request) {
        userValidator.validate(user,result);
        if(result.hasErrors()) {
            handleErrors(model,result);
        } else {
            model.addAttribute("success",true);
            userService.save(user);
            if(user.getEnabled() == 0)
                publisher.publishEvent(new OnRegistrationCompleteEvent(user,request.getContextPath()));
        }
        return "/admin/createUser";
    }
    @GetMapping(value = {"/index","/",""})
    public String showAdminPanel(Model model, @RequestParam(required = false) String sortBy,@RequestParam(required = false) String findBy,
                                 @RequestParam(required = false) Integer currentPage,@RequestParam(required = false) Integer itemsPerPage,
                                 @RequestParam(required = false) String find,
                                 @RequestParam(required = false) String sortDirection) {
        model.addAttribute("sortBy",sortBy);
        model.addAttribute("findBy",findBy);


        sortBy = "noSort".equals(sortBy) ? null : sortBy;
        paginationUser(userService,itemsPerPage,currentPage,sortBy,find,findBy,sortDirection,model);

        return "/admin/index";
    }

    private void paginationUser(UserService service,Integer itemsPerPage,Integer currentPage,String sortBy,String find,String findBy,String sortDirection,Model model) {

        currentPage = currentPage == null || currentPage <= 0  ? 1 : currentPage;
        itemsPerPage = itemsPerPage == null ? 15 : itemsPerPage;
        model.addAttribute("currentPage",currentPage);

        model.addAttribute("itemsPerPage",itemsPerPage);
        Page<User> page = null;

        sortDirection = sortDirection == null ? "asc" : sortDirection;

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

    @GetMapping("/block/{username}")
    public String block(@PathVariable String username,@RequestParam(required = false) String sortBy,@RequestParam(required = false) String findBy,
                        @RequestParam(required = false) Integer currentPage,@RequestParam(required = false) Integer itemsPerPage,
                        @RequestParam(required = false) String find,@RequestParam(required = false) String sortDirection,Model model) {
        userService.block(username);
        return showAdminPanel(model,sortBy,findBy,currentPage,itemsPerPage,find,sortDirection);
    }

    @GetMapping("/unblock/{username}")
    public String unblock(@PathVariable String username,@RequestParam(required = false) String sortBy,@RequestParam(required = false) String findBy,
                          @RequestParam(required = false) Integer currentPage,@RequestParam(required = false) Integer itemsPerPage,
                          @RequestParam(required = false) String find,@RequestParam(required = false) String sortDirection,Model model) {
        userService.unblock(username);
        return showAdminPanel(model,sortBy,findBy,currentPage,itemsPerPage,find,sortDirection);
    }

    @GetMapping("/activate/{username}")
    public String activate(@PathVariable String username,@RequestParam(required = false) String sortBy,@RequestParam(required = false) String findBy,
                           @RequestParam(required = false) Integer currentPage,@RequestParam(required = false) Integer itemsPerPage,
                           @RequestParam(required = false) String find,@RequestParam(required = false) String sortDirection,Model model) {
        userService.activate(username);
        return showAdminPanel(model,sortBy,findBy,currentPage,itemsPerPage,find,sortDirection);
    }

    @GetMapping("/deactivate/{username}")
    public String deactivate(@PathVariable String username,@RequestParam(required = false) String sortBy,@RequestParam(required = false) String findBy,
                             @RequestParam(required = false) Integer currentPage,@RequestParam(required = false) Integer itemsPerPage,
                             @RequestParam(required = false) String find,@RequestParam(required = false) String sortDirection,Model model) {
        userService.deactivate(username);
        return showAdminPanel(model,sortBy,findBy,currentPage,itemsPerPage,find,sortDirection);
    }
}
