package rs.ac.metropolitan.anteaprimorac5157.it381spring.security

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import rs.ac.metropolitan.anteaprimorac5157.it381spring.data.StudentDataStore


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/", "/login", "/error", "/css/**").permitAll()
                    // Zaobilazak sigurnosnih provjera za lokalne zahtjeve
                    .requestMatchers(AntPathRequestMatcher("/**")).access(AuthorizationManager { auth, context ->
                        val request = context.request
                        if (request.remoteAddr == "127.0.0.1" ||
                            request.remoteAddr == "0:0:0:0:0:0:0:1"
                        ) {
                            return@AuthorizationManager AuthorizationDecision(true)
                        }
                        AuthorizationDecision(auth != null)
                    })
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .successHandler(authenticationSuccessHandler())
                    .failureUrl("/login?error=true")
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout=true")
                    .permitAll()
            }

        return http.build()
    }

    @Bean
    fun authenticationSuccessHandler(): AuthenticationSuccessHandler {
        return AuthenticationSuccessHandler { request, response, authentication ->
            when {
                authentication.authorities.any { it.authority == "ROLE_TEACHER" } ->
                    response.sendRedirect("/students")

                authentication.authorities.any { it.authority == "ROLE_STUDENT" } -> {
                    // Nađi studentov ID na temelju maila i preusmjeri ga na "njegovu" stranicu
                    val email = authentication.name
                    val studentId = StudentDataStore.findStudentIdByEmail(email)
                    response.sendRedirect("/students/$studentId")
                }

                else -> response.sendRedirect("/")
            }
        }
    }

    @Bean
    fun userDetailsService(): UserDetailsService {
        val users = buildList {
            add(
                User.withDefaultPasswordEncoder()
                    .username("teacher@university.edu")
                    .password("password")
                    .roles("TEACHER")
                    .build()
            )

            StudentDataStore.students.forEach { student ->
                add(
                    User.withDefaultPasswordEncoder()
                        .username(student.email)
                        .password("password")
                        .roles("STUDENT")
                        .build()
                )
            }
        }

        return InMemoryUserDetailsManager(users)
    }
}