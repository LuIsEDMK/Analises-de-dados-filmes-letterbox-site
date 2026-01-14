import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImportarMoviesCSV {

    // -------------------------
    // Método auxiliar para pegar valor limpo da coluna
    // -------------------------
    private static String getVal(String[] row, int idx) {
        if (idx < 0 || idx >= row.length) return null;
        String v = row[idx];
        return (v == null) ? null : v.trim();
    }

    public static void main(String[] args) {

        // AJUSTE O CAMINHO DO CSV AQUI
        String csvPath = "W:/codigos/java/aulas de java/dados/filmes.csv";

        String sql = """
                INSERT INTO movies (
                    id, name, date, tagline, description, minute, rating,
                    genres, studios, themes, countries,
                    actors, directors, writers, poster_filename
                ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);
                """;

        try (
                Connection conn = conexao.getConnection();
                PreparedStatement pst = conn.prepareStatement(sql);
                CSVReader reader = new CSVReader(new FileReader(csvPath))
        ) {

            String[] row;

            // 1) Pular cabeçalho
            row = reader.readNext();
            if (row == null) {
                System.out.println("CSV vazio!");
                return;
            }

            int count = 0;

            // 2) Ler linha por linha
            while ((row = reader.readNext()) != null) {

                if (row.length < 21) {
                    System.out.println("Linha inválida, ignorada:");
                    System.out.println(String.join(" | ", row));
                    continue;
                }

                try {
                    int i = 1;

                    // ler valores conforme o SEU cabeçalho
                    int id = Integer.parseInt(getVal(row, 0));
                    String name = getVal(row, 1);

                    String dateStr = getVal(row, 2);
                    int date = (dateStr == null || dateStr.isEmpty()) ? 0 : Integer.parseInt(dateStr);

                    String tagline = getVal(row, 4);
                    String description = getVal(row, 5);

                    String minuteStr = getVal(row, 6);
                    int minute = (minuteStr == null || minuteStr.isEmpty()) ? 0 : Integer.parseInt(minuteStr);

                    String ratingStr = getVal(row, 8);
                    double rating = (ratingStr == null || ratingStr.isEmpty()) ? 0.0 : Double.parseDouble(ratingStr);

                    String genres = getVal(row, 10);
                    String studios = getVal(row, 12);
                    String themes = getVal(row, 14);
                    String countries = getVal(row, 16);
                    String actors = getVal(row, 17);
                    String directors = getVal(row, 18);
                    String writers = getVal(row, 19);
                    String poster = getVal(row, 20);

                    // preencher PreparedStatement
                    pst.setInt(i++, id);
                    pst.setString(i++, name);
                    pst.setInt(i++, date);
                    pst.setString(i++, tagline);
                    pst.setString(i++, description);
                    pst.setInt(i++, minute);
                    pst.setDouble(i++, rating);
                    pst.setString(i++, genres);
                    pst.setString(i++, studios);
                    pst.setString(i++, themes);
                    pst.setString(i++, countries);
                    pst.setString(i++, actors);
                    pst.setString(i++, directors);
                    pst.setString(i++, writers);
                    pst.setString(i++, poster);

                    pst.addBatch();
                    count++;

                    if (count % 500 == 0) {
                        pst.executeBatch();
                        System.out.println("Inseridos: " + count);
                    }

                } catch (Exception e) {
                    System.out.println("Erro nesta linha, ignorando:");
                    System.out.println(String.join(" | ", row));
                    e.printStackTrace();
                }
            }

            pst.executeBatch();
            System.out.println("Importação concluída. Total inserido: " + count);

        } catch (SQLException | IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
