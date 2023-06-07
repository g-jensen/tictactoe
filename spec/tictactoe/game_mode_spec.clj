(ns tictactoe.game-mode-spec
  (:require [speclj.core :refer :all]
            [tictactoe.game-mode :refer :all]
            [tictactoe.board :as board]
            [tictactoe.move :as move]))

;; TODO - test next-board with PvPGame
(describe "A TicTacToe Game-mode"
  (it "gets the next board"
    ;; TODO - ask if I should put a vector literal here or if this is good
    (should= (move/play-move board/empty-board (move/get-computer-move board/empty-board))
             (next-board (->PvCGame) board/empty-board))))