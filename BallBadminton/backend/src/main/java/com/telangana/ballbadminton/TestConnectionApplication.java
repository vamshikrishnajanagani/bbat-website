package com.telangana.ballbadminton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

/**
 * Simple application to test database connection
 * This application only tests connectivity without running migrations
 */
@SpringBootApplication(exclude = {
    RedisAutoConfiguration.class,
    RedisRepositoriesAutoConfiguration.class
})
@ComponentScan(basePackages = {
    "com.telangana.ballbadminton.config"
})
public class TestConnectionApplication {

    public static void main(String[] args) {
        try {
            System.out.println("🔍 Starting database connection test...");
            
            var context = SpringApplication.run(TestConnectionApplication.class, args);
            
            // Test database connection
            DataSource dataSource = context.getBean(DataSource.class);
            
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                
                System.out.println("✅ Database connection successful!");
                System.out.println("📊 Database Info:");
                System.out.println("   - Product Name: " + metaData.getDatabaseProductName());
                System.out.println("   - Product Version: " + metaData.getDatabaseProductVersion());
                System.out.println("   - Driver Name: " + metaData.getDriverName());
                System.out.println("   - Driver Version: " + metaData.getDriverVersion());
                System.out.println("   - URL: " + metaData.getURL());
                System.out.println("   - Username: " + metaData.getUserName());
                
                // Check if tables exist
                System.out.println("\n📋 Checking existing tables:");
                try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
                    int tableCount = 0;
                    while (tables.next() && tableCount < 10) { // Show first 10 tables
                        String tableName = tables.getString("TABLE_NAME");
                        System.out.println("   - " + tableName);
                        tableCount++;
                    }
                    if (tableCount == 0) {
                        System.out.println("   - No tables found (empty database)");
                    } else if (tableCount == 10) {
                        System.out.println("   - ... and more tables");
                    }
                }
                
                System.out.println("\n🎉 Connection test completed successfully!");
                
            } catch (Exception e) {
                System.err.println("❌ Database connection failed: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
            
            // Keep the application running for a few seconds to see the health endpoint
            System.out.println("\n🌐 Application is running on http://localhost:8080/api/v1/health");
            System.out.println("💡 Press Ctrl+C to stop");
            
        } catch (Exception e) {
            System.err.println("❌ Application startup failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}