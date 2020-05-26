/*
 * Copyright 2006-2020 DLR, Germany
 * 
 * SPDX-License-Identifier: EPL-1.0
 * 
 * https://rcenvironment.de/
 */
 
package de.rcenvironment.core.component.execution.api;

import java.io.Serializable;

import de.rcenvironment.core.utils.common.StringUtils;

/**
 * Event type produced by components and workflows.
 *
 * @author Doreen Seider
 * @author Robert Mischke (tweaked notification setup)
 */
public interface ConsoleRow extends Comparable<ConsoleRow>, Serializable {

    /** Suffix used for publishing console notifications. */
    String NOTIFICATION_ID_PREFIX_CONSOLE_EVENT = "rce.component.console:";
    
    /** Type of the row. */
    enum Type {

        /** Tool standard out. */
        TOOL_OUT("TOOL OUT"),

        /** Tool standard error. */
        TOOL_ERROR("TOOL ERROR"),

        /** Component log output on info level. */
        COMPONENT_INFO("COMPONENT INFO"),
        
        /** Component log output on warn level. */
        COMPONENT_WARN("COMPONENT WARN"),
        
        /** Component log output on error level. */
        COMPONENT_ERROR("COMPONENT ERROR"),
        
        /** Workflow log output on error level. */
        WORKFLOW_ERROR("WORKFLOW ERROR"),

        /**
         * Lifecycle events of workflows and components. Semantically, they are related to ComponentState and WorkflowState. it is required
         * to deal with lifecycle events here as well, to reliably recognize very first and very last console row. In general, they are
         * generated by the workflow engine. Note: Actually, this type is not a console row as it was intended as ConsoleRow as a class are
         * introduced. In the future, we'll improve this probably by introducing a super type. ConsoleRow will be a child of it next to at
         * least one other for lifecycle events.
         */
        LIFE_CYCLE_EVENT("Life cycle event");
        
        private String displayName;
        
        Type(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }

    /** Type of the workflow lifecylce event. */
    enum WorkflowLifecyleEventType {

        /**
         * Sent if component is started.
         */
        COMPONENT_STARTING,

        /**
         * Sent if component is finished.
         */
        COMPONENT_TERMINATED,
        
        /**
         * Sent if component is paused.
         */
        COMPONENT_PAUSED,
        
        /**
         * Sent if component is resumed.
         */
        COMPONENT_RESUMED,
        
        /**
         * Sent if component has finished writing console log.
         */
        COMPONENT_LOG_FINISHED,

        /**
         * Sent at tool start.
         */
        TOOL_STARTING,
        
        /**
         * Sent at tool finish.
         */
        TOOL_FINISHED,
        
        /**
         * Sent at workflow start.
         */
        WORKFLOW_STARTING,

        /**
         * Sent at workflow finish.
         */
        WORKFLOW_FINISHED,
        
        /**
         * Sent if all of the workflow's components have finished writing console log.
         */
        WORKFLOW_LOG_FINISHED,

        /**
         * Sent when a new WorkflowState is entered; this enum's and the new state's string form are concatenated using
         * {@link StringUtils#escapeAndConcat(String...)}.
         */
        NEW_STATE;
    }

    /**
     * @return timestamp the {@link ConsoleRow} was produced
     */
    long getTimestamp();
    
    /**
     * @return index of the {@link ConsoleRow}. Console rows can be tagged with a running index which allows sorting even if the timestamp
     *         is equal
     */
    long getIndex();
    
    /**
     * @param index index of the {@link ConsoleRow}. Console rows can be tagged with a running index which allows sorting even if the
     *        timestamp is equal
     */
    void setIndex(long index);

    /**
     * @return execution identifier of the associated workflow
     */
    String getWorkflowIdentifier();

    /**
     * @return execution identifier of the associated component
     */
    String getComponentIdentifier();

    /**
     * @return instance name of the associated workflow
     */
    String getWorkflowName();

    /**
     * @return instance name of the associated component
     */
    String getComponentName();

    /**
     * @return type of the {@link ConsoleRow}
     */
    Type getType();

    /**
     * @return payload of the {@link ConsoleRow}
     */
    String getPayload();
    
    /**
     * @return component execution count
     */
    int getComponentRun();
    
    /**
     * @return sequence number (immutable) of the {@link ConsoleRow}
     */
    long getSequenzNumber();
    
}
