import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexao {

    public static Connection getConnection() throws SQLException {
        // opcional: o sqlite-jdbc já registra o driver, mas não faz mal manter:
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver SQLite não encontrado no classpath!", e);
        }

        String url = "jdbc:sqlite:movies.db";
        return DriverManager.getConnection(url);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            System.out.println("Conectado ao SQLite!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
