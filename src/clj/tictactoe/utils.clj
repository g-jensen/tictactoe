(ns tictactoe.utils
  (:require [clojure.string :as str])
  (:import (java.util Date)))

(def empty-tile \_)
(defn empty-board [n]
  (vec (repeat (* n n) empty-tile)))

(defn board-3d? [board]
  (coll? (first board)))

(defn tile-count [board tile]
  (if (board-3d? board)
    (apply + (map #(tile-count % tile) board))
    (count (filter #(= tile %) board))))

(defn in-range? [start end n]
  (and (>= n start) (< n end)))

(defn empty-indices [board]
  (let [board (flatten board)]
    (filter #(= empty-tile (nth board %)) (range 0 (count board)))))

(defn input-valid?
  ([input] (and (not (empty? input)) (every? #(Character/isDigit %) input)))
  ([input options] (and (input-valid? input)
                        (in-range? 1 (inc (count options)) (Integer/parseInt input)))))

(defn board->str [board]
  (->> (partition (int (Math/sqrt (count board))) board)
       (map #(str/join " " %))
       (str/join "\n")))

(defn now []
  (str (Date.)))