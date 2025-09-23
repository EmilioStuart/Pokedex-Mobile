# 📱 Pokedex Mobile (v1.4.0)

Uma Pokédex moderna e interativa desenvolvida em Java nativo para Android. Este projeto vai além de uma simples lista, integrando múltiplas chamadas de API em tempo real para enriquecer os dados, incluindo um serviço de tradução e uma interface dinâmica para explorar os detalhes, status e evoluções de cada Pokémon.

## 🎥 Demonstração em Vídeo

Assista ao aplicativo em ação, demonstrando a navegação, busca, ordenação e a riqueza de detalhes de cada Pokémon.

*Vídeo em breve*

## ✨ Principais Funcionalidades

O aplicativo foi construído com um rico conjunto de funcionalidades para criar uma experiência de usuário completa e agradável.

#### Tela Principal (`MainActivity`)

* **🌐 Filtro por Geração:** Um seletor (`Spinner`) permite ao usuário carregar e explorar a Pokédex de cada uma das 9 gerações de jogos, atualizando dinamicamente a lista de Pokémon.
* **🎵 Música de Fundo:** Abertura clássica de Pokémon Emerald para uma imersão nostálgica.
* **🖼️ Lista em Grid Dinâmico:** Carregamento dos Pokémon da geração selecionada, exibidos em um `RecyclerView` com `GridLayoutManager`.
* **🔍 Busca por Prefixo:** A busca filtra a lista de Pokémon da geração atual, exibindo instantaneamente os resultados que começam com o texto digitado.
* **⇅ Ordenação Dinâmica:** Um `FloatingActionButton` abre um menu para reordenar a lista por:
    * Número (Crescente ou Decrescente)
    * Nome (A-Z ou Z-A)
* **🔄 Indicador de Carregamento:** Um GIF animado é exibido durante as operações de rede para fornecer feedback visual ao usuário.
* **🎨 UI Consistente:** O app opera exclusivamente em Modo Claro e com a orientação travada no modo Retrato para garantir uma experiência de usuário estável e consistente.

#### Tela de Detalhes (`DetailActivity`)

* **✨ Alternância de Sprite (Normal/Shiny):** Um `FloatingActionButton` e um carrossel de imagens permitem ao usuário visualizar múltiplos sprites do Pokémon (3D, Artwork Oficial, Pixelado) e alternar para suas versões *shiny*.
* **📜 Descrição Traduzida:** A descrição da Pokédex é obtida em inglês e traduzida para o português em tempo real através da integração com a MyMemory Translation API.
* **📊 Gráfico de Atributos:** Os status base (HP, Ataque, Defesa, etc.) são exibidos com barras de progresso customizadas e com **cores condicionais** (vermelho, amarelo, verde) baseadas no valor do atributo.
* **🧬 Cadeia de Evolução Interativa:** Uma linha do tempo horizontal e rolável exibe toda a cadeia de evolução do Pokémon. Clicar em uma das evoluções **recarrega a tela de detalhes** com as informações do Pokémon selecionado.
* **🎨 Ícones de Tipo Customizados:** Os tipos do Pokémon são exibidos como imagens (`ImageViews`), carregadas dinamicamente com base no nome do tipo.

## 🛠️ Arquitetura e Tecnologias Utilizadas

A arquitetura do app foi projetada para ser desacoplada e escalável, separando as responsabilidades em pacotes lógicos (ui, adapter, data).

* **Linguagem:** **Java**
* **Framework:** **Android SDK Nativo**
* **Bibliotecas Principais:**
    * **Retrofit 2:** Cliente HTTP utilizado para a comunicação com as APIs. Duas instâncias são criadas para gerenciar os diferentes `baseUrls` da PokeAPI e da API de Tradução.
    * **Gson:** Utilizado para a serialização e desserialização de objetos Java para JSON.
    * **Glide:** Gerencia o carregamento e cache de imagens e GIFs, essencial para a performance da UI.
    * **AndroidX Libraries:** `RecyclerView`, `CardView`, `ConstraintLayout`, `AppCompat`.
    * **Material Components:** Para componentes modernos como o `FloatingActionButton`.

## ⚙️ APIs Utilizadas

* **[PokeAPI (v2)](https://pokeapi.co/):** Fonte principal para todos os dados de Pokémon, incluindo detalhes, espécies, gerações e cadeias de evolução.
* **[MyMemory Translation API](https://mymemory.translated.net/):** Serviço gratuito utilizado para a tradução em tempo real das descrições dos Pokémon.

## 🚀 Como Executar o Projeto

Para clonar e executar esta aplicação, siga os passos:

1.  **Pré-requisitos:**
    * Android Studio (versão Hedgehog ou superior)
    * Git

2.  **Clonagem:**
    ```bash
    git clone [https://github.com/EmilioStuart/Pokedex-Mobile.git](https://github.com/EmilioStuart/Pokedex-Mobile.git)
    ```

3.  **Assets Necessários:**
    * **Ícones dos Tipos:** Este projeto carrega os ícones dos tipos dinamicamente. Você precisa adicionar os arquivos `.png` para cada tipo na pasta `app/src/main/res/drawable`. O nome de cada arquivo deve seguir o padrão `nomedotipoemingles_type.png` (ex: `fire_type.png`, `water_type.png`).
    * **Música de Fundo:** Adicione o arquivo de áudio `opening_pokemon_emerald.mp3` (ou `.wav`) na pasta `app/src/main/res/raw`.

4.  **Build e Execução:**
    * Abra o projeto no Android Studio.
    * Aguarde a sincronização do Gradle.
    * Execute o aplicativo em um emulador ou dispositivo físico.

## 📲 Download / Instalação

Você pode instalar o aplicativo diretamente no seu celular Android baixando o arquivo APK da nossa última release.

[![Download APK](https://img.shields.io/badge/Download-APK%20v1.4.0-brightgreen?style=for-the-badge&logo=android)](https://github.com/EmilioStuart/Pokedex-Mobile/releases/download/v1.4.0/app-debug.apk)

**Aviso:** Para instalar, você precisará habilitar a opção "Instalar de fontes desconhecidas" nas configurações de segurança do seu Android. Instale apenas arquivos APK de fontes que você confia.

## 🔮 Próximos Passos e Melhorias

* **Cache Local (Room):** Implementar um banco de dados local com Room para persistir os dados dos Pokémon. Isso permitiria o funcionamento offline e uma inicialização quase instantânea do app.
* **Injeção de Dependência (Hilt):** Refatorar o projeto para usar Hilt, facilitando a testabilidade e o gerenciamento de dependências.
* **Testes Unitários:** Adicionar testes unitários para a lógica de negócio, como o processamento dos dados da API.
* **Melhorar UI/UX:** Adicionar animações de transição entre telas e otimizar a performance da rolagem com `DiffUtil`.

## 👨‍💻 Autor

* **[GitHub](https://github.com/EmilioStuart)**
