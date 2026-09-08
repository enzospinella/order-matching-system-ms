# Order Matching Engine

Matching engine de um único ativo, implementada em Java para simular um livro de ofertas (*order book*) com prioridade de preço e tempo.

## Funcionalidades

- Ordens limitadas (*limit orders*).
- Ordens a mercado (*market orders*).
- Ordens pegged vinculadas ao melhor bid ou offer disponível.
- Execução automática de ordens que cruzam o spread.
- Cancelamento de ordens pelo ID.
- Alteração de preço, quantidade ou ambos.
- Exibição do livro de ofertas.
- Filas FIFO dentro de cada nível de preço.

## Requisitos

- Java Development Kit (JDK) 8 ou superior.
- Terminal compatível com comandos Java.

Verifique a instalação com:

```bash
java -version
javac -version
```

## Execução

Na raiz do projeto, compile os arquivos:

```bash
javac *.java
```

Depois, inicie a aplicação:

```bash
java Main
```

O programa exibirá um prompt `>>>`. Digite um comando por linha. Para encerrar, use:

```text
exit
```

## Comandos

### Criar ordem limitada

```text
limit <buy|sell> <preço> <quantidade>
```

Exemplos:

```text
limit buy 100.50 10
limit sell 101.00 5
```

O programa exibirá o ID gerado, por exemplo:

```text
Order created: buy 10 @ 100.5 id_1
```

O preço deve ser maior que zero e a quantidade deve ser positiva.

### Criar ordem a mercado

```text
market <buy|sell> <quantidade>
```

Exemplos:

```text
market buy 8
market sell 3
```

Uma ordem a mercado é executada contra os melhores preços disponíveis. Ela não é adicionada ao livro caso reste quantidade não executada.

### Criar ordem pegged

Para acompanhar o melhor bid:

```text
peg bid buy <quantidade>
```

Para acompanhar o melhor offer:

```text
peg offer sell <quantidade>
```

Exemplos:

```text
peg bid buy 20
peg offer sell 15
```

Uma ordem `peg bid` usa o maior preço de compra disponível no livro. Uma ordem `peg offer` usa o maior preço de venda disponível. Se não houver referência no lado correspondente, a ordem é rejeitada com uma mensagem como:

```text
Error: No reference bid available for peg order.
```

Quando o preço de referência muda, a ordem pegged é reposicionada no novo nível de preço e perde a prioridade de tempo anterior.

### Exibir o livro

```text
print book
```

O livro é exibido com as compras em ordem decrescente de preço e as vendas em ordem crescente de preço:

```text
Ordens de Compra| Ordens de Venda
-----------------|-----------------
10 @ 100.5      | 5 @ 101.0
```

### Cancelar ordem

```text
cancel order <id>
```

Exemplo:

```text
cancel order id_1
```

O ID precisa pertencer a uma ordem que ainda esteja registrada no livro.

### Alterar quantidade

```text
alter order qty <id> <nova-quantidade>
```

Exemplo:

```text
alter order qty id_2 25
```

### Alterar preço

```text
alter order price <id> <novo-preço>
```

Exemplo:

```text
alter order price id_2 102.25
```

### Alterar preço e quantidade

```text
alter order <id> <novo-preço> <nova-quantidade>
```

Exemplo:

```text
alter order id_2 102.25 25
```

Toda alteração remove a ordem da fila atual e a reinsere no final da fila do novo nível de preço. A alteração preserva o ID da ordem.

## Exemplo completo

Uma sessão simples pode ser executada assim:

```text
>>> limit buy 100.00 10
Order created: buy 10 @ 100.0 id_1
>>> limit sell 102.00 5
Order created: sell 5 @ 102.0 id_2
>>> print book
Ordens de Compra| Ordens de Venda
-----------------|-----------------
10 @ 100.0      | 5 @ 102.0
>>> market sell 4
Trade, price: 100.0, qty: 4
>>> alter order price id_2 101.00
Order altered: id_2
>>> market buy 3
Trade, price: 101.0, qty: 3
>>> cancel order id_2
Order cancelled
>>> exit
```

## Regras de matching

### Prioridade de preço

- Compras são executadas primeiro nos maiores preços.
- Vendas são executadas primeiro nos menores preços.
- Uma compra limitada pode executar vendas com preço menor ou igual ao seu limite.
- Uma venda limitada pode executar compras com preço maior ou igual ao seu limite.

### Prioridade de tempo

Ordens no mesmo nível de preço são armazenadas em uma fila FIFO. A primeira ordem inserida é a primeira a ser executada.

### Ordens parcialmente executadas

Quando a quantidade da ordem agressora é maior que a quantidade disponível no nível de preço, o matching continua nos próximos níveis compatíveis. A quantidade restante de uma ordem limitada permanece no livro.

### Preço do trade

O trade é registrado pelo preço da ordem passiva que já estava no livro.

## Estrutura do projeto

| Arquivo | Responsabilidade |
| --- | --- |
| `Order.java` | Classe base com ID, tipo, lado e quantidade. |
| `LimitOrder.java` | Ordem limitada com preço definido. |
| `MarketOrder.java` | Ordem a mercado. |
| `PeggedOrder.java` | Ordem cujo preço acompanha o melhor bid ou offer. |
| `Trade.java` | Representação de uma execução. |
| `MatchingEngine.java` | Livro de ofertas, matching, cancelamentos e alterações. |
| `Main.java` | Interface de linha de comando. |

## Estruturas de dados

O `MatchingEngine` utiliza:

- `TreeMap<Double, Queue<Order>>` para manter os níveis de preço ordenados.
- `Queue<Order>` para preservar a prioridade FIFO em cada preço.
- `HashMap<String, Order>` para localizar ordens por ID.
- Mapas separados para acompanhar pegged buys e pegged sells.

O melhor bid é o maior preço em `buys`. O melhor offer é o menor preço em `sells`.

## Mensagens de erro

As entradas inválidas são capturadas pela interface e exibidas como:

```text
Error: <mensagem>
```

Mensagens comuns:

| Situação | Mensagem |
| --- | --- |
| ID inexistente | `Order not found` |
| Peg bid sem referência | `No reference bid available for peg order.` |
| Peg offer sem referência | `No reference offer available for peg order.` |
| Preço inválido | `Invalid arguments` |
| Quantidade inválida | `Invalid arguments` |
| Comando desconhecido | `Invalid command` |

## Limpeza dos arquivos compilados

Os comandos abaixo removem os arquivos `.class` gerados pela compilação:

```bash
rm -f *.class
```

## Observações

- Cada execução de `java Main` inicia um livro vazio.
- Os IDs são gerados durante a execução e devem ser usados exatamente como exibidos.
- Ordens a mercado não ficam pendentes no livro.
- Uma ordem pegged precisa de uma referência disponível no momento da criação.
