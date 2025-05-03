package com.newsalesbeatApp.customview;

import java.util.ArrayList;

public interface ChartAnimationListener {
    /**
     * Callback to let {@link com.db.chart.view.ChartView} know when to invalidate and present new data.
     *
     * @param data Chart data to be used in the next view invalidation.
     * @return True if {@link com.db.chart.view.ChartView} accepts the call, False otherwise.
     */
    boolean onAnimationUpdate(ArrayList<ChartSet> data);
}
