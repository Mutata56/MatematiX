package mutata.com.github.MatematixProject.security;

import mutata.com.github.MatematixProject.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {

    private final MyUserDetailsService service;
    private final PasswordEncoder encoder;
    @Autowired
    public AuthenticationProviderImpl(MyUserDetailsService myUserDetailsService, PasswordEncoder encoder) {
        this.service = myUserDetailsService;
        this.encoder = encoder;

    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        UserDetails details;
        if(!username.contains("@"))
              details = service.loadUserByUsername(username);
        else
             details = service.loadUserByEmail(username);
        if(!encoder.matches(authentication.getCredentials().toString(),details.getPassword()))
            throw new BadCredentialsException("Неверный пароль!");
        return new UsernamePasswordAuthenticationToken(details,details.getPassword(), details.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
