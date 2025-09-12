# Pokedex Mobile

Uma aplicação Android nativa, desenvolvida em Java, que implementa uma Pokedex funcional através do consumo de dados da [PokeAPI](https://pokeapi.co/). O objetivo principal deste projeto é demonstrar a implementação de conceitos fundamentais do desenvolvimento Android, como consumo de APIs REST, manipulação de dados JSON e construção de interfaces de usuário dinâmicas e responsivas.

## Visão Geral do Aplicativo

| Lista Principal e Busca | Tela de Detalhes do Pokémon |
| :---: | :---: |
| ![Imagem da tela principal](https://github.com/user-attachments/assets/466383c7-a7ba-42ed-8c08-814ad437eed1) | ![Imagem da tela de detalhes do pokémon](https://github.com/user-attachments/assets/82166e78-8db0-4336-a66f-c47ca88450ab) |

## Funcionalidades Implementadas

  * **Tela de Carregamento (Splash Screen):** Exibe uma animação inicial durante o carregamento do aplicativo.
  * **Listagem de Pokémon:** Apresenta os Pokémon em um layout de grid (`GridLayoutManager`) utilizando `RecyclerView` para otimização de performance.
  * **Busca em Tempo Real:** Inclui uma `SearchView` que filtra a lista de Pokémon com base na entrada do usuário. A filtragem é realizada no lado do cliente.
  * **Ordenação por ID:** A lista de Pokémon é consistentemente ordenada pelo seu número de ID, mesmo com o carregamento assíncrono dos detalhes.
  * **Exibição de Sprites:** Carrega e exibe sprites de alta qualidade dos Pokémon, utilizando os assets fornecidos pela API.
  * **Visualização de Tipos:** Cada tipo do Pokémon é exibido com um background colorido correspondente para fácil identificação.
  * **Tela de Detalhes:** Permite a navegação para uma tela de detalhes dedicada ao clicar em um Pokémon, exibindo informações adicionais como altura e peso.
  * **Controle de Versão:** O código-fonte é gerenciado com Git.

## Tecnologias Utilizadas

  * **Linguagem:** Java
  * **Framework:** Android SDK nativo
  * **Bibliotecas Principais:**
      * **Retrofit 2:** Cliente HTTP type-safe para Android e Java, utilizado para realizar as chamadas de rede à PokeAPI.
      * **Gson:** Biblioteca para serialização e desserialização de objetos Java para JSON, utilizada para converter as respostas da API em modelos de dados (POJOs).
      * **Glide:** Biblioteca para carregamento e cache de imagens, responsável por exibir os sprites dos Pokémon de forma assíncrona e eficiente.
      * **AndroidX Libraries:** Incluindo `RecyclerView`, `CardView`, e `ConstraintLayout` para a construção da interface de usuário.

## Como Executar o Projeto

Para clonar e executar esta aplicação em um ambiente de desenvolvimento local, os seguintes pré-requisitos são necessários:

  * Android Studio (versão Narwhal 25.1.2 ou superior)
  * Git

Siga os passos abaixo:

1.  **Clone o repositório:**
    ```bash
    git clone https://github.com/EmilioStuart/Pokedex-Mobile.git
    ```
2.  **Abra o projeto no Android Studio:**
      * No menu do Android Studio, selecione `File > Open` e navegue até o diretório onde o projeto foi clonado.
3.  **Sincronize as dependências do Gradle:**
      * Aguarde o Android Studio concluir o processo de sincronização do Gradle, que irá baixar todas as bibliotecas necessárias.
4.  **Execute a aplicação:**
      * Selecione um dispositivo Android (físico ou emulador) e clique no botão "Run" (▶).

## Estrutura do Código

  * **`MainActivity.java`**: Controla a tela principal, que exibe a lista de Pokémon e a funcionalidade de busca.
  * **`DetailActivity.java`**: Controla a tela que exibe as informações detalhadas de um Pokémon específico.
  * **`PokemonAdapter.java`**: Adaptador para o `RecyclerView`, responsável por vincular os dados dos Pokémon aos cards exibidos na lista.
  * **`PokeApiService.java`**: Interface do Retrofit que define os endpoints da API a serem consumidos.
  * **Modelos (POJOs)**: Classes como `Pokemon.java`, `PokemonDetail.java`, e `PokemonResponse.java` que modelam a estrutura dos dados recebidos da API.

## Possíveis Melhorias Futuras

  * **Paginação:** Implementar a biblioteca Paging 3 para carregar a lista de Pokémon sob demanda, melhorando a performance inicial e o uso de memória.
  * **Cache Local:** Integrar a biblioteca Room Persistence para criar um banco de dados local, permitindo o funcionamento offline e uma inicialização mais rápida do aplicativo.
  * **Injeção de Dependência:** Refatorar o projeto para utilizar um framework como Hilt ou Dagger para gerenciar as dependências de forma mais eficiente e melhorar a testabilidade.
  * **Testes:** Desenvolver testes unitários (JUnit) e de instrumentação (Espresso) para garantir a qualidade e a estabilidade do código.

## Autor

[EmilioStuart](https://github.com/EmilioStuart)
