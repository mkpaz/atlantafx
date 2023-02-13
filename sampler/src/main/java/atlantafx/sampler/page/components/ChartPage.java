/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class ChartPage extends AbstractPage {

    public static final String NAME = "Chart";

    @Override
    public String getName() {
        return NAME;
    }

    private final BorderPane chartWrapper = new BorderPane();
    private final ComboBox<Example> exampleSelect = new ComboBox<>();

    public ChartPage() {
        super();
        setUserContent(new VBox(
            new SampleBlock("Playground", createPlayground())
        ));
    }

    private VBox createPlayground() {
        exampleSelect.setMaxWidth(Double.MAX_VALUE);
        exampleSelect.getItems().setAll(Example.values());
        exampleSelect.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            if (val == null) {
                return;
            }

            Chart newChart = createChart(val);

            // copy existing properties to the new chart
            findDisplayedChart().ifPresent(chart -> newChart.setDisable(chart.isDisable()));

            chartWrapper.setCenter(newChart);
        });
        exampleSelect.setConverter(new StringConverter<>() {
            @Override
            public String toString(Example example) {
                return example == null ? "" : example.getName();
            }

            @Override
            public Example fromString(String s) {
                return Example.find(s);
            }
        });

        var disableToggle = new ToggleSwitch("Disable");
        disableToggle.selectedProperty().addListener((obs, old, val) -> findDisplayedChart().ifPresent(ch -> {
            if (val != null) {
                ch.setDisable(val);
            }
        }));

        var controls = new HBox(disableToggle);
        controls.setAlignment(Pos.CENTER);

        VBox playground = new VBox(SampleBlock.BLOCK_VGAP);
        playground.getChildren().setAll(
            new HBox(new Label("Select an example:"), new Spacer(), disableToggle),
            exampleSelect,
            chartWrapper
        );

        return playground;
    }

    @Override
    protected void onRendered() {
        super.onRendered();
        exampleSelect.getSelectionModel().selectFirst();
    }

    private Optional<Chart> findDisplayedChart() {
        return chartWrapper.getChildren().size() > 0
            ? Optional.of((Chart) chartWrapper.getChildren().get(0))
            : Optional.empty();
    }

    private Chart createChart(Example example) {
        switch (example) {
            case AREA_CHART -> {
                return areaChart(false);
            }
            case BAR_CHART -> {
                return barChart(false);
            }
            case BUBBLE_CHART -> {
                return bubbleChart();
            }
            case LINE_CHART -> {
                return lineChart();
            }
            case PIE_CHART -> {
                return pieChart();
            }
            case SCATTER_CHART -> {
                return scatterChart();
            }
            case STACKED_AREA_CHART -> {
                return areaChart(true);
            }
            case STACKED_BAR_CHART -> {
                return barChart(true);
            }
            default -> throw new IllegalArgumentException("Unexpected enum value: " + example);
        }
    }

    @SuppressWarnings("unchecked")
    private Chart areaChart(boolean stacked) {
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

        var chart = stacked ? new StackedAreaChart<>(x, y) : new AreaChart<>(x, y);
        chart.setTitle("Temperature Monitoring");
        chart.getData().addAll(april, may);

        return chart;
    }

    @SuppressWarnings("unchecked")
    private Chart barChart(boolean stacked) {
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

        var chart = stacked ? new StackedBarChart<>(x, y) : new BarChart<>(x, y);
        chart.setTitle("Country Summary");
        chart.getData().addAll(january, february, march);

        return chart;
    }

    @SuppressWarnings("unchecked")
    private Chart bubbleChart() {
        final var rnd = FAKER.random();

        var x = new NumberAxis(1, 53, 4);
        x.setLabel("Week");

        var y = new NumberAxis(0, 80, 10);
        y.setLabel("Product Budget");

        var series1 = new XYChart.Series<Number, Number>();
        series1.setName(FAKER.commerce().productName());
        IntStream.range(1, 10).forEach(i -> series1.getData().add(
            new XYChart.Data<>(rnd.nextInt(1, 53), rnd.nextInt(10, 80), rnd.nextDouble(1, 10))
        ));

        var series2 = new XYChart.Series<Number, Number>();
        series2.setName(FAKER.commerce().productName());
        IntStream.range(1, 10).forEach(i -> series2.getData().add(
            new XYChart.Data<>(rnd.nextInt(1, 53), rnd.nextInt(10, 80), rnd.nextDouble(1, 10))
        ));

        var chart = new BubbleChart<>(x, y);
        chart.setTitle("Budget Monitoring");
        chart.getData().addAll(series1, series2);

        return chart;
    }

    @SuppressWarnings("unchecked")
    private Chart lineChart() {
        final var rnd = FAKER.random();

        var x = new CategoryAxis();
        x.setLabel("Month");

        var y = new NumberAxis(0, 80, 10);
        y.setLabel("Value");

        var series1 = new XYChart.Series<String, Number>();
        series1.setName(FAKER.stock().nsdqSymbol());
        IntStream.range(1, 12).forEach(i -> series1.getData().add(
            new XYChart.Data<>(Month.of(i).getDisplayName(TextStyle.SHORT, Locale.getDefault()), rnd.nextInt(10, 80))
        ));

        var series2 = new XYChart.Series<String, Number>();
        series2.setName(FAKER.stock().nsdqSymbol());
        IntStream.range(1, 12).forEach(i -> series2.getData().add(
            new XYChart.Data<>(Month.of(i).getDisplayName(TextStyle.SHORT, Locale.getDefault()), rnd.nextInt(10, 80))
        ));

        var chart = new LineChart<>(x, y);
        chart.setTitle("Stock Monitoring");
        chart.getData().addAll(series1, series2);

        return chart;
    }

    private Chart pieChart() {
        final var rnd = FAKER.random();

        ObservableList<PieChart.Data> data = FXCollections.observableArrayList(
            new PieChart.Data(FAKER.food().fruit(), rnd.nextInt(10, 30)),
            new PieChart.Data(FAKER.food().fruit(), rnd.nextInt(10, 30)),
            new PieChart.Data(FAKER.food().fruit(), rnd.nextInt(10, 30)),
            new PieChart.Data(FAKER.food().fruit(), rnd.nextInt(10, 30)),
            new PieChart.Data(FAKER.food().fruit(), rnd.nextInt(10, 30))
        );

        var chart = new PieChart(data);
        chart.setTitle("Imported Fruits");

        return chart;
    }

    @SuppressWarnings("unchecked")
    private Chart scatterChart() {
        final var rnd = FAKER.random();

        var x = new NumberAxis(0, 10, 1);
        x.setLabel("Age");

        var y = new NumberAxis(-100, 500, 100);
        y.setLabel("Returns to date");

        var series1 = new XYChart.Series<Number, Number>();
        series1.setName("Equities");
        IntStream.range(1, 10).forEach(i -> series1.getData().add(
            new XYChart.Data<>(rnd.nextDouble(0, 10), rnd.nextDouble(-100, 500))
        ));

        var series2 = new XYChart.Series<Number, Number>();
        series2.setName("Mutual funds");
        IntStream.range(1, 10).forEach(i -> series2.getData().add(
            new XYChart.Data<>(rnd.nextDouble(0, 10), rnd.nextDouble(-100, 500))
        ));

        var chart = new ScatterChart<>(x, y);
        chart.setTitle("Investment Overview");
        chart.getData().addAll(series1, series2);

        return chart;
    }

    private enum Example {
        AREA_CHART("Area Chart"),
        BAR_CHART("Bar Chart"),
        BUBBLE_CHART("Bubble Chart"),
        LINE_CHART("Line Chart"),
        PIE_CHART("Pie Chart"),
        SCATTER_CHART("Scatter Chart"),
        STACKED_AREA_CHART("Stacked Area Chart"),
        STACKED_BAR_CHART("Stacked Bar Chart");

        private final String name;

        Example(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static Example find(String name) {
            return Arrays.stream(Example.values())
                .filter(example -> Objects.equals(example.getName(), name))
                .findFirst()
                .orElse(null);
        }
    }
}
