# tictactoe

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
