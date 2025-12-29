package com.vsa.ecommerce.config.mail;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Properties;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender(MailProperties mailProperties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());
        mailSender.setProtocol(mailProperties.getProtocol());
        mailSender.setDefaultEncoding(mailProperties.getDefaultEncoding());

        Properties props = mailSender.getJavaMailProperties();
        applyProperties(props, mailProperties.getProperties(), null);

        return mailSender;
    }

    private void applyProperties(Properties props, Map<String, Object> map, String prefix) {
        if (map == null)
            return;

        map.forEach((key, value) -> {
            String newKey = (prefix == null ? "" : prefix + ".") + key;
            if (value instanceof Map) {
                applyProperties(props, (Map<String, Object>) value, newKey);
            } else if (value != null) {
                props.put(newKey, value.toString());
            }
        });
    }
}
