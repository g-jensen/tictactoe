(ns tictactoe.ui-spec
  (:require [speclj.core :refer :all]
            [tictactoe.ui :refer :all]
            [tictactoe.board :as board]
            [tictactoe.game-mode :as game-mode]))

(describe "A TicTacToe Console UI"
  (it "converts a board to a string"
    (should= "_ _ _\n_ _ _\n_ _ _" (board->str board/empty-board))
    (should= "x _ _\n_ _ _\n_ _ _" (board->str board/first-move-board)))

  (it "displays a board"
    (should= "_ _ _\n_ _ _\n_ _ _\n"
             (with-out-str (display-board board/empty-board)))
    (should= "x _ _\n_ _ _\n_ _ _\n"
             (with-out-str (display-board board/first-move-board))))

  (it "displays the game over message"
    (should= "x has won!\n" (with-out-str (display-game-over-message
                                        [\x \x \x \o \o \_ \_ \_ \_])))
    (should= "o has won!\n" (with-out-str (display-game-over-message
                                            [\o \o \o \x \x \_ \x \_ \_])))
    (should= "x has won!\n" (with-out-str (display-game-over-message
                                            [\o \o \_ \x \x \x \_ \_ \_])))
    (should= "tie!\n" (with-out-str (display-game-over-message
                                    [\o \x \x \x \o \o \o \x \x]))))

  (it "displays the game-modes"
    (should= "1: gamemode1\n2: gamemode2\n"
             (with-out-str (display-game-modes
                             [{:name "gamemode1"}
                              {:name "gamemode2"}])))
    (should= "1: Versus Player\n2: Versus Unbeatable Computer\n"
             (with-out-str (display-game-modes game-mode/game-modes))))

  (it "displays the game-modes prompt"
    (should= (str "Pick a game-mode:\n" (with-out-str (display-game-modes game-mode/game-modes)))
             (with-out-str (display-game-modes-prompt game-mode/game-modes)))))