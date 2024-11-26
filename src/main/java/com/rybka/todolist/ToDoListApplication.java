package com.rybka.todolist;

import jakarta.servlet.ServletContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

@SpringBootApplication(scanBasePackages = {"com.rybka.todolist", "com.rybka.todolist.User"})
@EnableJpaRepositories(basePackages = {"com.rybka.todolist.User", "com.rybka.todolist.Tasks"})
public class ToDoListApplication implements WebApplicationInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ToDoListApplication.class, args);

    }

    public void onStartup(ServletContext sc) {

        AnnotationConfigWebApplicationContext root = new AnnotationConfigWebApplicationContext();
        root.register(SecurityConfig.class);

        sc.addListener(new ContextLoaderListener(root));

        sc.addFilter("securityFilter", new DelegatingFilterProxy("springSecurityFilterChain"))
                .addMappingForUrlPatterns(null, false, "/*");
    }
}
