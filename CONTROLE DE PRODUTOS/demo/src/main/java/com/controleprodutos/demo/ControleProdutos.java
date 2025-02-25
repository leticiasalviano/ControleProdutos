package com.controleprodutos.demo;


import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SuppressWarnings("unused")
@SpringBootApplication
public class ControleProdutos {
	private static final String URL = ConfigBanco.getUrl();
    private static final String USER = ConfigBanco.getUser();
    private static final String PASSWORD = ConfigBanco.getPassword();

    private static Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
        System.out.println("URL do banco: " + ConfigBanco.getUrl());

		try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Carregar o driver manualmente
        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC não encontrado: " + e.getMessage());
            e.printStackTrace();
        }

        int opcao;
        do {
            System.out.println("\n=== Controle de Produtos ===");
            System.out.println("1. Adicionar Produto");
            System.out.println("2. Listar Produtos");
            System.out.println("3. Buscar Produto por Nome");
            System.out.println("4. Buscar Produto por Código de Barras");
            System.out.println("5. Remover Produto");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    adicionarProduto();
                    break;
                case 2:
                    listarProdutos();
                    break;
                case 3:
                    buscarProduto();
                    break;
                case 4:
                    buscarProdutoPorCodigoBarras();
                    break;
                case 5:
                    removerProduto();
                    break;
                case 6:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 5);
    }

    private static void adicionarProduto() {
        System.out.println("Código de Barras: ");
        String codigoBarras = scanner.nextLine();
        System.out.print("Nome do produto: ");
        String nome = scanner.nextLine();
        System.out.print("Preço do produto: ");
        double preco = scanner.nextDouble();
        System.out.print("Quantidade do produto: ");
        int quantidade = scanner.nextInt();
        scanner.nextLine();

        String sql = "INSERT INTO produtos (nome, preco, quantidade) VALUES (?, ?, ?)"; // Alterado para corresponder
                                                                                        // aos nomes das colunas

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigoBarras);
            stmt.setString(1, nome);
            stmt.setDouble(2, preco);
            stmt.setInt(3, quantidade);
            stmt.executeUpdate();
            System.out.println("Produto adicionado com sucesso!");
        } catch (SQLException e) {
            System.out.println("Erro ao adicionar produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void listarProdutos() {
        String sql = "SELECT * FROM produtos";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.isBeforeFirst()) {
                System.out.println("Nenhum produto cadastrado.");
                return;
            }

            System.out.println("\nLista de Produtos:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Código de Barras: " + rs.getString("codigo_barras") +
                        ", Nome: " + rs.getString("nome") +
                        ", Preço: R$" + rs.getDouble("preco") +
                        ", Quantidade: " + rs.getInt("quantidade"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void buscarProduto() {
        System.out.print("Digite o nome do produto para buscar: ");
        String nomeBusca = scanner.nextLine();

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM produtos WHERE nome LIKE ?")) {

            stmt.setString(1, "%" + nomeBusca + "%"); // Corrigido: LIKE precisa do "?"
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) { // Verifica se há resultados
                System.out.println("Produto não encontrado.");
                return;
            }

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Nome: " + rs.getString("nome") +
                        ", Preço: R$" + rs.getDouble("preco") +
                        ", Quantidade: " + rs.getInt("quantidade"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void buscarProdutoPorCodigoBarras() {
        System.out.print("Digite o código de barras do produto: ");
        String codigoBarras = scanner.nextLine();

        String sql = "SELECT * FROM produtos WHERE codigo_barras = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoBarras);
            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("Produto não encontrado.");
                return;
            }

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Código de Barras: " + rs.getString("codigo_barras") +
                        ", Nome: " + rs.getString("nome") +
                        ", Preço: R$" + rs.getDouble("preco") +
                        ", Quantidade: " + rs.getInt("quantidade"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void removerProduto() {
        System.out.print("Digite o ID do produto que deseja remover: ");
        int idProduto = scanner.nextInt();
        scanner.nextLine(); // Consumir a quebra de linha

        String sqlVerificar = "SELECT * FROM produtos WHERE id = ?";
        String sqlDeletar = "DELETE FROM produtos WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement stmtVerificar = conn.prepareStatement(sqlVerificar);
                PreparedStatement stmtDeletar = conn.prepareStatement(sqlDeletar)) {

            // Verifica se o produto existe
            stmtVerificar.setInt(1, idProduto);
            ResultSet rs = stmtVerificar.executeQuery();

            if (!rs.next()) { // Se não encontrar o produto
                System.out.println("Produto não encontrado.");
                return;
            }

            // Exclui o produto
            stmtDeletar.setInt(1, idProduto);
            int linhasAfetadas = stmtDeletar.executeUpdate();

            if (linhasAfetadas > 0) {
                System.out.println("Produto removido com sucesso!");
            } else {
                System.out.println("Erro ao remover o produto.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao remover produto: " + e.getMessage());
            e.printStackTrace();
        }

      

    }


}



