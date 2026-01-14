import javax.swing.*;
import java.awt.*;
import java.sql.*;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

public class Top5FilmesGrafico extends JFrame {

    private Connection c;
    private PieDataset ds;

    public Top5FilmesGrafico() {
        super("Top 5 melhores filmes (Letterboxd)");

        carregarDados();

        JFreeChart chart = ChartFactory.createPieChart(
                "Top 5 melhores filmes por rating", // título
                ds,                                  // dataset
                true,                                // legenda
                true,                                // tooltips
                false                                // URLs
        );

        // Adicionar o gráfico à janela
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        this.add(chartPanel, BorderLayout.CENTER);

        this.setSize(650, 450);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // centraliza
        this.setVisible(true);
    }

    public void carregarDados() {
        try {
            this.criarConexao();
            ds = createDataset();
            this.c.close();
        } catch (ClassNotFoundException e1) {
            System.out.println("Classe JDBC errada " + e1.getMessage());
        } catch (SQLException e2) {
            System.out.println("Erro de SQL " + e2.getMessage());
            try {
                if (this.c != null) {
                    this.c.rollback();
                }
            } catch (SQLException e) {
                // ignora
            }
        }
    }

    public static void main(String[] args) {
        new Top5FilmesGrafico();
    }

    // AQUI você conecta no movies.db
    public void criarConexao() throws ClassNotFoundException, SQLException {
        // Se você já tem a classe conexao.getConnection(), pode usar ela:
        // c = conexao.getConnection();

        // ou, se quiser seguir o modelo do professor:
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:movies.db");
        c.setAutoCommit(false);
    }

    public PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        try {
            Statement s = c.createStatement();

            // Top 5 melhores filmes por rating
            ResultSet r = s.executeQuery("""
                    SELECT name, rating
                    FROM movies
                    WHERE rating > 0
                    ORDER BY rating DESC
                    LIMIT 5
                    """);

            while (r.next()) {
                String nomeFilme = r.getString("name");
                double media = r.getDouble("rating");

                dataset.setValue(nomeFilme, media);

                // Só para conferência no console:
                System.out.println(nomeFilme + " -> " + media);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao criar dataset: " + e.getMessage());
        }

        return dataset;
    }
}
