(ns tictactoe.utils-spec
  (:require [speclj.core :refer :all]
            [tictactoe.utils :refer :all]))

(defn first-move-board [n]
  (vec (cons \x (repeat (dec (* n n)) empty-tile))))

(describe "TicTacToe Utilities"
  (it "initializes an empty 3x3 board"
    (should= (repeat 9 empty-tile) (empty-board 3)))

  (it "counts the amount of times a tile is on a board"
    (should= 9 (tile-count (empty-board 3) empty-tile))
    (should= 0 (tile-count (empty-board 3) \x))
    (should= 1 (tile-count [\x \_ \_ \_ \_ \_ \_ \_ \_] \x))
    (should= 3 (tile-count [\x \o \o \_ \o \_ \_ \_ \_] \o))
    (should= 27 (tile-count (repeat 3 (empty-board 3)) empty-tile)))

  (it "checks if a number is in a range"
    (should (in-range? 0 10 0))
    (should-not (in-range? 0 10 -1))
    (should-not (in-range? 0 10 -2))
    (should-not (in-range? 0 10 10))
    (should-not (in-range? 0 10 11)))

  (it "gets the indices of the empty tiles of a board"
    (should= (range 0 9) (empty-indices (empty-board 3)))
    (should= (range 1 9) (empty-indices (first-move-board 3)))
    (should= [0 1 2] (empty-indices [\_ \_ \_ \o \o \o]))
    (should= (range 0 27) (empty-indices (repeat 3 (empty-board 3)))))

  (it "checks if input is valid"
    (should (input-valid? 1 [:a]))
    (should-not (input-valid? 2 [:a]))
    (should-not (input-valid? -1 [:a]))
    (should (input-valid? 2 [:a :b :c]))
    (should (input-valid? 3 [:a :b :c]))
    (should (input-valid? 10 (repeat 16 0))))

  (it "checks if a board is 3 dimensional"
    (should-not (board-3d? (empty-board 3)))
    (should (board-3d? (repeat 3 (empty-board 3))))))