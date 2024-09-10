package com.parsingVAST.Model;

import lombok.Data;
import java.util.List;

public class Creative {
    private String duration;
    private List<CompanionBanner> companionBanners;

    // Getters and Setters

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<CompanionBanner> getCompanionBanners() {
        return companionBanners;
    }

    public void setCompanionBanners(List<CompanionBanner> companionBanners) {
        this.companionBanners = companionBanners;
    }
}
