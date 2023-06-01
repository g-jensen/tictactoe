(ns tictactoe.game-engine-spec
  (:require [speclj.core :refer :all]
            [tictactoe.game-engine :refer :all]
            [tictactoe.board :refer :all]))

(describe "A TicTacToe Game Engine"
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
    (should= [\x \o \_ \_ \_ \_ \_ \_ \_] (play-move first-move-board 1)))

  (it "gets the rows of a board"
    (should= [[:a :b :c] [:d :e :f] [:g :h :i]]
             (rows [:a :b :c :d :e :f :g :h :i])))

  (it "gets the columns of a board"
    (should= [[:a :d :g] [:b :e :h] [:c :f :i]]
             (columns [:a :b :c :d :e :f :g :h :i])))

  (it "gets the diagonals of a board"
    (should= [[:a :e :i] [:c :e :g]]
             (diagonals [:a :b :c :d :e :f :g :h :i])))

  (it "checks if a player has won"
    (should-not (win? empty-board))
    (should (win? [\x \x \x \_ \_ \_ \_ \_ \_]))
    (should (win? [\_ \_ \_ \x \x \x \_ \_ \_]))
    (should (win? [\_ \_ \_ \_ \_ \_ \x \x \x]))
    (should-not (win? [\x \x \_ \x \_ \_ \_ \_ \_]))
    (should (win? [\x \_ \_ \x \_ \_ \x \_ \_]))
    (should (win? [\_ \x \_ \_ \x \_ \_ \x \_]))
    (should (win? [\_ \_ \x \_ \_ \x \_ \_ \x]))
    (should (win? [\x \_ \_ \_ \x \_ \_ \_ \x]))
    (should (win? [\_ \_ \x \_ \x \_ \x \_ \_]))))