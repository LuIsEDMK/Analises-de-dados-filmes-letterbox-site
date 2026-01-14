import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;

public class GraficoGenerosMediaRating extends JFrame {

    private Connection c;

    public GraficoGenerosMediaRating() {
        super("Média de Ratings por Gênero (Letterboxd)");

        DefaultCategoryDataset dataset = carregarDataset();

        JFreeChart chart = ChartFactory.createBarChart(
                "Média de Ratings por Gênero",
                "Gênero",
                "Média do Rating",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        // Estilo
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(46, 204, 113)); // verde
        renderer.setBarPainter(new BarRenderer().getBarPainter());

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(1000, 600));

        add(panel);
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }


    private DefaultCategoryDataset carregarDataset() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();

        // gênero → soma das notas
        Map<String, Double> soma = new HashMap<>();

        // gênero → quantidade de filmes
        Map<String, Integer> contagem = new HashMap<>();

        try {
            criarConexao();

            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("""
                SELECT genres, rating
                FROM movies
                WHERE rating > 0 AND genres IS NOT NULL AND genres <> ''
            """);

            while (r.next()) {
                String linha = r.getString("genres");
                double nota = r.getDouble("rating");

                String[] generos = linha.split("\\|");

                for (String g : generos) {
                    g = g.trim();
                    if (g.isEmpty()) continue;

                    soma.put(g, soma.getOrDefault(g, 0.0) + nota);
                    contagem.put(g, contagem.getOrDefault(g, 0) + 1);
                }
            }

            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // calcular média e enviar ao dataset
        for (String g : soma.keySet()) {
            double media = soma.get(g) / contagem.get(g);
            ds.addValue(media, "Média", g);
        }

        return ds;
    }


    public void criarConexao() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:movies.db");
    }

    public static void main(String[] args) {
        new GraficoGenerosMediaRating();
    }
}
