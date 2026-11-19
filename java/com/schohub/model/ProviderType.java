package com.schohub.model;

public enum ProviderType {
    LOCAL_GOVERNMENT("Local Government"),
    PROVINCIAL_GOVERNMENT("Provincial Government"),
    UNIVERSITY("University / Institutional"),
    PRIVATE_ORGANIZATION("Private Organization");

    private final String label;

    ProviderType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
