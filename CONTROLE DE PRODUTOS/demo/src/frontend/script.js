// Função para adicionar um produto
function adicionarProduto() {
    let nome = document.getElementById("nome").value;
    let preco = document.getElementById("preco").value;
    let quantidade = document.getElementById("quantidade").value;

    fetch("http://localhost:8080/api/produtos", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nome, preco, quantidade })
    })
    .then(response => response.json())
    .then(data => {
        alert("Produto adicionado com sucesso!");
        listarProdutos();
    })
    .catch(error => console.error("Erro:", error));
}

// Função para listar os produtos
function listarProdutos() {
    fetch("http://localhost:8080/api/produtos")
    .then(response => response.json())
    .then(produtos => {
        let tabela = document.getElementById("tabelaProdutos");
        tabela.innerHTML = "";
        
        produtos.forEach(produto => {
            let linha = `<tr>
                <td>${produto.id}</td>
                <td>${produto.nome}</td>
                <td>R$ ${produto.preco}</td>
                <td>${produto.quantidade}</td>
            </tr>`;
            tabela.innerHTML += linha;
        });
    })
    .catch(error => console.error("Erro:", error));
}

// Função para buscar um produto pelo nome
function buscarProduto() {
    let nome = document.getElementById("busca").value;

    fetch(`http://localhost:8080/api/produtos?nome=${nome}`)
    .then(response => response.json())
    .then(produtos => {
        let tabela = document.getElementById("tabelaProdutos");
        tabela.innerHTML = "";

        produtos.forEach(produto => {
            let linha = `<tr>
                <td>${produto.id}</td>
                <td>${produto.nome}</td>
                <td>R$ ${produto.preco}</td>
                <td>${produto.quantidade}</td>
            </tr>`;
            tabela.innerHTML += linha;
        });

        if (produtos.length === 0) {
            alert("Nenhum produto encontrado.");
        }
    })
    .catch(error => console.error("Erro:", error));
}
