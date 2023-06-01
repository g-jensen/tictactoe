(ns tictactoe.ui
  (:require [clojure.string :as str]))

(defn board->str [board]
  (->> (partition 3 board)
       (map #(str/join " " %))
       (str/join "\n")))

(defn display-board [board]
  (println (board->str board)))