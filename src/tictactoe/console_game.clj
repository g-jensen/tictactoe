(ns tictactoe.console-game
  (:require [clojure.string :as str]
            [tictactoe.board-state :as board-state]
            [tictactoe.database :as database]
            [tictactoe.game-state :as game-state]
            [tictactoe.game-mode :as game-mode]
            [tictactoe.menu :as menu]
            [tictactoe.utils :as utils]))

(defn over? [state]
  (let [board (:board state)]
    (board-state/game-over? board)))

(defn clean-up [state]
  (let [date (:date state)
        database (:database state)]
    (database/delete-game database date)))

(defn evaluate-menu []
  (loop [state (menu/next-state {} nil)]
    (if (= :done (:state state))
      state
      (do (println (:label (menu/ui-components state)))
          (println (str/join "\n" (:options (menu/ui-components state))))
          (recur (menu/next-state state (read-line)))))))

(defn init-gamemode [state]
  (let [size (:board-size state)
        difficulty (:difficulty state)]
    (if (= :pvp (:versus-type state))
      (game-mode/->PvPGame size (:board state))
      (game-mode/->PvCGame size (:board state) difficulty))))

(defn initial-state []
  (let [menu-state (evaluate-menu)]
    (as-> menu-state state
          (assoc state :gamemode (init-gamemode state))
          (assoc state :date (utils/now)))))

(defn update-state [state]
  (let [game-mode (:gamemode state)
        board (:board state)
        date (:date state)
        old-date (:old-date state)
        database (:database state)]
    (database/delete-game database old-date)
    (database/update-game database date board game-mode)
    (as-> (assoc state :board (game-mode/next-board game-mode board)) state
          (assoc state :over? (over? state)))))

(defn print-board [board]
  (println (utils/board->str board))
  (println))

(defn print-game-over-message [board]
  (if (board-state/win? board)
    (println (str (board-state/winner board) " has won!"))
    (println "tie!"))
  (println "\nPlay Again:"))

(defn console-initialize []
  (println "TicTacToe Game"))

(defn console-draw [state]
  (let [board (:board state)
        over? (:over? state)]
    (print-board board)
    (if over?
      (print-game-over-message board))))

(defmethod game-state/run-tictactoe :console [_]
  (console-initialize)
  (loop [state (initial-state)]
    (console-draw state)
    (if (over? state)
      (do (clean-up state)
          (recur (initial-state)))
      (recur (update-state state)))))