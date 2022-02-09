package com.vivek.calendit.configuration;

import com.vivek.calendit.exception.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity(debug = true)
@EnableOAuth2Client
@Order(1)
public class WebConfiguration extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity security) throws Exception {
    security.csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
        .authorizeRequests(a -> a
            .antMatchers("/", "*/oauth2/**", "/error", "/oauth_login",
                "https://accounts.google.com/o/oauth2/v4/token",
                "https://accounts.google.com/o/oauth2/v2/auth", "/webjars/**")
            .permitAll().anyRequest().authenticated())
        .exceptionHandling(e -> e.authenticationEntryPoint(new RestAuthenticationEntryPoint()))
        .oauth2Login(o -> {
          o.failureHandler((request, response, exception) -> {
            request.getSession().setAttribute("error.message", exception.getMessage());
          });
          o.loginPage("/");
          o.defaultSuccessUrl("/loginSuccess");
        }).logout(l -> l.deleteCookies("JESSIONID", "XSRF-TOKEN").invalidateHttpSession(true)
            .clearAuthentication(true).logoutSuccessUrl("/").permitAll());
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
  }
}
