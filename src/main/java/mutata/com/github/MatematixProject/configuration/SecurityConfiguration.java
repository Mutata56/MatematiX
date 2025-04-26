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
 * Конфигурация безопасности веб-приложения.
 * <p>Настраивает доступ к ресурсам по ролям, параметры формы логина, выход из системы,
 * а также планирует очистку устаревших токенов в фоновом режиме.</p>
 *
 * @author Khaliullin Cyrill
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity  // Включает веб-безопасность Spring Security
@EnableGlobalMethodSecurity(prePostEnabled = true)  // Разрешает использование аннотаций @PreAuthorize
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /**
     * Провайдер аутентификации, реализующий логику проверки учетных данных.
     */
    private final AuthenticationProvider provider;

    /**
     * Сервис для работы с токенами верификации email.
     * @see mutata.com.github.MatematixProject.entity.token.VerificationToken
     */
    private final VerificationTokenService verificationTokenService;

    /**
     * Сервис для работы с токенами сброса пароля.
     * @see mutata.com.github.MatematixProject.entity.token.ResetPasswordToken
     */
    private final ResetPasswordTokenService resetPasswordTokenService;

    /**
     * Сервис для загрузки деталей пользователя при аутентификации.
     * @see MyUserDetailsService
     */
    private final MyUserDetailsService myUserDetailsService;

    /**
     * Конструктор для внедрения зависимостей.
     *
     * @param provider - провайдер аутентификации
     * @param verificationTokenService - сервис для токенов верификации
     * @param resetPasswordTokenService - сервис для токенов сброса пароля
     * @param myUserDetailsService - сервис для загрузки данных пользователя
     */
    @Autowired
    public SecurityConfiguration(
            AuthenticationProviderImpl provider,
            VerificationTokenService verificationTokenService,
            ResetPasswordTokenService resetPasswordTokenService,
            MyUserDetailsService myUserDetailsService) {
        this.provider = provider;
        this.verificationTokenService = verificationTokenService;
        this.resetPasswordTokenService = resetPasswordTokenService;
        this.myUserDetailsService = myUserDetailsService;
    }

    /**
     * Регистрирует пользовательский сервис в AuthenticationManagerBuilder.
     *
     * @param builder билдер менеджера аутентификации
     * @throws Exception в случае ошибки конфигурации
     */
    @Autowired
    public void registerGlobalAuthentication(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(myUserDetailsService);
    }

    /**
     * Конфигурирует провайдер аутентификации.
     *
     * @param auth билдер аутентификации
     * @throws Exception в случае ошибки конфигурации
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(provider);
    }

    /**
     * Настройка правил доступа к HTTP-ресурсам по URL и ролям,
     * а также конфигурация страниц логина и выхода.
     *
     * @param http объект настройки HTTP безопасности
     * @throws Exception в случае ошибки конфигурации
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
                .antMatchers("/auth/resetPassword").permitAll()
                .antMatchers("/h2/**").permitAll()  // FIXME: Отключить доступ к H2 в проде
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/process_login")
                .defaultSuccessUrl("/", false)
                .failureHandler(authenticationFailureHandler())
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());
    }

    /**
     * Периодическая очистка устаревших VerificationToken во избежание накопления.
     * Выполняется раз в сутки.
     */
    @Scheduled(fixedRate = 3600 * 24000)
    public void purgeExpiredVerificationTokens() {
        verificationTokenService.deleteExpiredSince(LocalDateTime.now());
    }

    /**
     * Периодическая очистка устаревших ResetPasswordToken во избежание накопления.
     * Выполняется раз в сутки.
     */
    @Scheduled(fixedRate = 3600 * 4000)
    public void purgeExpiredResetPasswordTokens() {
        resetPasswordTokenService.deleteExpiredSince(LocalDateTime.now());
    }

    /**
     * Бин для обработки отказа в доступе (HTTP 403).
     *
     * @return экземпляр AccessDeniedHandler
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new mutata.com.github.MatematixProject.security.AccessDeniedHandler();
    }

    /**
     * Бин для обработки неудачных попыток аутентификации.
     *
     * @return экземпляр AuthenticationFailureHandler
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new mutata.com.github.MatematixProject.security.AuthenticationFailureHandler();
    }
}
