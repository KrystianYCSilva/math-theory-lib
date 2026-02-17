# ecosystem_and_science

Resumo
Panorama prático das bibliotecas, frameworks e ferramentas relevantes para produção e para aplicações de Data Science/Científicas em Kotlin.

Frameworks e ferramentas comerciais / produção
- Spring Boot + Coroutines
  - Uso: microservices, backends corporativos.
  - Nota: integrar coroutines com Spring WebFlux/Servlet via adaptadores; preferir WebFlux para backpressure.
- Ktor
  - Uso: servidores HTTP leves e APIs; arquitetura asincrónica nativa com coroutines.
- Jetpack Compose (Android) / Compose for Desktop
  - Uso: UI declarativa multiplataforma; integrar com coroutines para side-effects.
- Bibliotecas de DI
  - Koin — simples e idiomático para multiplataforma em testes e protótipos.
  - Dagger / Hilt — para cenários que requerem otimização e integração com toolchains Android.

Data Science / Aplicações científicas
- Jupyter + Kotlin kernel (Kotlin Jupyter)
  - Uso: notebooks exploratórios, prototipagem e visualização interativa.
- Multik
  - Biblioteca para arrays multi-dimensionais e operações numéricas, similar a numpy.
- KotlinDL
  - Biblioteca de deep learning em Kotlin (camadas, modelos), boa para prototipagem; para produção considerar export para TF/PyTorch quando necessário.
- Kotlin Dataframe
  - Manipulação tabular de dados (filtrar, agrupar, transformar) com API idiomática Kotlin.

Core libs essenciais
- kotlinx.coroutines — concorrência idiomática (suspending functions, flows, structured concurrency).
- kotlinx.serialization — serialização multiplataforma (JSON, ProtoBuf, CBOR); suporte a polymorphic serializers.
- Arrow (opcional) — programação funcional mais rica: Either, Option, validated, typeclasses.
- Ktor client / OkHttp / Retrofit — clients HTTP para JVM/JS/Native (escolha conforme target).
- Test libs — kotlin.test, MockK (mocking idiomático Kotlin), kotest (mais features).

Recomendações práticas
- Preferir multiplataforma apenas quando traz vantagem (compartilhar lógica core); caso contrário, escolha JVM (ecossistema maduro) ou JS (frontend).
- Para pipelines de data science exploratórias, combinar Kotlin Jupyter + Multik + Kotlin Dataframe; quando houver modelos de ML maduros, avaliar uso de Python interop via subprocessos ou export de modelos.
- Serialização: escolher kotlinx.serialization para multiplataforma; ao interoperar com Jackson/Gson em projetos Java, mapear contratos explicitamente.

Exemplos de dependências (Gradle Kotlin DSL)
```
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
implementation("io.insert-koin:koin-core:3.3.0")
```

Quando consultar este ficheiro
- Ao selecionar stack para novo serviço (Ktor vs Spring).
- Ao avaliar bibliotecas para processamento numérico e ML.
- Ao resolver trade-offs entre produtividade (Kotlin-first libs) e ecosistema (Java/Python mature libs).

Links úteis
- kotlinx.coroutines: https://github.com/Kotlin/kotlinx.coroutines
- kotlinx.serialization: https://github.com/Kotlin/kotlinx.serialization
- Multik: https://github.com/Kotlin/multik
- KotlinDL: https://github.com/JetBrains/KotlinDL
- Kotlin Dataframe: https://github.com/Kotlin/dataframe

Scripts e snippets úteis
- Shell: inicializar módulo KMP (exemplo mínimo)
  ```
  #!/usr/bin/env bash
  ./gradlew :module-name:init --quiet
  # adaptar build.gradle.kts com targets desejados (jvm, js, mingwX64)
  ```
- Python helper (packaging docs): script de validação de dependências pode ser colocado em scripts/ se a skill for persistida.

(Fim do documento)
