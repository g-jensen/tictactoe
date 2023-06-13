(ns tictactoe.move_spec
  (:require [speclj.core :refer :all]
            [tictactoe.move :refer :all]
            [tictactoe.utils :as utils]
            [tictactoe.utils-spec :as utils-spec]))

(describe "A TicTacToe Mover"
  (it "gets whose move it is"
    (should= \x (player-to-move utils/empty-board))
    (should= \o (player-to-move utils-spec/first-move-board))
    (should= \x (player-to-move [\x \o \_ \_ \_ \_ \_ \_ \_])))

  (it "checks if a move is valid"
    (should (move-valid? utils/empty-board 0))
    (should-not (move-valid? utils/empty-board -1))
    (should-not (move-valid? utils/empty-board 9))
    (should-not (move-valid? utils-spec/first-move-board 0)))

  (it "plays a move on a board"
    (should= utils/empty-board (play-move utils/empty-board -1))
    (should= utils/empty-board (play-move utils/empty-board 9))
    (should= utils-spec/first-move-board (play-move utils-spec/first-move-board 0))
    (should= utils-spec/first-move-board (play-move utils/empty-board 0))
    (should= [\x \o \_ \_ \_ \_ \_ \_ \_] (play-move utils-spec/first-move-board 1)))

  (with-stubs)
  (it "gets the user's next move"
    (with-redefs [read-line (stub :read-line {:return "1"})]
      (should= 0 (get-user-move))
      (should-have-invoked :read-line))

    (with-redefs [read-line (stub :read-line {:return "2"})]
      (should= 1 (get-user-move))
      (should-have-invoked :read-line))

    (with-redefs [read-line (stub :read-line {:return "0"})]
      (should= -1 (get-user-move))
      (should-have-invoked :read-line))

    (with-redefs [read-line (stub :read-line {:return "10"})]
      (should= -1 (get-user-move))
      (should-have-invoked :read-line))

    (with-redefs [read-line (stub :read-line {:return "g"})]
      (should= -1 (get-user-move))
      (should-have-invoked :read-line)))

  (context "An Unbeatable Computer"

    (it "weighs the value of a move"
      (should= 10 (move-weight [\_ \x \x \o \o \_ \_ \_ \_] 0))
      (should= -10 (move-weight [\_ \o \o \x \x \_ \x \_ \_] 0))
      (should= 0 (move-weight [\_ \o \x \x \o \o \o \x \x] 0))
      (should= 10 (move-weight [\_ \_ \_ \_ \_ \_ \_ \_ \x] 7))
      (should= 0 (move-weight [\_ \_ \_ \_ \_ \_ \_ \_ \x] 4))
      (should= 10 (move-weight [\_ \_ \_ \_ \_ \_ \_ \o \x] 4))
      (should= -10 (move-weight [\_ \o \o \_ \_ \x \x \_ \x] 0))
      (should= 0 (move-weight utils/empty-board 8)))

    (it "picks a corner given an empty board"
      (should= 8 (get-computer-move utils/empty-board)))

    (it "wins given the chance"
      (should= 6 (get-computer-move [\_ \_ \_ \_ \o \o \_ \x \x]))
      (should= 2 (get-computer-move [\_ \_ \_ \_ \o \x \_ \o \x]))
      (should= 0 (get-computer-move [\_ \_ \_ \o \x \_ \o \_ \x]))
      (should= 3 (get-computer-move [\_ \_ \_ \_ \x \x \x \o \_])))

    (it "blocks the other player from winning"
      (should= 2 (get-computer-move [\_ \_ \_ \_ \o \_ \o \x \x])))))