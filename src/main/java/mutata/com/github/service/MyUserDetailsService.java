package mutata.com.github.service;

import mutata.com.github.entity.User;
import mutata.com.github.repository.UserRepository;
import mutata.com.github.security.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Autowired
    public MyUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optional = repository.findUserByNameIgnoreCase(username);
        if(optional.isEmpty())
            throw new UsernameNotFoundException("Пользователь не найден!");
        else
            return new MyUserDetails(optional.get());

    }

    public UserDetails loadUserByEmail(String email) {
        Optional<User> optional = repository.findUserByEmailIgnoreCase(email);
        if(optional.isEmpty())
            throw new UsernameNotFoundException("Пользователь не найден!");
        else
            return new MyUserDetails(optional.get());
    }
}
