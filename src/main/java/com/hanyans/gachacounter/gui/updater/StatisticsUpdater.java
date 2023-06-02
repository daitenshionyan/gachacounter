package com.hanyans.gachacounter.gui.updater;

import java.util.Collection;

import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;


public class StatisticsUpdater {
    private final StackedBarChart<String, Number> stats5NormGraph;
    private final StackedBarChart<String, Number> stats5WeapGraph;
    private final StackedBarChart<String, Number> stats4Graph;


    public StatisticsUpdater(
                StackedBarChart<String, Number> stats5NormGraph,
                StackedBarChart<String, Number> stats5WeapGraph,
                StackedBarChart<String, Number> stats4Graph) {
        this.stats5NormGraph = stats5NormGraph;
        this.stats5WeapGraph = stats5WeapGraph;
        this.stats4Graph = stats4Graph;
    }


    public void update(Arguments args) {
        updateGraph(stats5NormGraph, args.data5Norm);
        updateGraph(stats5WeapGraph, args.data5Weap);
        updateGraph(stats4Graph, args.data4);
    }


    private void updateGraph(StackedBarChart<String, Number> graph, PlotData plotData) {
        // set data
        graph.getData().clear();
        graph.getData().addAll(plotData.series);

        // set axis range
        NumberAxis yAxis = (NumberAxis) graph.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setTickUnit(plotData.step);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(plotData.max);

        // add tooltip
        plotData.series.stream()
                .forEach(series ->
                    series.getData().forEach(data -> {
                        Object exVal = data.getExtraValue();
                        if (exVal == null) {
                            return;
                        }
                        Node node = data.getNode();
                        if (node == null) {
                            return;
                        }
                        Tooltip tooltip = new Tooltip();
                        tooltip.setText(exVal.toString());
                        tooltip.setShowDelay(Duration.millis(50));
                        Tooltip.install(node, tooltip);
                    })
                );
    }





    public static class Arguments {
        public final PlotData data5Norm;
        public final PlotData data5Weap;
        public final PlotData data4;


        public Arguments(
                    PlotData data5Norm,
                    PlotData data5Weap,
                    PlotData data4) {
            this.data5Norm = data5Norm;
            this.data5Weap = data5Weap;
            this.data4 = data4;
        }
    }





    public static class PlotData {
        public final Collection<Series<String, Number>> series;
        public final int step;
        public final int max;


        public PlotData(Collection<Series<String, Number>> series, int freqStep, int freqMax) {
            this.series = series;
            this.step = freqStep;
            this.max = freqMax;
        }
    }
}
