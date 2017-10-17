/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.toolkit.modules.introspection.api;

/**
 * A service for registering {@link StatusCollectionContributor}s at. As contributors are intended to provide their state until shutdown, no
 * unregistration method is provided (or intended).
 * 
 * @author Robert Mischke
 */
public interface StatusCollectionRegistry {

    /**
     * Registers a new {@link StatusCollectionContributor}.
     * 
     * @param contributor the contributor to add
     */
    void addContributor(StatusCollectionContributor contributor);
}
