package com.anee.projects.lovable_clone.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Plan {

    Long id;

    String name;

    String stripePriceId;
    Integer maxProjects;
    Integer maxTokensPerDay;
    Integer maxPreviews;
    Boolean unlimitedAi; // unlimited access to LLM, ignore maxTokensPerDaay if true

    Boolean active;
}
