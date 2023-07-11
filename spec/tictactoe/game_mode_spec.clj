(ns tictactoe.game-mode-spec
  (:require [speclj.core :refer :all]
            [tictactoe.game-mode :refer :all]
            [tictactoe.utils :as utils]
            [tictactoe.move :as move]))

(describe "A TicTacToe Game-mode"

  (with-stubs)
  (context "PvPGame"

    (it "gets the initial board"
      (should= (utils/empty-board 3) (initial-board (->PvPGame 3 (utils/empty-board 3))))))

  (context "PvCGame"
    (it "gets the initial board"
      (should= (utils/empty-board 3) (initial-board (->PvCGame 3 (utils/empty-board 3) :hard))))))