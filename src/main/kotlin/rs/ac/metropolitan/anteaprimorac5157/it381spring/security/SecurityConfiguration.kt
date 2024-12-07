package rs.ac.metropolitan.anteaprimorac5157.it381spring.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import rs.ac.metropolitan.anteaprimorac5157.it381spring.data.StudentDataStore


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfiguration(private val securityLogger: SecurityLogger) {

    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/", "/login", "/error", "/css/**").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { form ->
                form
                    .loginPage("/login")
                    .successHandler(authenticationSuccessHandler())
                    .failureHandler(authenticationFailureHandler())
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
                    .logoutSuccessHandler(logoutSuccessHandler())
                    .permitAll()
            }

        return http.build()
    }

    @Bean
    fun authenticationSuccessHandler(): AuthenticationSuccessHandler {
        return AuthenticationSuccessHandler { request, response, authentication ->
            securityLogger.logAuthenticationSuccess(authentication.name, request)
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
    fun authenticationFailureHandler(): AuthenticationFailureHandler {
        return AuthenticationFailureHandler { request, response, exception ->
            securityLogger.logAuthenticationFailure(
                request.getParameter("username") ?: "unknown",
                request,
                exception.message
            )
            response.sendRedirect("/login?error=true")
        }
    }

    @Bean
    fun logoutSuccessHandler(): LogoutSuccessHandler {
        return LogoutSuccessHandler { request, response, authentication ->
            authentication?.let { auth ->
                securityLogger.logLogout(auth.name, request)
            }
            response.sendRedirect("/login?logout=true")
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