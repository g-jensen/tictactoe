(ns tictactoe.ui-spec
  (:require [speclj.core :refer :all]
            [tictactoe.board :refer :all]
            [tictactoe.ui :refer :all]))

(describe "A TicTacToe Console UI"
  (it "converts a board to a string"
    (should= "_ _ _\n_ _ _\n_ _ _" (board->str empty-board))
    (should= "x _ _\n_ _ _\n_ _ _" (board->str first-move-board)))

  (it "displays a board"
    (should= "_ _ _\n_ _ _\n_ _ _\n"
             (with-out-str (display-board empty-board)))
    (should= "x _ _\n_ _ _\n_ _ _\n"
             (with-out-str (display-board first-move-board))))

  (it "displays the winning message"
    (should= "x has won!\n" (with-out-str (display-winning-message
                                        [\x \x \x \o \o \_ \_ \_ \_])))
    (should= "o has won!\n" (with-out-str (display-winning-message
                                            [\o \o \o \x \x \_ \x \_ \_])))
    (should= "x has won!\n" (with-out-str (display-winning-message
                                            [\o \o \_ \x \x \x \_ \_ \_])))))