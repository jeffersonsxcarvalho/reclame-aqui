# Comandos cURL para testar a API de Reclamações

Estes são exemplos de comandos `curl` para interagir com a API de Reclamações.
Execute-os em seu terminal.

## 1. Criar uma nova reclamação

### Com título explícito

Este comando cria uma nova reclamação com um título definido. O corpo da requisição é um JSON com os detalhes.
O comando `-v` (verbose) é usado para exibir detalhes da requisição e da resposta.

```bash
curl -v -X POST http://localhost:8080/reclamacoes \
-H "Content-Type: application/json" \
-d '{
  "titulo": "Produto com defeito",
  "descricao": "Recebi o produto X com defeito de fabricação.",
  "autor": "Marcel Tanuri"
}'
```

### Com título gerado automaticamente

Este comando cria uma nova reclamação sem um título. A API irá gerar um título automaticamente usando um serviço externo (Bacon Ipsum).

```bash
curl -v -X POST http://localhost:8080/reclamacoes \
-H "Content-Type: application/json" \
-d '{
  "descricao": "A entrega está muito atrasada e não consigo rastrear o pedido.",
  "autor": "Cliente Ansioso"
}'
```

**Nota:** A resposta destes comandos incluirá o ID da reclamação recém-criada. Guarde esse ID para usar nos comandos seguintes.

## 2. Listar Reclamações

Este comando lista as reclamações cadastradas.

### Listagem simples

```bash
curl -v http://localhost:8080/reclamacoes
```

### Listagem com filtro

Filtra reclamações que contenham a palavra "produto" no título ou na descrição.

```bash
curl -v "http://localhost:8080/reclamacoes?filtro=produto"
```

### Listagem com paginação

Retorna a segunda página de resultados, com 2 itens por página.

```bash
curl -v "http://localhost:8080/reclamacoes?pagina=1&tamanhoPagina=2"
```

## 3. Buscar uma reclamação por ID

Substitua `<ID_DA_RECLAMACAO>` pelo ID da reclamação que você deseja buscar.

```bash
curl -v http://localhost:8080/reclamacoes/<ID_DA_RECLAMACAO>
```

## 4. Atualizar uma reclamação

Substitua `<ID_DA_RECLAMACAO>` pelo ID da reclamação que você deseja atualizar.

```bash
curl -v -X PUT http://localhost:8080/reclamacoes/<ID_DA_RECLAMACAO> \
-H "Content-Type: application/json" \
-d '{
  "titulo": "Produto com defeito - ATUALIZADO",
  "descricao": "Recebi o produto X com defeito de fabricação e a loja não quer trocar.",
  "autor": "Marcel Tanuri"
}'
```

## 5. Deletar uma reclamação

Substitua `<ID_DA_RECLAMACAO>` pelo ID da reclamação que você deseja deletar.

```bash
curl -v -X DELETE http://localhost:8080/reclamacoes/<ID_DA_RECLAMACAO>
```
