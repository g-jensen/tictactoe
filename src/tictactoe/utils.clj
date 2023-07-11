(ns tictactoe.utils
  (:require [clojure.string :as str])
  (:import (java.util Date)))

(def empty-tile \_)
(defn empty-board [n]
  (vec (repeat (* n n) empty-tile)))

(defn tile-count [board tile]
  (count (filter #(= tile %) board)))

(defn in-range? [start end n]
  (and (>= n start) (< n end)))

(defn empty-indices [board]
  (filter #(= empty-tile (nth board %)) (range 0 (count board))))

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

(defn board-3d? [board]
  (coll? (first board)))