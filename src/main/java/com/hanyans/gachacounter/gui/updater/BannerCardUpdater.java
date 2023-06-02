package com.hanyans.gachacounter.gui.updater;

import java.util.List;
import java.util.Objects;

import com.hanyans.gachacounter.model.count.BannerReport;

import javafx.scene.control.Label;


public class BannerCardUpdater {
    private static final String RATE_UP_NEXT_STYLECLASS = "rate-up-next";

    private final Label pityLabel4;
    private final Label pityLabel5;
    private final Label totalLabel;


    public BannerCardUpdater(Label pityLabel4, Label pityLabel5, Label totalLabel) {
        this.pityLabel4 = Objects.requireNonNull(pityLabel4);
        this.pityLabel5 = Objects.requireNonNull(pityLabel5);
        this.totalLabel = Objects.requireNonNull(totalLabel);
    }


    public void update(BannerReport report) {
        if (report.uids.size() > 1) {
            return;
        }

        long uid = Integer.MIN_VALUE;
        if (!report.uids.isEmpty()) {
            uid = List.copyOf(report.uids).get(0);
        }

        if (report.isRateUp4.getOrDefault(uid, true)) {
            pityLabel4.getStyleClass().remove(RATE_UP_NEXT_STYLECLASS);
        } else {
            pityLabel4.getStyleClass().add(RATE_UP_NEXT_STYLECLASS);
        }

        if (report.isRateUp5.getOrDefault(uid, true)) {
            pityLabel5.getStyleClass().remove(RATE_UP_NEXT_STYLECLASS);
        } else {
            pityLabel5.getStyleClass().add(RATE_UP_NEXT_STYLECLASS);
        }

        if (report.uids.isEmpty()) {
            setZero();
            return;
        }

        pityLabel4.setText(String.valueOf(report.pullSince4.get(uid)));
        pityLabel5.setText(String.valueOf(report.pullSince5.get(uid)));
        totalLabel.setText(String.valueOf(report.total));
    }


    private void setZero() {
        pityLabel4.setText("0");
        pityLabel5.setText("0");
        totalLabel.setText("0");
    }
}
