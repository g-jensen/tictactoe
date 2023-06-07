(ns tictactoe.game-mode-spec
  (:require [speclj.core :refer :all]
            [tictactoe.game-mode :refer :all]
            [tictactoe.board :as board]
            [tictactoe.move :as move]))

(describe "A TicTacToe Game-mode"
  (it "gets the next board"
    (should= (move/play-move board/empty-board (move/get-computer-move board/empty-board))
             (next-board (->PvCGame) board/empty-board))))