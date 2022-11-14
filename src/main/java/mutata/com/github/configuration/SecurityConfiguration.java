package mutata.com.github.configuration;
import mutata.com.github.security.AuthenticationProviderImpl;
import mutata.com.github.service.MyUserDetailsService;
import mutata.com.github.service.ResetPasswordTokenService;
import mutata.com.github.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import java.time.LocalDateTime;
// Password Encoder was moved to Main App Config in order to avoid circular reference
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize Annotation
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final AuthenticationProvider provider;

    private final VerificationTokenService verificationTokenService;

    private final ResetPasswordTokenService resetPasswordTokenService;

    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public SecurityConfiguration(AuthenticationProviderImpl provider, VerificationTokenService verificationTokenService,
                                 ResetPasswordTokenService resetPasswordTokenService, MyUserDetailsService myUserDetailsService) {
        this.provider = provider;
        this.verificationTokenService = verificationTokenService;
        this.resetPasswordTokenService = resetPasswordTokenService;
        this.myUserDetailsService = myUserDetailsService;
    }
    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(myUserDetailsService);
    }

    // Configure Authentication
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(provider);
    }

    // Configure HTTPS requests authorization
    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                .antMatchers("/api/**").hasRole("ADMIN")
                .antMatchers("/ajax/**").permitAll()
                .antMatchers("/auth/ajax/**").permitAll()
                .antMatchers("/uploadAvatar").authenticated()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/").permitAll()
                .antMatchers("/jax").permitAll()
                .antMatchers("/articles/**").permitAll()
                .antMatchers("/getInTouch").permitAll()
                .antMatchers("/process_getInTouch").permitAll()
                .antMatchers("/auth/registrationConfirm").authenticated()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/logout").authenticated()
                .antMatchers("/auth/login").anonymous()
                .antMatchers("/auth/error").permitAll()
                .antMatchers("/auth/register").anonymous()
                .antMatchers("/auth/forgotPassword").anonymous()
                .antMatchers("/auth//process_forgotPassword").anonymous()
                .antMatchers("/auth/resetPassword").anonymous()
                .anyRequest().authenticated().and()
                .formLogin().loginPage("/auth/login")
                .loginProcessingUrl("/auth/process_login")
                .defaultSuccessUrl("/",false)
                .failureHandler(authenticationFailureHandler()).and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/").
                    and().exceptionHandling().accessDeniedHandler(accessDeniedHandler());
    }

    @Scheduled(fixedRate = 3600 * 24000)
    public void purgeExpiredVerificationTokens() {
        verificationTokenService.deleteExpiredSince(LocalDateTime.now());
    }

    @Scheduled(fixedRate = 3600 * 4000)
    public void purgeExpiredResetPasswordTokens() {
        resetPasswordTokenService.deleteExpiredSince(LocalDateTime.now());
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new mutata.com.github.security.AccessDeniedHandler();
    }
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new mutata.com.github.security.AuthenticationFailureHandler();
    }
}
