package edu.uwm.gwt.client;

import com.vaadin.shared.communication.ServerRpc;

public interface ClinicalTrialsPersistToServerRpc extends ServerRpc {
    void persistToServer();
}
