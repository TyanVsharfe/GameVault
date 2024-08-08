package com.gamevault.data_template;

public class UserStatisticsInfo {
    private long totalGames;
    private long completedGames;
    private long playingGames;
    private long plannedGames;
    private long abandonedGames;
    private long noneStatusGames;

    public long getTotalGames() {
        return totalGames;
    }

    public void setTotalGames(long totalGames) {
        this.totalGames = totalGames;
    }

    public long getCompletedGames() {
        return completedGames;
    }

    public void setCompletedGames(long completedGames) {
        this.completedGames = completedGames;
    }

    public long getPlannedGames() {
        return plannedGames;
    }

    public void setPlannedGames(long plannedGames) {
        this.plannedGames = plannedGames;
    }

    public long getPlayingGames() {
        return playingGames;
    }

    public void setPlayingGames(long playingGames) {
        this.playingGames = playingGames;
    }

    public long getNoneStatusGames() {
        return noneStatusGames;
    }

    public void setNoneStatusGames(long noneStatusGames) {
        this.noneStatusGames = noneStatusGames;
    }

    public long getAbandonedGames() {
        return abandonedGames;
    }

    public void setAbandonedGames(long abandonedGames) {
        this.abandonedGames = abandonedGames;
    }
}
