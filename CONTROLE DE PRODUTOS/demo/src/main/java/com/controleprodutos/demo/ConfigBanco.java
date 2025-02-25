package com.controleprodutos.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigBanco {
    private static final String PROPERTIES_FILE = "application.properties";
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    static {
        Properties prop = new Properties();
        try (InputStream input = ConfigBanco.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                throw new IOException("Arquivo " + PROPERTIES_FILE + " não encontrado nos resources.");
            }
            prop.load(input);
            URL = prop.getProperty("datasource.url");
            USER = prop.getProperty("datasource.user");
            PASSWORD = prop.getProperty("datasource.password");

            // Exibir os valores carregados (DEBUG)
            System.out.println("URL carregada: " + URL);
            System.out.println("Usuário carregado: " + USER);
        } catch (IOException e) {
            System.out.println("Erro ao carregar configurações do banco de dados.");
            e.printStackTrace();
        }
    }

    public static String getUrl() {
        return URL;
    }

    public static String getUser() {
        return USER;
    }

    public static String getPassword() {
        return PASSWORD;
    }
}
