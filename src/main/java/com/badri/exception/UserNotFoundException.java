package com.badri.exception;

public class UserNotFoundException extends RuntimeException{

    private String resourceName;
    private String fieldName;
    private Long fieldValue;

    public UserNotFoundException(String resourceName, String fieldName, Long fieldValue) {
        super(String.format(" %s not found with %s : '%s'",resourceName,fieldName,fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
