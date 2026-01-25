package com.anee.projects.lovable_clone.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


/**
 * <h6>Custom exception representing a bad request error.</h6>
 * message: Error message describing the bad request
 */
@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BadRequestException extends RuntimeException{
    String message;
}
