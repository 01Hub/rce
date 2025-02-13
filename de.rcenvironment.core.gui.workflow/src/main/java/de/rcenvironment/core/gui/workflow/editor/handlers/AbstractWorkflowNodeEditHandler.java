/*
 * Copyright 2006-2021 DLR, Germany
 * 
 * SPDX-License-Identifier: EPL-1.0
 * 
 * https://rcenvironment.de/
 */
 
package de.rcenvironment.core.gui.workflow.editor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

import de.rcenvironment.core.gui.workflow.editor.WorkflowEditor;


/**
 * Contains common logic of component handlers.
 *
 * @author Doreen Seider
 */
public abstract class AbstractWorkflowNodeEditHandler extends AbstractHandler {

    protected GraphicalViewer viewer;
    
    protected WorkflowEditor editor;
    
    protected CommandStack commandStack;
    
    protected ExecutionEvent event;

    @Override
    public Object execute(ExecutionEvent e) throws ExecutionException {
        final IWorkbenchPart activePart = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().getActivePart();
        if (activePart instanceof WorkflowEditor) {
            editor = (WorkflowEditor) activePart;            
            viewer = editor.getViewer();
            commandStack = (CommandStack) editor.getAdapter(CommandStack.class);
            event = e;
            edit();
        }
        return null;
    }
    
    abstract void edit();
}
