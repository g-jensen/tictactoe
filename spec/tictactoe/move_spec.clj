(ns tictactoe.move_spec
  (:require [speclj.core :refer :all]
            [tictactoe.move :refer :all]
            [tictactoe.board :as board]
            [tictactoe.board-spec :as board-spec]))

(describe "A TicTacToe Mover"
  (it "gets whose move it is"
    (should= \x (player-to-move board/empty-board))
    (should= \o (player-to-move board-spec/first-move-board))
    (should= \x (player-to-move [\x \o \_ \_ \_ \_ \_ \_ \_])))

  (it "checks if a move is valid"
    (should (move-valid? board/empty-board 0))
    (should-not (move-valid? board/empty-board -1))
    (should-not (move-valid? board/empty-board 9))
    (should-not (move-valid? board-spec/first-move-board 0)))

  (it "plays a move on a board"
    (should= board/empty-board (play-move board/empty-board -1))
    (should= board/empty-board (play-move board/empty-board 9))
    (should= board-spec/first-move-board (play-move board-spec/first-move-board 0))
    (should= board-spec/first-move-board (play-move board/empty-board 0))
    (should= [\x \o \_ \_ \_ \_ \_ \_ \_] (play-move board-spec/first-move-board 1)))

  ;;TODO - test get-user-move
  (it "gets the user's next move"
    ())

  (it "weighs the value of a move"
    (should= 10 (move-weight [\_ \x \x \o \o \_ \_ \_ \_] 0))
    (should= -10 (move-weight [\_ \o \o \x \x \_ \x \_ \_] 0))
    (should= 0 (move-weight [\_ \o \x \x \o \o \o \x \x] 0))
    (should= 10 (move-weight [\_ \_ \_ \_ \_ \_ \_ \_ \x] 7))
    (should= 0 (move-weight [\_ \_ \_ \_ \_ \_ \_ \_ \x] 4))
    (should= 10 (move-weight [\_ \_ \_ \_ \_ \_ \_ \o \x] 4))
    (should= -10 (move-weight [\_ \o \o \_ \_ \x \x \_ \x] 0))
    (should= 0 (move-weight board/empty-board 8)))

  (context "An Unbeatable Computer"
    (it "picks a corner given an empty board"
      (should= 8 (get-computer-move board/empty-board)))
    (it "wins given the chance"
      (should= 6 (get-computer-move [\_ \_ \_ \_ \o \o \_ \x \x]))
      (should= 2 (get-computer-move [\_ \_ \_ \_ \o \x \_ \o \x]))
      (should= 0 (get-computer-move [\_ \_ \_ \o \x \_ \o \_ \x]))
      (should= 3 (get-computer-move [\_ \_ \_ \_ \x \x \x \o \_])))
    (it "blocks the other player from winning"
      (should= 2 (get-computer-move [\_ \_ \_ \_ \o \_ \o \x \x])))))