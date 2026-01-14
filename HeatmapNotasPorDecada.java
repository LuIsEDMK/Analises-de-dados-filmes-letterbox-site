import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYBlockRenderer;
import org.jfree.data.xy.DefaultXYZDataset;

public class HeatmapNotasPorDecada extends JFrame {

    private Connection c;

    public HeatmapNotasPorDecada() {
        super("Heatmap de Ratings por Década (1900+)");

        DefaultXYZDataset dataset = carregarDados();

        NumberAxis xAxis = new NumberAxis("Década");
        xAxis.setLowerBound(1900);  // COMEÇA EM 1900
        xAxis.setUpperBound(2030);  // limite superior
        xAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(10)); // ticks a cada década

        NumberAxis yAxis = new NumberAxis("Rating (faixas 0.5)");
        yAxis.setLowerBound(0.0);
        yAxis.setUpperBound(5.0);
        yAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(0.5));

        XYBlockRenderer renderer = new XYBlockRenderer();

        LookupPaintScale paintScale = new LookupPaintScale(0, 200, Color.WHITE);
        paintScale.add(10, new Color(200, 255, 200));
        paintScale.add(30, new Color(150, 220, 150));
        paintScale.add(60, new Color(100, 200, 100));
        paintScale.add(100, new Color(60, 150, 60));
        paintScale.add(150, new Color(30, 100, 30));
        paintScale.add(200, new Color(0, 60, 0));

        renderer.setPaintScale(paintScale);
        renderer.setBlockWidth(10);    // cada década = um bloco de largura
        renderer.setBlockHeight(0.5);  // cada faixa de rating = linha

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);

        JFreeChart chart = new JFreeChart(
                "Heatmap de Ratings por Década (somente anos ≥ 1900)",
                JFreeChart.DEFAULT_TITLE_FONT,
                plot,
                false
        );

        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);

        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private DefaultXYZDataset carregarDados() {
        DefaultXYZDataset dataset = new DefaultXYZDataset();
        Map<String, Integer> mapa = new HashMap<>();

        try {
            criarConexao();

            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("""
                SELECT date, rating
                FROM movies
                WHERE date >= 1900 AND rating > 0
            """);

            while (r.next()) {
                int ano = r.getInt("date");
                double rating = r.getDouble("rating");

                int decada = (ano / 10) * 10;
                double faixa = Math.round(rating * 2) / 2.0;

                String chave = decada + "-" + faixa;
                mapa.put(chave, mapa.getOrDefault(chave, 0) + 1);
            }

            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        double[][] data = new double[3][mapa.size()];
        int i = 0;

        for (String chave : mapa.keySet()) {
            String[] parts = chave.split("-");
            double decada = Double.parseDouble(parts[0]);
            double faixa = Double.parseDouble(parts[1]);
            double valor = mapa.get(chave);

            data[0][i] = decada;
            data[1][i] = faixa;
            data[2][i] = valor;
            i++;
        }

        dataset.addSeries("Heatmap", data);

        return dataset;
    }

    public void criarConexao() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:movies.db");
    }

    public static void main(String[] args) {
        new HeatmapNotasPorDecada();
    }
}
