import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CriarTabelaMovies {

    public static void main(String[] args) {
        String sql = """
            CREATE TABLE IF NOT EXISTS movies (
                id              INTEGER PRIMARY KEY,
                name            TEXT,
                date            INTEGER,          -- ano (ex: 2023)
                tagline         TEXT,
                description     TEXT,
                minute          INTEGER,
                rating          REAL,
                genres          TEXT,
                studios         TEXT,
                themes          TEXT,
                countries       TEXT,
                actors          TEXT,
                directors       TEXT,
                writers         TEXT,
                poster_filename TEXT
            );
            """;

        try (Connection conn = conexao.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Tabela 'movies' criada/verificada com sucesso.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
