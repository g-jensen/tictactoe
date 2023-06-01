(ns tictactoe.core
  (:require [clojure.string :as str]
            [tictactoe.board :refer :all]))

(defn board->str [board]
  (->> (partition 3 board)
       (map #(str/join " " %))
       (str/join "\n")))

(defn display-board [board]
  (println (board->str board)))

(defn in-range? [start end n]
  (and (>= n start) (< n end)))

(defn tile-count [board tile]
  (count (filter #(= tile %) board)))

(defn player-to-move [board]
  (if (= (tile-count board \x) (tile-count board \o))
    \x
    \o))

(defn move-valid? [board index]
  (and (in-range? 0 9 index)
       (= (nth board index) empty-tile)))

(defn play-move [board index]
  (if-not (move-valid? board index)
    board
    (assoc board index (player-to-move board))))

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

(defn horizontal-win? [board]
  (boolean (some true? (map winning-line? (rows board)))))

(defn vertical-win? [board]
  (boolean (some true? (map winning-line? (columns board)))))

(defn diagonal-win? [board]
  (boolean (some true? (map winning-line? (diagonals board)))))

(defn win? [board]
  (or (horizontal-win? board)
      (vertical-win? board)
      (diagonal-win? board)))

(defn winner [board]
  (if (= \x (player-to-move board)) \o \x))

(defn display-winning-message [board]
  (println (str (winner board) " has won!")))

(defn get-user-move []
  (Integer/parseInt (read-line)))

;;TODO - test main
(defn -main [& args]
  (loop [board empty-board]
    (display-board board)
    (if (win? board)
      (display-winning-message board)
      (recur (play-move board (get-user-move))))))