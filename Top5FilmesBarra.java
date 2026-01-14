import javax.swing.*;
import java.awt.*;
import java.sql.*;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.renderer.category.BarRenderer;

public class Top5FilmesBarra extends JFrame {

    private Connection c;

    public Top5FilmesBarra() {
        super("Top 5 melhores filmes (Letterboxd) - Barras");

        DefaultCategoryDataset dataset = carregarDados();

        JFreeChart chart = ChartFactory.createBarChart(
                "Top 5 melhores filmes por rating", // título
                "Filme (Ano)",                      // eixo X
                "Rating médio",                     // eixo Y
                dataset,                            // dados
                PlotOrientation.VERTICAL,
                false,                              // legenda
                true,                               // tooltips
                false                               // URLs
        );

        // Personalizar o gráfico
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(52, 152, 219)); // azul
        renderer.setBarPainter(new BarRenderer().getBarPainter());

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(800, 500));
        setContentPane(panel);

        setSize(850, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private DefaultCategoryDataset carregarDados() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try {
            criarConexao();

            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("""
                SELECT name, date, rating
                FROM movies
                WHERE rating > 0 AND date > 0
                ORDER BY rating DESC
                LIMIT 5
            """);

            while (r.next()) {
                String nome = r.getString("name");
                int ano = r.getInt("date");
                double media = r.getDouble("rating");

                String rotulo = nome + " (" + ano + ")";

                // linha = "Rating", coluna = filme(ano)
                dataset.addValue(media, "Rating", rotulo);

                System.out.println(rotulo + " -> " + media);
            }

            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }

    public void criarConexao() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:movies.db");
        c.setAutoCommit(false);
    }

    public static void main(String[] args) {
        new Top5FilmesBarra();
    }
}
