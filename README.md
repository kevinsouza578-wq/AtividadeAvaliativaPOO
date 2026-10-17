# Atualizacao de Smart TV - Java POO

Projeto de simulacao de atualizacao de uma Smart TV.
A TV inicia na versao 1.0.0 e pode atualizar para 1.2.0.

## Executar

Precisa de JDK 8 ou superior instalado e configurado no PATH.
Extraia a pasta e execute executar.bat, ou use o terminal:

```
javac -encoding UTF-8 -d bin src\*.java
java -cp bin Main
```

O menu permite consultar a TV, buscar atualizacoes, baixar e instalar,
listar versoes e consultar o historico.
Para a verificacao automatica do fluxo principal, execute testar.bat.

## Classes

- Dispositivo: classe abstrata com modelo e numero de serie.
- Televisao: herda de Dispositivo e possui um Software.
- Software: controla a versao instalada e o historico.
- Versao: representa e compara os numeros da versao.
- PacoteAtualizacao: guarda versao, descricao, e link.
- ServidorAtualizacoes: disponibiliza os pacotes por HTTP local.
- GerenciadorAtualizacoes: consulta, baixa e instala.
- Main: cria os objetos e apresenta o menu.

## Observacoes

Nao utiliza bibliotecas externas. O servidor roda no proprio computador,
sem precisar de internet. O link 127.0.0.1 nao e publico.
O arquivo baixado e um texto demonstrativo, nao um firmware real.
A instalacao e simulada e a versao volta para 1.0.0 ao reiniciar.
O download e direto, sem verificacao de hash.

O README original registra entrega pelo GitHub e orientacao do professor
para uso minimo de IA. Confira o audio/enunciado e entenda o codigo antes
da entrega; os requisitos originais nao estao incluidos neste ZIP.

## Ajustes desta revisao

Validacao dos dados do pacote, tratamento de catalogo invalido,
conferencia entre versao e nome do arquivo e encerramento do menu
quando a entrada do terminal termina.
