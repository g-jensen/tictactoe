(ns tictactoe.console-game
  (:require [clojure.string :as str]
            [tictactoe.board-state :as board-state]
            [tictactoe.game-state :as gs]
            [tictactoe.file-database]
            [tictactoe.menu]
            [tictactoe.move :as move]
            [tictactoe.utils :as utils]))

(defn evaluate-menu []
  (loop [state {:state :database}]
    (if (= :done (:state state))
      state
      (do (println (:label (gs/ui-components state)))
          (println (str/join "\n" (:options (gs/ui-components state))))
          (recur (gs/next-state state (read-line)))))))

(defn initial-state []
  (let [menu-state (evaluate-menu)]
    (as-> menu-state state
          (assoc state :gamemode (gs/init-gamemode state))
          (assoc state :date (utils/now)))))

(defn print-board [board]
  (if (utils/board-3d? board)
    (doall (map #(println (str (utils/board->str %) "\n")) board))
    (do (println (utils/board->str board))
        (println))))

(defn print-game-over-message [board]
  (if (board-state/win? board)
    (println (str (board-state/winner board) " has won!"))
    (println "tie!"))
  (println "\nPlay Again:"))

(defn console-draw [state]
  (let [board (:board state)
        over? (:over? state)]
    (print-board board)
    (if over?
      (print-game-over-message board))))

(defmethod gs/next-board :console [state]
  (let [board (:board state)
        difficulty (:difficulty state)]
    (if (gs/computer-turn? state)
      (assoc state :board (move/play-move board (move/get-computer-move difficulty board)))
      (assoc state :board (move/play-move board (move/get-user-move board))))))

(defmethod gs/run-tictactoe :console [state]
  (loop [state (merge state (initial-state))]
    (console-draw state)
    (if (:over? state)
      (do (gs/clean-up state)
          (recur (assoc (initial-state) :ui :console)))
      (let [state (gs/update-state state)]
        (gs/db-save-game state)
        (recur state)))))