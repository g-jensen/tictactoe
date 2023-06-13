(ns tictactoe.utils
  (:require [clojure.string :as str]))

(def empty-tile \_)
(def empty-board (vec (repeat 9 empty-tile)))

(defn tile-count [board tile]
  (count (filter #(= tile %) board)))

(defn in-range? [start end n]
  (and (>= n start) (< n end)))

(defn empty-indices [board]
  (filter #(= empty-tile (nth board %)) (range 0 (count board))))

(defn input-valid? [input options]
  (and (= 1 (count input))
       (Character/isDigit (first input))
       (in-range? 1 (inc (count options)) (Integer/parseInt input))))

(defn board->str [board]
  (->> (partition 3 board)
       (map #(str/join " " %))
       (str/join "\n")))