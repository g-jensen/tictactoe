(ns clj.tictactoe.game-mode-spec
  (:require [speclj.core :refer :all]
            [clj.tictactoe.game-mode :refer :all]
            [clj.tictactoe.utils :as utils]
            [clj.tictactoe.move :as move]))

(describe "A TicTacToe Game-mode"

  (with-stubs)
  (context "PvPGame"

    (it "gets the initial board"
      (should= (utils/empty-board 3) (initial-board (->PvPGame 3 (utils/empty-board 3))))))

  (context "PvCGame"
    (it "gets the initial board"
      (should= (utils/empty-board 3) (initial-board (->PvCGame 3 (utils/empty-board 3) :hard))))))