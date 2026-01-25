package com.anee.projects.lovable_clone.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * <h6>Custom exception representing a resource not found error.</h6>
 * resourceName: Name of the resource that was not found
 * resourceId: Identifier of the resource that was not found
 */

@Setter
@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ResourceNotFoundException extends RuntimeException{
    String resourceName;
    String resourceId;
}
