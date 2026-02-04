#!/bin/bash

echo "🔍 Checking database schema..."

# Create a simple Java test to check schema
cat > SchemaCheck.java << 'EOF'
import java.sql.*;

public class SchemaCheck {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://c1erdbv5s7bd6i.cluster-czz5s0kz4scl.eu-west-1.rds.amazonaws.com:5432/df72l64ipgnnt9";
        String username = "uarq7169567h9a";
        String password = "p2a3d8927b39fd36827c371f06b66377c30f5c1c121f030bb9a1ec6f30990201b";
        
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            
            System.out.println("🔍 Checking database schema...");
            
            // Check key tables
            String[] keyTables = {"users", "members", "players", "tournaments", "news_articles", "districts"};
            
            for (String tableName : keyTables) {
                try {
                    Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("✅ Table '" + tableName + "': " + count + " records");
                    }
                    rs.close();
                    stmt.close();
                } catch (SQLException e) {
                    System.out.println("❌ Table '" + tableName + "': " + e.getMessage());
                }
            }
            
            // Check if flyway schema history exists
            try {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT version FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5");
                System.out.println("\n📋 Recent migrations:");
                while (rs.next()) {
                    System.out.println("   - Version: " + rs.getString(1));
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                System.out.println("\n❌ Flyway schema history not found: " + e.getMessage());
            }
            
            connection.close();
            System.out.println("\n🎉 Schema check completed!");
            
        } catch (Exception e) {
            System.err.println("❌ Schema check failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
EOF

# Compile and run the test
echo "🔨 Compiling schema check..."
javac -cp "build/libs/postgresql.jar" SchemaCheck.java

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo "🚀 Running schema check..."
java -cp ".:build/libs/postgresql.jar" SchemaCheck

# Cleanup
rm -f SchemaCheck.java SchemaCheck.class

echo "🏁 Schema check completed!"