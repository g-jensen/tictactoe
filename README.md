# tictactoe

## Features
* Any NxN board (N > 2)
* 3x3x3 board
* Easy, Medium, and Hard difficulty versus computer (Hard is unbeatable)
* User Interface (Console or Graphical)
* Database persistence (File or SQL)

## Running Release Build
```shell
java -jar tictactoe.jar console
```
or
```shell
java -jar tictactoe.jar quil
```

## Running In Development
### Prerequisite

Install [sqlite](https://www.sqlite.org/download.html)

### Running main
```shell
clj -M:console
```
or
```shell
clj -M:quil
```
### Running tests
```shell
clj -M:test:spec
```
