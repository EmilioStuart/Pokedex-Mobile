# 📱 Pokedex Mobile (v2.0.0)

![Java](https://img.shields.io/badge/Language-Java-orange?style=for-the-badge&logo=android)
![API](https://img.shields.io/badge/API-PokeAPI%20%26%20MyMemory-red?style=for-the-badge)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

Uma Pokédex moderna e interativa desenvolvida em Java nativo para Android, inspirada na interface do Pokémon GO. Este projeto representa uma refatoração completa de uma Pokédex básica para uma aplicação rica em funcionalidades, com um novo fluxo de navegação, design system customizado e integração de múltiplas APIs para enriquecimento de dados em tempo real.

## 🎥 Demonstração em Vídeo

Assista ao aplicativo em ação, demonstrando a navegação por regiões, a grade de Pokémon, a busca, a ordenação e a nova tela de detalhes com fundo dinâmico e componentes interativos.

*(Vídeo em breve)*

## ✨ Principais Funcionalidades

O aplicativo foi completamente redesenhado com um novo fluxo de navegação e um conjunto robusto de funcionalidades.

#### 📍 Tela de Regiões (`RegionActivity`) - *Novo Ponto de Entrada*
* **🗺️ Navegação por Região:** A tela principal agora é uma lista com cards para cada uma das 9 gerações de jogos, servindo como o hub central de navegação.
* **🎨 Design Temático:** Apresenta um design limpo com uma paleta de cor laranja e imagens dos Pokémon iniciais de cada região.
* **⚡ Performance na Carga:** Carrega instantaneamente, utilizando dados locais para exibir os cards e garantir uma experiência de usuário fluida.

#### Pokemon Grid Screen (`PokemonGridActivity`)
* **🖼️ Grade Regional:** Exibe todos os Pokémon pertencentes à região selecionada em um grid customizado.
* **🔍 Busca por Prefixo:** Filtra a lista de Pokémon da geração atual, exibindo instantaneamente os resultados que começam com o texto digitado.
* **⇅ Ordenação Dinâmica:** Um `FloatingActionButton` abre um menu para reordenar a lista por Número (crescente/decrescente) ou Nome (A-Z/Z-A).

#### Tela de Detalhes (`DetailActivity`) - *Completamente Redesenhada*
* **🎨 Fundo Dinâmico:** O plano de fundo da tela muda de cor com um degradê baseado no tipo principal do Pokémon exibido.
* **🎠 Carrossel de Sprites Interativo:** Visualize múltiplos sprites do Pokémon (3D, Artwork Oficial, Pixelado) através de um carrossel com botões de navegação.
* **✨ Alternância de Sprites (Normal/Shiny):** Um `FloatingActionButton` permite ao usuário alternar instantaneamente **todas** as imagens (carrossel principal e cadeia de evolução) para suas versões *shiny*.
* **📜 Descrição Traduzida:** A descrição da Pokédex é obtida em inglês e traduzida para o português em tempo real via MyMemory Translation API, com uma mensagem de "Traduzindo..." durante o processo.
* **📊 Gráfico de Atributos:** Os status base (HP, Ataque, etc.) são exibidos com barras de progresso customizadas e com **cores condicionais** (vermelho, amarelo, verde) baseadas no valor.
* **🧬 Cadeia de Evolução Interativa:** Uma linha do tempo horizontal exibe a cadeia de evolução com sprites de alta qualidade. Clicar em uma evolução **recarrega a tela** com as informações do Pokémon selecionado.

## 🛠️ Arquitetura e Tecnologias Utilizadas

A arquitetura do app foi reestruturada para suportar um fluxo de navegação mais complexo e manter o código desacoplado e escalável, com responsabilidades separadas por pacotes.

* **Linguagem:** **Java**
* **Framework:** **Android SDK Nativo**
* **Bibliotecas Principais:**
    * **Retrofit 2:** Cliente HTTP para a comunicação com as APIs. Duas instâncias são criadas para gerenciar os diferentes `baseUrls` da PokeAPI e da API de Tradução.
    * **Gson:** Utilizado para a serialização e desserialização de objetos Java para JSON.
    * **Glide:** Gerencia o carregamento e cache de imagens e GIFs.
    * **AndroidX Libraries:** `RecyclerView`, `CardView`, `ConstraintLayout`, `AppCompat`.
    * **Material Components:** Para componentes modernos como o `FloatingActionButton` e `Toolbar`.

## ⚙️ APIs Utilizadas

* **[PokeAPI (v2)](https://pokeapi.co/):** Fonte principal para todos os dados de Pokémon.
* **[MyMemory Translation API](https://mymemory.translated.net/):** Serviço gratuito utilizado para a tradução em tempo real.

## 🚀 Como Executar o Projeto

1.  **Pré-requisitos:**
    * Android Studio (versão Hedgehog ou superior)
    * Git

2.  **Clonagem:**
    ```bash
    git clone https://github.com/EmilioStuart/Pokedex-Mobile.git
    ```

3.  **Build e Execução:**
    * Abra o projeto no Android Studio, aguarde a sincronização do Gradle e execute.

## 📲 Download / Instalação

Você pode instalar o aplicativo diretamente no seu celular Android baixando o arquivo APK da nossa última release.

[![Download APK](https://img.shields.io/badge/Download-APK%20v2.0.0-orange?style=for-the-badge&logo=android)](https://github.com/EmilioStuart/Pokedex-Mobile/releases/download/v2.0.0/app-debug.apk)

**Aviso:** Para instalar, você precisará habilitar a opção "Instalar de fontes desconhecidas" nas configurações de segurança do seu Android.

## 🔮 Próximos Passos e Melhorias

* **Cache Local (Room):** Implementar um banco de dados local para persistir os dados, permitindo funcionamento offline e carregamento instantâneo.
* **Injeção de Dependência (Hilt):** Refatorar o projeto para usar Hilt, facilitando a testabilidade e o gerenciamento de dependências.
* **Testes Unitários:** Adicionar testes para a lógica de negócio e interações com a API.

## 👨‍💻 Autor

* **[EmilioStuart](https://github.com/EmilioStuart)**
