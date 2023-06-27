(ns tictactoe.game-state
  (:require [tictactoe.board-state :as board-state]
            [tictactoe.database :as database]
            [tictactoe.menu :as menu]
            [tictactoe.utils :as utils]
            [tictactoe.game-mode :as game-mode]))

(defn initial-state []
  (let [state (menu/evaluate-menu menu/game-mode-menu)
        date (utils/now)
        board (:init-board (:gamemode state))]
    (as-> (assoc state :date date) state
          (assoc state :board board))))

(defn over? [state]
  (let [board (:board state)]
    (board-state/game-over? board)))

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

(defn clean-up [state]
  (let [date (:date state)
        database (:database state)]
    (database/delete-game database date)))