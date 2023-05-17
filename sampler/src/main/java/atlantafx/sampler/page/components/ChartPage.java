/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;

public final class ChartPage extends OutlinePage {

    public static final String NAME = "Chart";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create("https://openjfx.io/javadoc/20/javafx.controls/javafx/scene/chart/package-summary.html");
    }

    public ChartPage() {
        super();

        addPageHeader();
        addFormattedText("""
            JavaFX provides a set of chart components specifically designed \
            for data visualization. The charts include common types such as \
            Bar, Line, Area, Pie, Scatter, and Bubble charts."""
        );
        addSection("Area Chart", areaChart());
        addSection("Stacked Area Chart", stackedAreaChart());
        addSection("Bar Chart", barChart());
        addSection("Stacked Bar Chart", stackedBarChart());
        addSection("Bubble Chart", bubbleChart());
        addSection("Line Chart", lineChart());
        addSection("Pie Chart", pieChart());
        addSection("Scatter Chart", scatterChart());
    }

    @SuppressWarnings("unchecked")
    private ExampleBox areaChart() {
        //snippet_1:start
        var x = new NumberAxis(1, 31, 1);
        x.setLabel("Day");

        var y = new NumberAxis();
        y.setLabel("Temperature");

        var april = new XYChart.Series<Number, Number>();
        april.setName("April");
        IntStream.range(1, 30).forEach(i -> april.getData().add(
            new XYChart.Data<>(i, FAKER.random().nextInt(15, 30))
        ));

        var may = new XYChart.Series<Number, Number>();
        may.setName("May");
        IntStream.range(1, 30).forEach(i -> may.getData().add(
            new XYChart.Data<>(i, FAKER.random().nextInt(15, 30))
        ));

        var chart = new AreaChart<>(x, y);
        chart.setTitle("Temperature Monitoring");
        chart.setMinHeight(300);
        chart.getData().addAll(april, may);
        //snippet_1:end

        var description = BBCodeParser.createFormattedText("""
            Plots the area between the line that connects the data points and the axis. \
            Good for comparing cumulated totals over time.""");

        return new ExampleBox(chart, new Snippet(getClass(), 1), description);
    }

    @SuppressWarnings("unchecked")
    private ExampleBox stackedAreaChart() {
        //snippet_2:start
        var x = new NumberAxis(1, 31, 1);
        x.setLabel("Day");

        var y = new NumberAxis();
        y.setLabel("Temperature");

        var april = new XYChart.Series<Number, Number>();
        april.setName("April");
        IntStream.range(1, 30).forEach(i -> april.getData().add(
            new XYChart.Data<>(i, FAKER.random().nextInt(15, 30))
        ));

        var may = new XYChart.Series<Number, Number>();
        may.setName("May");
        IntStream.range(1, 30).forEach(i -> may.getData().add(
            new XYChart.Data<>(i, FAKER.random().nextInt(15, 30))
        ));

        var chart = new StackedAreaChart<>(x, y);
        chart.setTitle("Temperature Monitoring");
        chart.setMinHeight(300);
        chart.getData().addAll(april, may);
        //snippet_2:end

        var description = BBCodeParser.createFormattedText("""
            A variation of [i]AreaChart[/i] that displays trends of the contribution of each value."""
        );

        return new ExampleBox(chart, new Snippet(getClass(), 2), description);
    }

    @SuppressWarnings("unchecked")
    private ExampleBox barChart() {
        //snippet_3:start
        final var rnd = FAKER.random();
        final var countries = IntStream.range(0, 5).boxed()
            .map(i -> FAKER.country().countryCode3().toUpperCase())
            .toList();

        var x = new CategoryAxis();
        x.setLabel("Country");

        var y = new NumberAxis(0, 80, 10);
        y.setLabel("Value");

        var january = new XYChart.Series<String, Number>();
        january.setName("January");
        IntStream.range(0, countries.size()).forEach(i -> january.getData().add(
            new XYChart.Data<>(countries.get(i), rnd.nextInt(10, 80))
        ));

        var february = new XYChart.Series<String, Number>();
        february.setName("February");
        IntStream.range(0, countries.size()).forEach(i -> february.getData().add(
            new XYChart.Data<>(countries.get(i), rnd.nextInt(10, 80))
        ));

        var march = new XYChart.Series<String, Number>();
        march.setName("March");
        IntStream.range(0, countries.size()).forEach(i -> march.getData().add(
            new XYChart.Data<>(countries.get(i), rnd.nextInt(10, 80))
        ));

        var chart = new BarChart<>(x, y);
        chart.setTitle("Country Summary");
        chart.setMinHeight(300);
        chart.getData().addAll(january, february, march);
        //snippet_3:end

        var description = BBCodeParser.createFormattedText("""
            Plots rectangular bars with heights indicating data values they represent, \
            and corresponding to the categories they belongs to. Used for displaying \
            discontinuous or discrete data."""
        );

        return new ExampleBox(chart, new Snippet(getClass(), 3), description);
    }

    @SuppressWarnings("unchecked")
    private ExampleBox stackedBarChart() {
        //snippet_4:start
        final var rnd = FAKER.random();
        final var countries = IntStream.range(0, 5).boxed()
            .map(i -> FAKER.country().countryCode3().toUpperCase())
            .toList();

        var x = new CategoryAxis();
        x.setLabel("Country");

        var y = new NumberAxis(0, 80, 10);
        y.setLabel("Value");

        var january = new XYChart.Series<String, Number>();
        january.setName("January");
        IntStream.range(0, countries.size()).forEach(i -> january.getData().add(
            new XYChart.Data<>(countries.get(i), rnd.nextInt(10, 80))
        ));

        var february = new XYChart.Series<String, Number>();
        february.setName("February");
        IntStream.range(0, countries.size()).forEach(i -> february.getData().add(
            new XYChart.Data<>(countries.get(i), rnd.nextInt(10, 80))
        ));

        var march = new XYChart.Series<String, Number>();
        march.setName("March");
        IntStream.range(0, countries.size()).forEach(i -> march.getData().add(
            new XYChart.Data<>(countries.get(i), rnd.nextInt(10, 80))
        ));

        var chart = new StackedBarChart<>(x, y);
        chart.setTitle("Country Summary");
        chart.setMinHeight(300);
        chart.getData().addAll(january, february, march);
        //snippet_4:end

        var description = BBCodeParser.createFormattedText("""
            A variation of [i]BarChart[/i] that plots bars indicating data values for a category."""
        );

        return new ExampleBox(chart, new Snippet(getClass(), 4), description);
    }

    @SuppressWarnings("unchecked")
    private ExampleBox bubbleChart() {
        //snippet_5:start
        final var rnd = FAKER.random();

        var x = new NumberAxis(1, 53, 4);
        x.setLabel("Week");

        var y = new NumberAxis(0, 80, 10);
        y.setLabel("Product Budget");

        var series1 = new XYChart.Series<Number, Number>();
        series1.setName(FAKER.commerce().productName());
        IntStream.range(1, 10).forEach(i -> series1.getData().add(
            new XYChart.Data<>(
                rnd.nextInt(1, 53),
                rnd.nextInt(10, 80),
                rnd.nextDouble(1, 10)
            )
        ));

        var series2 = new XYChart.Series<Number, Number>();
        series2.setName(FAKER.commerce().productName());
        IntStream.range(1, 10).forEach(i -> series2.getData().add(
            new XYChart.Data<>(
                rnd.nextInt(1, 53),
                rnd.nextInt(10, 80),
                rnd.nextDouble(1, 10)
            )
        ));

        var chart = new BubbleChart<>(x, y);
        chart.setTitle("Budget Monitoring");
        chart.setMinHeight(300);
        chart.getData().addAll(series1, series2);
        //snippet_5:end

        var description = BBCodeParser.createFormattedText("""
            Plots bubbles for data points in a series. Each plotted entity depicts \
            three parameters in a 2D chart and hence a unique chart type."""
        );

        return new ExampleBox(chart, new Snippet(getClass(), 5), description);
    }

    @SuppressWarnings("unchecked")
    private ExampleBox lineChart() {
        //snippet_6:start
        final var rnd = FAKER.random();

        var x = new CategoryAxis();
        x.setLabel("Month");

        var y = new NumberAxis(0, 80, 10);
        y.setLabel("Value");

        var series1 = new XYChart.Series<String, Number>();
        series1.setName(FAKER.stock().nsdqSymbol());
        IntStream.range(1, 12).forEach(i -> series1.getData().add(
            new XYChart.Data<>(
                Month.of(i).getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                rnd.nextInt(10, 80)
            )
        ));

        var series2 = new XYChart.Series<String, Number>();
        series2.setName(FAKER.stock().nsdqSymbol());
        IntStream.range(1, 12).forEach(i -> series2.getData().add(
            new XYChart.Data<>(
                Month.of(i).getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                rnd.nextInt(10, 80)
            )
        ));

        var chart = new LineChart<>(x, y);
        chart.setTitle("Stock Monitoring");
        chart.setMinHeight(300);
        chart.getData().addAll(series1, series2);
        //snippet_6:end

        var description = BBCodeParser.createFormattedText("""
            Plots line between the data points in a series. Used usually to view data trends over time."""
        );

        return new ExampleBox(chart, new Snippet(getClass(), 6), description);
    }

    private ExampleBox pieChart() {
        //snippet_7:start
        final var rnd = FAKER.random();

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
            new PieChart.Data(FAKER.food().fruit(), rnd.nextInt(10, 30)),
            new PieChart.Data(FAKER.food().fruit(), rnd.nextInt(10, 30)),
            new PieChart.Data(FAKER.food().fruit(), rnd.nextInt(10, 30)),
            new PieChart.Data(FAKER.food().fruit(), rnd.nextInt(10, 30)),
            new PieChart.Data(FAKER.food().fruit(), rnd.nextInt(10, 30))
        );

        var chart = new PieChart(data);
        chart.setMinHeight(300);
        chart.setTitle("Imported Fruits");
        //snippet_7:end

        var description = BBCodeParser.createFormattedText("""
            Plots circular chart divided into segments with each segment representing a value \
            as a proportion of the total. It looks like a Pie and hence the name."""
        );

        return new ExampleBox(chart, new Snippet(getClass(), 7), description);
    }

    @SuppressWarnings("unchecked")
    private ExampleBox scatterChart() {
        //snippet_8:start
        final var rnd = FAKER.random();

        var x = new NumberAxis(0, 10, 1);
        x.setLabel("Age");

        var y = new NumberAxis(-100, 500, 100);
        y.setLabel("Returns to date");

        var series1 = new XYChart.Series<Number, Number>();
        series1.setName("Equities");
        IntStream.range(1, 10).forEach(i -> series1.getData().add(
            new XYChart.Data<>(
                rnd.nextDouble(0, 10),
                rnd.nextDouble(-100, 500)
            )
        ));

        var series2 = new XYChart.Series<Number, Number>();
        series2.setName("Mutual funds");
        IntStream.range(1, 10).forEach(i -> series2.getData().add(
            new XYChart.Data<>(
                rnd.nextDouble(0, 10),
                rnd.nextDouble(-100, 500)
            )
        ));

        var chart = new ScatterChart<>(x, y);
        chart.setTitle("Investment Overview");
        chart.setMinHeight(300);
        chart.getData().addAll(series1, series2);
        //snippet_8:end

        var description = BBCodeParser.createFormattedText("""
            Plots symbols for the data points in a series. This type of chart is useful in viewing \
            distribution of data and its correlation, if there is any clustering."""
        );

        return new ExampleBox(chart, new Snippet(getClass(), 8), description);
    }
}
