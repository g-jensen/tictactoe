(ns tictactoe.game-mode-spec
  (:require [speclj.core :refer :all]
            [tictactoe.game-mode :refer :all]
            [tictactoe.utils :as utils]
            [tictactoe.move :as move]))

(describe "A TicTacToe Game-mode"

  (with-stubs)
  (context "PvPGame"

    (it "gets the initial board"
      (should= utils/empty-board (initial-board (->PvPGame))))

    (it "gets the next board"
      (with-redefs [read-line (stub :read-line {:return "1"})]
        (should= [\x \_ \_ \_ \_ \_ \_ \_ \_]
                 (next-board (->PvPGame) utils/empty-board))
        (should-have-invoked :read-line))

      (with-redefs [read-line (stub :read-line {:return "2"})]
        (should= [\x \o \_ \_ \_ \_ \_ \_ \_]
                 (next-board (->PvPGame) [\x \_ \_ \_ \_ \_ \_ \_ \_]))
        (should-have-invoked :read-line))))

  (context "PvCGame"
    (it "gets the initial board"
      (should= utils/empty-board (initial-board (->PvCGame \x :hard)))
      (should= [\_ \_ \_ \_ \_ \_ \_ \_ \x] (initial-board (->PvCGame \o :hard))))

    (it "gets the next board"
      (with-redefs [read-line (stub :read-line {:return "1"})]
        (should= [\x \_ \_ \_ \_ \_ \_ \_ \_]
                 (next-board (->PvCGame \x :hard) utils/empty-board))
        (should-have-invoked :read-line))

      ;; TODO - ask if I should put a vector literal here or if this is good
      (should= (move/play-move utils/empty-board (move/get-computer-move :hard utils/empty-board))
               (next-board (->PvCGame \o :hard) utils/empty-board)))))