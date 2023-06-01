(ns tictactoe.board-spec
  (:require [speclj.core :refer :all]
            [tictactoe.board :refer :all]))

(describe "A TicTacToe Board"
  (it "initializes an empty 3x3 board"
    (should= (repeat 9 empty-tile) empty-board)))