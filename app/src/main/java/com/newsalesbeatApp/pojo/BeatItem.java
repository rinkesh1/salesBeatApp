package com.newsalesbeatApp.pojo;

/*
 * Created by MTC on 25-07-2017.
 */

public class BeatItem {

    String beat_name, beat_id,beat_updated_at;
    boolean isMinimumCheckinRange;

    public String getBeatName() {
        return this.beat_name;
    }

    public void setBeatName(String beatName) {
        this.beat_name = beatName;
    }

    public String getBeatId() {
        return this.beat_id;
    }

    public void setBeatId(String beatId) {
        this.beat_id = beatId;
    }

    public String getBeatUpdatedAt() {
        return this.beat_updated_at;
    }

    public void setBeatUpdatedAt(String BeatUpdatedAt) {
        this.beat_updated_at = BeatUpdatedAt;
    }

    public boolean isMinimumCheckinRange() {
        return isMinimumCheckinRange;
    }

    public void setMinimumCheckinRange(boolean minimumCheckinRange) {
        isMinimumCheckinRange = minimumCheckinRange;
    }
}
