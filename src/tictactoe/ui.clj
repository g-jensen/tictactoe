(ns tictactoe.ui
  (:require [clojure.string :as str]
            [tictactoe.game-engine :refer :all]))

(defn board->str [board]
  (->> (partition 3 board)
       (map #(str/join " " %))
       (str/join "\n")))

(defn display-board [board]
  (println (board->str board)))

(defn display-winning-message [board]
  (println (str (winner board) " has won!")))

(defn get-user-move []
  (Integer/parseInt (read-line)))