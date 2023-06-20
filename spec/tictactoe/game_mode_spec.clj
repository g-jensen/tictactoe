(ns tictactoe.game-mode-spec
  (:require [speclj.core :refer :all]
            [tictactoe.game-mode :refer :all]
            [tictactoe.utils :as utils]
            [tictactoe.move :as move]))

(describe "A TicTacToe Game-mode"

  (with-stubs)
  (context "PvPGame"

    (it "gets the initial board"
      (should= (utils/empty-board 3) (initial-board (->PvPGame 3 (utils/empty-board 3)))))

    (it "gets the next board"
      (with-redefs [read-line (stub :read-line {:return "1"})]
        (should= [\x \_ \_ \_ \_ \_ \_ \_ \_]
                 (next-board (->PvPGame 3 (utils/empty-board 3)) (utils/empty-board 3)))
        (should-have-invoked :read-line))

      (with-redefs [read-line (stub :read-line {:return "2"})]
        (should= [\x \o \_ \_ \_ \_ \_ \_ \_]
                 (next-board (->PvPGame 3 (utils/empty-board 3)) [\x \_ \_ \_ \_ \_ \_ \_ \_]))
        (should-have-invoked :read-line))))

  (context "PvCGame"

    (it "gets the next board"
      (with-redefs [read-line (stub :read-line {:return "1"})]
        (should= [\x \_ \_ \_ \_ \_ \_ \_ \_]
                 (next-board (->PvCGame 3 (utils/empty-board 3) :hard) (utils/empty-board 3)))
        (should-have-invoked :read-line))

      ;; TODO - ask if I should put a vector literal here or if this is good
      (should= (move/play-move (utils/empty-board 3) (move/get-computer-move :hard (utils/empty-board 3)))
               (next-board (->PvCGame 3 (move/play-move (utils/empty-board 3) (move/get-computer-move :hard (utils/empty-board 3))) :hard) (utils/empty-board 3))))))