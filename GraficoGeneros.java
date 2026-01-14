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

public class GraficoGeneros extends JFrame {

    private Connection c;

    public GraficoGeneros() {
        super("Top Gêneros (Letterboxd)");

        DefaultCategoryDataset dataset = carregarDataset();

        JFreeChart chart = ChartFactory.createBarChart(
                "Quantidade de Filmes por Gênero",
                "Gênero",
                "Quantidade",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        // Personalização do estilo
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(52, 152, 219));  // azul
        renderer.setBarPainter(new BarRenderer().getBarPainter());

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(900, 600));

        add(panel);
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }


    private DefaultCategoryDataset carregarDataset() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        Map<String, Integer> mapa = new HashMap<>();

        try {
            criarConexao();

            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("""
                SELECT genres FROM movies
                WHERE genres IS NOT NULL AND genres <> ''
            """);

            while (r.next()) {
                String linha = r.getString("genres");

                // separar os gêneros
                String[] generos = linha.split("\\|");

                for (String g : generos) {
                    g = g.trim();
                    if (g.isEmpty()) continue;

                    mapa.put(g, mapa.getOrDefault(g, 0) + 1);
                }
            }

            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // adicionar ao dataset
        for (String g : mapa.keySet()) {
            ds.addValue(mapa.get(g), "Filmes", g);
        }

        return ds;
    }


    public void criarConexao() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:movies.db");
    }

    public static void main(String[] args) {
        new GraficoGeneros();
    }
}
