package com.vsa.ecommerce.common.id;

/**
 * Interface for generating unique identifiers.
 */
public interface IdGenerator {

    /**
     * Generate the next unique ID.
     * 
     * @return unique long ID
     */
    long nextId();
}
