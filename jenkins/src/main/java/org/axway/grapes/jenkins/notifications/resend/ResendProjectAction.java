package org.axway.grapes.jenkins.notifications.resend;

import hudson.model.Action;
import org.axway.grapes.jenkins.GrapesPlugin;
import org.axway.grapes.jenkins.config.GrapesConfig;
import org.axway.grapes.utils.client.GrapesClient;

import java.util.*;
import java.util.logging.Level;

/**
 * Resend Project Action
 *
 * <p>Gathers all the resend action to perform on a project.
 * This action will not be displayed in the project view because notification resend are performed from the administration panel.</p>
 *
 * @author jdcoffre
 */
public class ResendProjectAction implements Action {

    private final List<ResendBuildAction> resendBuildActions;
    private final GrapesConfig config;

    public ResendProjectAction(final List<ResendBuildAction> resendBuildActions, final GrapesConfig config) {
        this.resendBuildActions = resendBuildActions;
        this.config = config;
    }

    // Hide the build action
    public String getIconFileName() {
        return null;
    }

    // Hide the build action
    public String getDisplayName() {
        return null;
    }

    // Hide the build action
    public String getUrlName() {
        return null;
    }

    /**
     * Mandatory trick. ResendProjectAction are updated at each build.
     * If the notification has been resent by the admin panel, it will be taken into account only after the next time the build run.
     * To avoid resending again and again the notification from the admin panel until the next build, we check that the reports have not been sent.
     *
     * @return List<ResendBuildAction>
     */
    public List<ResendBuildAction> getResendBuildActions() {
        final List<ResendBuildAction> updatedActions = new ArrayList<ResendBuildAction>();
        try {
            for (ResendBuildAction action: resendBuildActions) {
                if (action.toSend()) {
                    updatedActions.add(action);
                }
            }
        } catch (Exception e){
            GrapesPlugin.getLogger().log(Level.INFO,"[GRAPES] Failed update resend action list: ", e);
        }
        resendBuildActions.clear();
        resendBuildActions.addAll(updatedActions);

        return resendBuildActions;
    }

    /**
     * Re-launch all the notification to the configured server
     */
    public void perform() {
        final GrapesClient client = new GrapesClient(config.getHost(), String.valueOf(config.getPort()));

        if (client.isServerAvailable()) {
            String user = null, password = null;

            if (config.getPublisherCredentials() != null) {
                user = config.getPublisherCredentials().getUsername();
                password = config.getPublisherCredentials().getPassword();
            }

            for (ResendBuildAction resendAction : resendBuildActions) {
                try {
                    client.postModule(resendAction.getModule(), user, password);
                    resendAction.discard();
                } catch (Exception e) {
                    GrapesPlugin.getLogger().log(Level.SEVERE,
                            "[GRAPES] Failed perform resend action of " + resendAction.getModuleName() +
                                    " in version " + resendAction.getModuleVersion(), e);
                }
            }

        }

    }

    /**
     * Returns the lis
     * @return
     */
    public Map<String, String> getModulesInfo() {
        final Map<String, String> modulesInfo = new HashMap<String, String>();

        for(ResendBuildAction action: getResendBuildActions()){
            modulesInfo.put(action.getModuleName(), action.getModuleVersion());
        }
        return modulesInfo;
    }
}
