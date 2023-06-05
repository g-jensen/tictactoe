(ns tictactoe.game-state-spec
  (:require [speclj.core :refer :all]
            [tictactoe.board :refer :all]
            [tictactoe.game-state :refer :all]))

(describe "A TicTacToe Game State"
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
    (should (win? [\_ \_ \x \_ \x \_ \x \_ \_])))

  (it "checks if there is a tie"
    (should-not (tie? empty-board))
    (should (tie? [\o \x \x \x \o \o \o \x \x]))
    (should (tie? [\o \x \o \o \x \x \x \o \x]))
    (should-not (tie? [\x \x \x \o \o \_ \_ \_ \_]))))