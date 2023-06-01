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
             (with-out-str (display-board first-move-board)))))