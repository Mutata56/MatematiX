package mutata.com.github.controller;



import mutata.com.github.dao.MyResponse;
import mutata.com.github.entity.User;
import mutata.com.github.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final VerificationTokenService verificationTokenService;
    private final ResetPasswordTokenService resetPasswordTokenService;

    @Autowired
    public AdminController(UserService userService,VerificationTokenService verificationTokenService,ResetPasswordTokenService resetPasswordTokenService) {
        this.userService = userService;
        this.resetPasswordTokenService = resetPasswordTokenService;
        this.verificationTokenService = verificationTokenService;
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
