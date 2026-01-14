import javax.swing.*;
import java.awt.*;
import java.sql.*;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

public class Top5FilmesGrafico2 extends JFrame {

    private Connection c;
    private PieDataset ds;

    public Top5FilmesGrafico2() {
        super("Top 5 melhores filmes (Letterboxd)");

        carregarDados();

        JFreeChart chart = ChartFactory.createPieChart(
                "Top 5 maiores notas do letterbox ", // título
                ds,                                  // dataset
                true,                                // legenda
                true,                                // tooltips
                false                                // URLs
        );

        // adiciona o gráfico na janela
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(700, 450));
        this.add(chartPanel, BorderLayout.CENTER);

        this.setSize(750, 500);
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
                // ignorar
            }
        }
    }

    // *** MUITO IMPORTANTE ***
    // O main AGORA CHAMA A CLASSE CERTA
    public static void main(String[] args) {
        new Top5FilmesGrafico2();
    }

    // conexão com o banco
    public void criarConexao() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:movies.db");
        c.setAutoCommit(false);
    }

    public PieDataset createDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        try {
            Statement s = c.createStatement();

            // consulta TOP 5 filmes por rating, com ano
            ResultSet r = s.executeQuery("""
                SELECT name, date, rating
                FROM movies
                WHERE rating > 0 AND date > 0
                ORDER BY rating DESC
                LIMIT 5
            """);

            while (r.next()) {
                String nomeFilme = r.getString("name");
                int ano = r.getInt("date");
                double media = r.getDouble("rating");

                // cria o rótulo "Nome (Ano)"
                String rotuloFinal = nomeFilme + " (" + ano + ")";

                // adiciona ao gráfico
                dataset.setValue(rotuloFinal, media);

                // debug no console
                System.out.println(rotuloFinal + " -> " + media);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao criar dataset: " + e.getMessage());
        }

        return dataset;
    }
}
