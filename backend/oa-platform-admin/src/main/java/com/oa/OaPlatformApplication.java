package com.oa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OA 平台启动类
 */
@SpringBootApplication
public class OaPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(OaPlatformApplication.class, args);
        System.out.println("""
            
            ╔═══════════════════════════════════════════════════╗
            ║           OA 平台启动成功！                        ║
            ║                                                   ║
            ║   Swagger UI: http://localhost:8080/doc.html      ║
            ║   API Docs:   http://localhost:8080/v3/api-docs   ║
            ║                                                   ║
            ╚═══════════════════════════════════════════════════╝
            """);
    }
}