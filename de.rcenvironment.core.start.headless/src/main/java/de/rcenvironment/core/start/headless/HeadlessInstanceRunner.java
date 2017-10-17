/*
 * Copyright (C) 2006-2016 DLR, Germany
 * 
 * All rights reserved
 * 
 * http://www.rcenvironment.de/
 */

package de.rcenvironment.core.start.headless;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.equinox.app.IApplication;

import de.rcenvironment.core.command.api.CommandExecutionResult;
import de.rcenvironment.core.configuration.CommandLineArguments;
import de.rcenvironment.core.configuration.bootstrap.ui.ErrorTextUI;
import de.rcenvironment.core.embedded.ssh.api.SshAccountConfigurationService;
import de.rcenvironment.core.mail.SMTPServerConfigurationService;
import de.rcenvironment.core.start.common.Instance;
import de.rcenvironment.core.start.common.InstanceRunner;
import de.rcenvironment.core.start.common.validation.api.InstanceValidationResult;
import de.rcenvironment.core.start.common.validation.api.InstanceValidationResult.InstanceValidationResultType;
import de.rcenvironment.core.start.headless.textui.ConfigurationTextUI;
import de.rcenvironment.core.start.headless.textui.QuestionDialogTextUI;
import de.rcenvironment.core.utils.incubator.ServiceRegistry;
import de.rcenvironment.core.utils.incubator.ServiceRegistryAccess;

/**
 * Start class for headless run.
 * 
 * @author Sascha Zur
 * @author Robert Mischke
 * @author Doreen Seider
 */
public final class HeadlessInstanceRunner extends InstanceRunner {

    private AtomicInteger exitCode = new AtomicInteger(IApplication.EXIT_OK);

    /**
     * Runs the RCE instance in headless mode.
     * 
     * @return exit code
     * @throws InterruptedException if waiting for the RCE instance to shut down is interrupted
     */
    @Override
    public int performRun() throws InterruptedException {

        String[] execCommandTokens = CommandLineArguments.getExecCommandTokens();
        Future<CommandExecutionResult> commandExecutionFuture = null;
        if (execCommandTokens != null) {
            final boolean isBatchMode = CommandLineArguments.isBatchModeRequested();
            String cliToken;
            if (isBatchMode) {
                cliToken = CommandLineArguments.BATCH_OPTION_TOKEN;
            } else {
                cliToken = CommandLineArguments.EXEC_OPTION_TOKEN;
            }
            commandExecutionFuture = initiateAsyncCommandExecution(execCommandTokens,
                "execution of " + cliToken + " commands", isBatchMode);
        }

        if (CommandLineArguments.isConfigurationShellRequested()) {
            log.debug("Running text-mode configuration UI");
            final ServiceRegistryAccess serviceAccess = ServiceRegistry.createAccessFor(this);
            final SshAccountConfigurationService sshConfigurationService = serviceAccess.getService(SshAccountConfigurationService.class);
            final SMTPServerConfigurationService smtpServerConfigurationService =
                serviceAccess.getService(SMTPServerConfigurationService.class);
            new ConfigurationTextUI(sshConfigurationService, smtpServerConfigurationService).run();
            log.debug("Shutting down after text-mode configuration UI has terminated");
        } else if (CommandLineArguments.isBatchModeRequested()) {
            if (commandExecutionFuture != null) {
                try {
                    commandExecutionFuture.get();
                } catch (ExecutionException e) {
                    log.error("Uncaught error in batch command execution", e);
                }
            } else {
                // Future could be null if the command service is unavailable
                log.error("Failed to initialize batch command execution");
            }
        } else {
            Instance.awaitShutdown();
        }

        return exitCode.get();
    }

    @Override
    public boolean onInstanceValidationFailures(Map<InstanceValidationResultType, List<InstanceValidationResult>> validationResults) {

        if (validationResults.get(InstanceValidationResultType.FAILED_SHUTDOWN_REQUIRED).size() > 0) {
            InstanceValidationResult result = validationResults.get(InstanceValidationResultType.FAILED_SHUTDOWN_REQUIRED).get(0);
            new ErrorTextUI("Instance validation failure: " + result.getGuiDialogMessage() + "\n\nRCE will be shut down.").run();
            return false;
        }

        if (validationResults.get(InstanceValidationResultType.FAILED_PROCEEDING_ALLOWED).size() > 0) {
            for (InstanceValidationResult result : validationResults.get(InstanceValidationResultType.FAILED_PROCEEDING_ALLOWED)) {
                if (!new QuestionDialogTextUI("Instance validation failure",
                    result.getGuiDialogMessage() + "\n\nDo you like to proceed anyway?").run()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void triggerRestart() {
        setExitCode(IApplication.EXIT_RESTART);
        Instance.shutdown();
    }

    private void setExitCode(int newExitCode) {
        exitCode.set(newExitCode);
    }
}
