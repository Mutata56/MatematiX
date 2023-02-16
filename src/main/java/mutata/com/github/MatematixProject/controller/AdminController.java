package mutata.com.github.MatematixProject.controller;



import mutata.com.github.MatematixProject.dao.MyResponse;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
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

    @PatchMapping("/ajax/update")
    public @ResponseBody boolean updatePersonAjax(@RequestParam String username,@RequestParam String email,@RequestParam Boolean blocked,
                                                      @RequestParam Boolean activated,@RequestParam String role) {
        var tempUser = userService.findByNameIgnoreCase(username);
        boolean hasToBeCreated = tempUser == null;
        if (hasToBeCreated) {
            tempUser = new User();
            tempUser.setName(username);
            tempUser.setEncryptedPassword("12345");
        }
        tempUser.setEmail(email);
        tempUser.setBlocked((byte) (blocked ? 1 : 0));
        tempUser.setEnabled((byte) (activated ? 1 : 0));
        tempUser.setRole(role);
        try {
            if(hasToBeCreated)
                userService.save(tempUser);
            else
                userService.saveWithoutPasswordEncryption(tempUser);
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }
    @DeleteMapping("/ajax/delete")
    public @ResponseBody boolean deletePersonAjax(@RequestParam String username) {
        var tempUser = userService.findByNameIgnoreCase(username);
        try {
            userService.delete(tempUser);
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
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
