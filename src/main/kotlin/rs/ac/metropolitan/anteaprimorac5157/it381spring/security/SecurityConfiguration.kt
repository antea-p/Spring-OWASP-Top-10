package rs.ac.metropolitan.anteaprimorac5157.it381spring.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher


@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { requests -> requests
                    .requestMatchers("/login", "/error").permitAll()
                    .requestMatchers("/*.css").permitAll()
                    .requestMatchers("/students/**").hasRole("TEACHER")
                    .anyRequest().authenticated()
            }
            .formLogin { form -> form
                    .loginPage("/login")
                    .defaultSuccessUrl("/students", true)
                    .failureUrl("/login?error=true")
                    .permitAll()
            }
            .logout { logout -> logout
                    .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout=true")
                    .permitAll()
            }

        return http.build()
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        val user: UserDetails =
            User.withDefaultPasswordEncoder()
                .username("teacher@university.edu")
                .password("password")
                .roles("TEACHER")
                .build()

        return InMemoryUserDetailsManager(user)
    }
}