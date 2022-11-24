package mutata.com.github.MatematixProject.util;

import mutata.com.github.MatematixProject.entity.AvatarInfo;
import mutata.com.github.MatematixProject.entity.User;
import mutata.com.github.MatematixProject.service.AvatarInfoService;
import mutata.com.github.MatematixProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
public class UserUtils {
    private  final UserService userService;
    private final AvatarInfoService avatarInfoService;
    private static final int FIVE_MINUTES_IN_MS = 300_000; // 5 * 60 * 1000
    private static final int HOUR_IN_MS = 3_600_000; // 60 * 1000 * 60

    @Autowired
    public UserUtils(UserService userService,AvatarInfoService avatarInfoService) {
        this.userService = userService;
        this.avatarInfoService = avatarInfoService;
    }

    public boolean isAlive(String username) {
        if(username == null)
            return false;
        if(username.equals(SecurityContextHolder.getContext().getAuthentication().getName()))
            return true;
        User targetUser = userService.findByNameIgnoreCase(username);
        Date theDate = targetUser.getLastTimeOnline();
        if(theDate == null)
            return false;
        return (new Date().getTime() - (theDate.getTime() - 3 * HOUR_IN_MS)) < FIVE_MINUTES_IN_MS;
        // Transform GMT + 0 Into GMT + 3
    }
    public boolean setAlive(String username) {
        User user = userService.findByNameIgnoreCase(username);
        if(user != null) {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            calendar.add(Calendar.HOUR_OF_DAY,3); // Transform GMT + 0 Into GMT + 3
            user.setLastTimeOnline(calendar.getTime());
            userService.saveWithoutPasswordEncryption(user);
        }
        return true;
    }

    public boolean hasAvatar(String username, Model model,String prefix) {
        AvatarInfo info = avatarInfoService.findByName(username).orElse(null);
        boolean result = info == null;
        if(result)
            model.addAttribute(String.format("has%sAvatar", StringUtils.capitalize(prefix)),false);
        else {
            model.addAttribute(String.format("has%sAvatar", StringUtils.capitalize(prefix)),true);
            model.addAttribute(String.format("%s%s", prefix,"".equals(prefix) ? "avatar" : StringUtils.capitalize("avatar")),
                    Utils.encodeAvatar(info.getAvatar()));
            model.addAttribute(String.format("%s%s", prefix,"".equals(prefix) ? "avatarFormat" : StringUtils.capitalize("avatarFormat")),
                    info.getAvatarFormat());
        }
        return result;
    }
    public boolean hasAvatar(String username,Model model) {
        return hasAvatar(username,model,"");
    }
}
