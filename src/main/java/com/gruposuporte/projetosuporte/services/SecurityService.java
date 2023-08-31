package com.gruposuporte.projetosuporte.services;

public interface SecurityService {
    boolean isAuthenticated();
    void autoLogin(String username, String password);
}
