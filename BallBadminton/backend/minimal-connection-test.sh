#!/bin/bash

echo "🔍 Minimal database connection test..."

# Create a simple Java class to test connection
cat > TestConnection.java << 'EOF'
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://c1erdbv5s7bd6i.cluster-czz5s0kz4scl.eu-west-1.rds.amazonaws.com:5432/df72l64ipgnnt9";
        String username = "uarq7169567h9a";
        String password = "p2a3d8927b39fd36827c371f06b66377c30f5c1c121f030bb9a1ec6f30990201b";
        
        System.out.println("🔍 Testing PostgreSQL connection...");
        System.out.println("📡 Host: c1erdbv5s7bd6i.cluster-czz5s0kz4scl.eu-west-1.rds.amazonaws.com");
        System.out.println("🗄️  Database: df72l64ipgnnt9");
        System.out.println("👤 User: uarq7169567h9a");
        System.out.println("");
        
        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            
            // Establish connection
            Connection connection = DriverManager.getConnection(url, username, password);
            
            System.out.println("✅ Connection successful!");
            
            // Get database metadata
            DatabaseMetaData metaData = connection.getMetaData();
            System.out.println("📊 Database Info:");
            System.out.println("   - Product: " + metaData.getDatabaseProductName());
            System.out.println("   - Version: " + metaData.getDatabaseProductVersion());
            System.out.println("   - Driver: " + metaData.getDriverName() + " " + metaData.getDriverVersion());
            
            // Check if any tables exist
            System.out.println("\n📋 Checking tables:");
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            int count = 0;
            while (tables.next() && count < 5) {
                System.out.println("   - " + tables.getString("TABLE_NAME"));
                count++;
            }
            
            if (count == 0) {
                System.out.println("   - No tables found (empty database)");
            } else if (count == 5) {
                System.out.println("   - ... and more");
            }
            
            // Test a simple query
            System.out.println("\n🔍 Testing query execution:");
            var statement = connection.createStatement();
            var result = statement.executeQuery("SELECT version(), current_database(), current_user");
            
            if (result.next()) {
                System.out.println("   - PostgreSQL Version: " + result.getString(1).split(" ")[1]);
                System.out.println("   - Current Database: " + result.getString(2));
                System.out.println("   - Current User: " + result.getString(3));
            }
            
            connection.close();
            System.out.println("\n🎉 All tests passed! Database is ready for Spring Boot.");
            
        } catch (Exception e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
EOF

# Compile and run the test
echo "🔨 Compiling test..."
javac -cp "build/libs/*" TestConnection.java

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo "🚀 Running connection test..."
java -cp ".:build/libs/*" TestConnection

# Cleanup
rm -f TestConnection.java TestConnection.class

echo "🏁 Test completed!"