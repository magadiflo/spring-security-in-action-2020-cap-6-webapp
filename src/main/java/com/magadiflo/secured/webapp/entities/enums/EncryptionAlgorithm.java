package com.magadiflo.secured.webapp.entities.enums;

public enum EncryptionAlgorithm {
    BCRYPT("bcrypt"), SCRYPT("scrypt");

    private final String idForEncode;

    EncryptionAlgorithm(String idForEncode) {
        this.idForEncode = idForEncode;
    }

    public final String getIdForEncode() {
        return this.idForEncode;
    }
}
