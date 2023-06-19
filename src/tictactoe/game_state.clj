(ns tictactoe.game-state
  (:require [tictactoe.utils :as utils]))

(defn winning-line? [seq]
  (and (not= utils/empty-tile (first seq))
       (apply = seq)))

(defn board-size [board]
  (int (Math/sqrt (count board))))

(defn rows [board]
  (partition (board-size board) board))

(defn columns [board]
  (for [i (range 0 (board-size board))]
    (map #(nth % i) (rows board))))

(defn diagonals [board]
  (let [size (board-size board)]
    [(map #(nth board (* (inc size) %)) (range 0 size))
     (map #(nth board (* (dec size) (inc %))) (range 0 size))]))

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
  (if (= (utils/tile-count board \x) (utils/tile-count board \o))
    \o
    \x))

(defn x-wins? [board]
  (and (win? board) (= \x (winner board))))

(defn o-wins? [board]
  (and (win? board) (= \o (winner board))))

(defn tie? [board]
  (and (not (win? board))
       (not (some #(= % utils/empty-tile) board))))

(defn game-over? [board]
  (or (win? board) (tie? board)))