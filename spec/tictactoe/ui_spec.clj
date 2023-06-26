(ns tictactoe.ui-spec
  (:require [speclj.core :refer :all]
            [tictactoe.ui :refer :all]
            [tictactoe.utils :as utils]
            [tictactoe.utils-spec :as utils-spec]))

(describe "A TicTacToe Console UI"

  (it "prints a board"
    (should= "_ _ _\n_ _ _\n_ _ _\n\n"
             (with-out-str (print-board (utils/empty-board 3))))
    (should= "x _ _\n_ _ _\n_ _ _\n\n"
             (with-out-str (print-board (utils-spec/first-move-board 3)))))

  (it "prints the game over message"
    (should= "x has won!\nPlay Again:\n" (with-out-str (print-game-over-message
                                        [\x \x \x \o \o \_ \_ \_ \_])))
    (should= "o has won!\nPlay Again:\n" (with-out-str (print-game-over-message
                                            [\o \o \o \x \x \_ \x \_ \_])))
    (should= "x has won!\nPlay Again:\n" (with-out-str (print-game-over-message
                                            [\o \o \_ \x \x \x \_ \_ \_])))
    (should= "tie!\nPlay Again:\n" (with-out-str (print-game-over-message
                                    [\o \x \x \x \o \o \o \x \x])))))