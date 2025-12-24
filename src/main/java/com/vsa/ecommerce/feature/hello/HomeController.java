package com.vsa.ecommerce.feature.hello;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return """
                <html>
                <head>
                    <title>Java VSA Monolith</title>
                </head>
                <body>
                    <h1>Welcome to Java VSA Monolith!</h1>
                    <p>API is running successfully.</p>
                    <ul>
                        <li><a href="/hello">Test Hello Endpoint</a></li>
                        <li><a href="/actuator/health">Health Check</a></li>
                    </ul>
                </body>
                </html>
                """;
    }
}
