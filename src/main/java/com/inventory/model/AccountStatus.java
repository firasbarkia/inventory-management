package com.inventory.model;

/**
 * Represents the status of a user account.
 */
public enum AccountStatus {
    PENDING, // User registration is pending approval
    ACTIVE,  // User account is active and can be used
    INACTIVE // User account has been deactivated (optional)
}
