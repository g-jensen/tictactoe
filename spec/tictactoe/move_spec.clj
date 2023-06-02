(ns tictactoe.move_spec
  (:require [speclj.core :refer :all]
            [tictactoe.move :refer :all]
            [tictactoe.board :refer :all]))

(describe "A TicTacToe Move"
  (it "gets whose move it is"
    (should= \x (player-to-move empty-board))
    (should= \o (player-to-move first-move-board))
    (should= \x (player-to-move [\x \o \_ \_ \_ \_ \_ \_ \_])))

  (it "checks if a move is valid"
    (should (move-valid? empty-board 0))
    (should-not (move-valid? empty-board -1))
    (should-not (move-valid? empty-board 9))
    (should-not (move-valid? first-move-board 0)))

  (it "plays a move on a board"
    (should= empty-board (play-move empty-board -1))
    (should= empty-board (play-move empty-board 9))
    (should= first-move-board (play-move first-move-board 0))
    (should= first-move-board (play-move empty-board 0))
    (should= [\x \o \_ \_ \_ \_ \_ \_ \_] (play-move first-move-board 1))))