#!/bin/bash

echo "🔍 Simple PostgreSQL connection test..."

# Database connection details
DB_HOST="c1erdbv5s7bd6i.cluster-czz5s0kz4scl.eu-west-1.rds.amazonaws.com"
DB_PORT="5432"
DB_NAME="df72l64ipgnnt9"
DB_USER="uarq7169567h9a"
DB_PASSWORD="p2a3d8927b39fd36827c371f06b66377c30f5c1c121f030bb9a1ec6f30990201b"

echo "📡 Host: $DB_HOST"
echo "🗄️  Database: $DB_NAME"
echo "👤 User: $DB_USER"
echo ""

# Create a simple Java test
cat > SimpleConnectionTest.java << 'EOF'
import java.sql.*;

public class SimpleConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://c1erdbv5s7bd6i.cluster-czz5s0kz4scl.eu-west-1.rds.amazonaws.com:5432/df72l64ipgnnt9";
        String username = "uarq7169567h9a";
        String password = "p2a3d8927b39fd36827c371f06b66377c30f5c1c121f030bb9a1ec6f30990201b";
        
        System.out.println("🔍 Testing PostgreSQL connection...");
        
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
            
            // Test a simple query
            System.out.println("\n🔍 Testing query execution:");
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT version(), current_database(), current_user");
            
            if (result.next()) {
                System.out.println("   - PostgreSQL Version: " + result.getString(1).split(" ")[1]);
                System.out.println("   - Current Database: " + result.getString(2));
                System.out.println("   - Current User: " + result.getString(3));
            }
            
            // Check if any tables exist
            System.out.println("\n📋 Checking tables:");
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            int count = 0;
            while (tables.next() && count < 10) {
                System.out.println("   - " + tables.getString("TABLE_NAME"));
                count++;
            }
            
            if (count == 0) {
                System.out.println("   - No tables found (empty database)");
            } else if (count == 10) {
                System.out.println("   - ... and more tables");
            }
            
            connection.close();
            System.out.println("\n🎉 All tests passed! Database is ready.");
            
        } catch (Exception e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
EOF

# Extract PostgreSQL driver from the JAR
echo "🔨 Extracting PostgreSQL driver..."
cd build/libs
jar -xf telangana-ball-badminton-association-*.jar BOOT-INF/lib/postgresql-42.6.0.jar
mv BOOT-INF/lib/postgresql-42.6.0.jar postgresql.jar
cd ../..

# Compile and run the test
echo "🔨 Compiling test..."
javac -cp "build/libs/postgresql.jar" SimpleConnectionTest.java

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo "🚀 Running connection test..."
java -cp ".:build/libs/postgresql.jar" SimpleConnectionTest

# Cleanup
rm -f SimpleConnectionTest.java SimpleConnectionTest.class
rm -rf build/libs/BOOT-INF
rm -f build/libs/postgresql.jar

echo "🏁 Test completed!"