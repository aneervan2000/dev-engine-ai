package com.anee.projects.lovable_clone.dto.subscription;


public record UsageTodayResponse(
        int tokensUsed,
        int tokensLimit,
        int previewRunning,
        int previewsLimit
) {
}
