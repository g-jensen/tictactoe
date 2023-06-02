(ns tictactoe.ui
  (:require [clojure.string :as str]
            [tictactoe.game :refer :all]))

(defn board->str [board]
  (->> (partition 3 board)
       (map #(str/join " " %))
       (str/join "\n")))

(defn display-board [board]
  (println (board->str board)))

(defn display-winning-message [board]
  (if (win? board)
    (println (str (winner board) " has won!"))
    (println "tie!")))

(defn display-game-modes [game-modes]
  (doall
    (for [i (range 0 (count game-modes))]
      (println (str (inc i) ": " (:name (nth game-modes i)))))))

(defn get-user-move []
  (Integer/parseInt (read-line)))