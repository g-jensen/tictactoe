# tictactoe

## Features
* Any NxN board (N > 2)
* 3x3x3 board
* Easy, Medium, and Hard difficulty versus computer (Hard is unbeatable)
* User Interface (Console, Graphical, or Web)
* Database persistence (File or SQL)

## Running Release Build
```shell
java -jar tictactoe.jar console
```
or
```shell
java -jar tictactoe.jar quil
```
or
```shell
java -jar tictactoe.jar web
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
or
```shell
clj -M:web
```
### Running tests
```shell
clj -M:test:spec
```
### Building
(currently broken)
```shell
clj -T:build uber
```
