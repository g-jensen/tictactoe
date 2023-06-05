(ns tictactoe.game-mode-spec
  (:require [speclj.core :refer :all]
            [tictactoe.board :refer :all]
            [tictactoe.move :refer :all]
            [tictactoe.game-mode :refer :all]))

(describe "A TicTacToe Game-mode"
  (it "gets the next board"
    (should= (play-move empty-board (get-computer-move empty-board))
             (next-board (->PvCGame) empty-board))))