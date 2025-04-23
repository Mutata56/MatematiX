package mutata.com.github.MatematixProject.configuration;
import mutata.com.github.MatematixProject.security.AuthenticationProviderImpl;
import mutata.com.github.MatematixProject.service.MyUserDetailsService;
import mutata.com.github.MatematixProject.service.ResetPasswordTokenService;
import mutata.com.github.MatematixProject.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

/**
 * Класс конфигурации защиты веб-сайта. Используется для настройки доступа к определённым страницам, настройки ауентификации, конфигурации,
 * Удаления токенов ResetPassword (Токен для восстановления пароля), VerificationToken (Токен для верификации юзера посредством отправки ему email сообщения)
 * @author Khaliullin Cyrill
 * @version 1.0.0
 *
 * Configuration - данный класс является классом конфигурации.
 * EnableWebSecurity - данный класс используется для активации и настройки веб-безопасности сайта.
 * EnableGlobalMethodSecurity  - доп. слой защиты, защита отдельных методов в бинах ( Настройка доступа к контенту с помощью ролей)
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize Annotation
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /** Объект, отвечающий за ауентификацию */
    private final AuthenticationProvider provider;

    /** Сервис для работы с БД VerificationTokens
     * @see mutata.com.github.MatematixProject.entity.token.VerificationToken
     */

    private final VerificationTokenService verificationTokenService;

    /** Сервис для работы с БД ResetPasswordToken
     * @see mutata.com.github.MatematixProject.entity.token.ResetPasswordToken
     */

    private final ResetPasswordTokenService resetPasswordTokenService;

    /** Сервис для работы с БД users (Пользователи)
     * @see MyUserDetailsService
     */

    private final MyUserDetailsService myUserDetailsService;



    @Autowired
    public SecurityConfiguration(AuthenticationProviderImpl provider, VerificationTokenService verificationTokenService,
                                 ResetPasswordTokenService resetPasswordTokenService, MyUserDetailsService myUserDetailsService) {
        this.provider = provider;
        this.verificationTokenService = verificationTokenService;
        this.resetPasswordTokenService = resetPasswordTokenService;
        this.myUserDetailsService = myUserDetailsService;
    }

    /**
     * Конфигурация объекта, отвечающего за регистрацию пользователя в системе.
     */

    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(myUserDetailsService);
    }

    /**
     * Конфигурация объекта, отвечающего за ауентификацию пользователя в системе.
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(provider);
    }

    /**
     * Конфигурация входящего http запроса по ролям, задача страницы для ауентификации в том числе логина и регистрации, лог-аута.
     * @param http -  http запрос, поступающий на сервер со стороны клиента.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                .antMatchers("/api/**").hasRole("ADMIN")
                .antMatchers("/ajax/**").permitAll()
                .antMatchers("/settings/**").authenticated()
                .antMatchers("/auth/ajax/**").permitAll()
                .antMatchers("/uploadAvatar").authenticated()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/").permitAll()
                .antMatchers("/jax").permitAll()
                .antMatchers("/articles/**").permitAll()
                .antMatchers("/getInTouch").permitAll()
                .antMatchers("/process_getInTouch").permitAll()
                .antMatchers("/auth/registrationConfirm").authenticated()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/img/**").permitAll()
                .antMatchers("/logout").authenticated()
                .antMatchers("/auth/login").anonymous()
                .antMatchers("/auth/error").permitAll()
                .antMatchers("/auth/register").anonymous()
                .antMatchers("/auth/forgotPassword").anonymous()
                .antMatchers("/auth//process_forgotPassword").anonymous()
                .antMatchers("/auth/resetPassword").anonymous()
                .antMatchers("/h2/**").permitAll() // FIXME Тестирование ДБ h2, запретить для ординарных пользователей в будущшем
                .anyRequest().authenticated().and()
                .formLogin().loginPage("/auth/login")
                .loginProcessingUrl("/auth/process_login")
                .defaultSuccessUrl("/",false)
                .failureHandler(authenticationFailureHandler()).and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/").
                    and().exceptionHandling().accessDeniedHandler(accessDeniedHandler());
    }

    /**
     * Удаление тех VerificationToken, которые существуют в системе больше чем EXPIRATION_IN_HOURS часов в системе, дабы те не висели мёртвым грузом. Данная операция выполняется раз в сутки
     * @see mutata.com.github.MatematixProject.entity.token.VerificationToken
     * fixedRate = 60 * 60 * 24 * 1000 (мс).
     */
    @Scheduled(fixedRate = 3600 * 24000)
    public void purgeExpiredVerificationTokens() {
        verificationTokenService.deleteExpiredSince(LocalDateTime.now());
    }

    /**
     *  Удаление тех ResetPasswordToken, которые существуют в системе больше чем EXPIRATION_IN_HOURS часов в системе, дабы те не висели мёртвым грузом. Данная операция выполняется раз в сутки
     * @see mutata.com.github.MatematixProject.entity.token.ResetPasswordToken
     * fixedRate = 60 * 60 * 24 * 1000 (мс).
     */

    @Scheduled(fixedRate = 3600 * 4000)
    public void purgeExpiredResetPasswordTokens() {
        resetPasswordTokenService.deleteExpiredSince(LocalDateTime.now());
    }

    /**
     * Конфигурация объекта, отвечающего за запрет получения какого-либо контента для пользователя.
     */

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new mutata.com.github.MatematixProject.security.AccessDeniedHandler();
    }

    /**
     * Конфигурация объекта, отвечающего за ошибку во время ауентификации пользователя.
     */

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new mutata.com.github.MatematixProject.security.AuthenticationFailureHandler();
    }

}
