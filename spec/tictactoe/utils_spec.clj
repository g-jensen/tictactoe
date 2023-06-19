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
    (should= 3 (tile-count [\x \o \o \_ \o \_ \_ \_ \_] \o)))

  (it "checks if a number is in a range"
    (should (in-range? 0 10 0))
    (should-not (in-range? 0 10 -1))
    (should-not (in-range? 0 10 -2))
    (should-not (in-range? 0 10 10))
    (should-not (in-range? 0 10 11)))

  (it "gets the indices of the empty tiles of a board"
    (should= (range 0 9) (empty-indices (empty-board 3)))
    (should= (range 1 9) (empty-indices (first-move-board 3)))
    (should= [0 1 2] (empty-indices [\_ \_ \_ \o \o \o])))

  (it "checks if input is valid"
    (should-not (input-valid? "" []))
    (should (input-valid? "1" [:a]))
    (should-not (input-valid? "g" [:a]))
    (should-not (input-valid? "0g" [:a]))
    (should-not (input-valid? "2" [:a]))
    (should-not (input-valid? "-1" [:a]))
    (should (input-valid? "2" [:a :b :c]))
    (should (input-valid? "3" [:a :b :c]))
    (should (input-valid? "10" (repeat 16 0))))

  (it "converts a board to a string"
    (should= "_ _ _\n_ _ _\n_ _ _" (board->str (empty-board 3)))
    (should= "x _ _\n_ _ _\n_ _ _" (board->str (first-move-board 3)))
    (should= "_ _ _ _\n_ _ _ _\n_ _ _ _\n_ _ _ _" (board->str (empty-board 4)))
    (should= "x _ _ _\n_ _ _ _\n_ _ _ _\n_ _ _ _" (board->str (first-move-board 4)))))