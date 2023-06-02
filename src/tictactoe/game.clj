(ns tictactoe.game
  (:require [tictactoe.board :refer :all]
            [tictactoe.move :refer :all]))

(def game-modes [{:name "Versus Player"}
                 {:name "Versus Computer"}])

(defn winning-line? [seq]
  (and (not= empty-tile (first seq))
       (apply = seq)))

(defn rows [board]
  (partition 3 board))

(defn columns [board]
  (for [i (range 0 3)]
    (map #(nth % i) (rows board))))

(defn diagonals [board]
  [(map #(nth board (* 4 %)) (range 0 3))
   (map #(nth board (* 2 (inc %))) (range 0 3))])

(defn- horizontal-win? [board]
  (boolean (some true? (map winning-line? (rows board)))))

(defn- vertical-win? [board]
  (boolean (some true? (map winning-line? (columns board)))))

(defn- diagonal-win? [board]
  (boolean (some true? (map winning-line? (diagonals board)))))

(defn win? [board]
  (or (horizontal-win? board)
      (vertical-win? board)
      (diagonal-win? board)))

(defn winner [board]
  (if (= \x (player-to-move board)) \o \x))

(defn tie? [board]
  (and (not (win? board)) (not (some #(= % empty-tile) board))))

(defn game-over? [board]
  (or (win? board) (tie? board)))