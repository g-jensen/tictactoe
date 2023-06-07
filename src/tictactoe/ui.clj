(ns tictactoe.ui
  (:require [clojure.string :as str]
            [tictactoe.game-state :as game-state]
            [tictactoe.move :as move]))

(defn board->str [board]
  (->> (partition 3 board)
       (map #(str/join " " %))
       (str/join "\n")))

(defn display-board [board]
  (println (board->str board)))

(defn display-game-over-message [board]
  (if (game-state/win? board)
    (println (str (move/winner board) " has won!"))
    (println "tie!")))

(defn display-game-modes [game-modes]
  (doall
    (for [i (range 0 (count game-modes))]
      (println (str (inc i) ": " (:name (nth game-modes i)))))))

(defn display-game-modes-prompt [game-modes]
  (println "Pick a game-mode:")
  (display-game-modes game-modes))

(defn prompt-game-mode [game-modes]
  (:mode (get game-modes (dec (Integer/parseInt (read-line))))))
