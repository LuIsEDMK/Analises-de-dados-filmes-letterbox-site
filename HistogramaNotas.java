import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;

public class HistogramaNotas extends JFrame {

    private Connection c;

    public HistogramaNotas() {
        super("Histograma das Notas (Letterboxd)");

        double[] dados = carregarRatings();

        HistogramDataset dataset = new HistogramDataset();
        dataset.addSeries("Ratings", dados, 20);
        // 20 = número de intervalos ("bins") do histograma

        JFreeChart chart = ChartFactory.createHistogram(
                "Distribuição das Notas",
                "Nota",
                "Frequência",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // estilização (igual ao estilo do professor)
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.GRAY);

        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(52, 152, 219)); // azul
        renderer.setBarPainter(new StandardXYBarPainter()); // barras lisas

        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(800, 500));

        setContentPane(panel);

        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private double[] carregarRatings() {
        List<Double> lista = new ArrayList<>();

        try {
            criarConexao();

            Statement s = c.createStatement();
            ResultSet r = s.executeQuery("""
                SELECT rating
                FROM movies
                WHERE rating > 0
            """);

            while (r.next()) {
                lista.add(r.getDouble("rating"));
            }

            c.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // converte List<Double> → double[]
        double[] arr = new double[lista.size()];
        for (int i = 0; i < lista.size(); i++) {
            arr[i] = lista.get(i);
        }

        return arr;
    }

    public void criarConexao() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        c = DriverManager.getConnection("jdbc:sqlite:movies.db");
        c.setAutoCommit(false);
    }

    public static void main(String[] args) {
        new HistogramaNotas();
    }
}
