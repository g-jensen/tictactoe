(ns tictactoe.game-state
  (:require [tictactoe.board-state :as board-state]
            [tictactoe.database :as database]
            [tictactoe.ui :as ui]
            [tictactoe.utils :as utils]
            [tictactoe.game-mode :as game-mode]))

(defn initial-state []
  (let [state (ui/evaluate-menu ui/game-mode-menu)
        date (utils/now)
        board (:init-board (:gamemode state))]
    (ui/display-guide board)
    (as-> (assoc state :date date) state
          (assoc state :board board))))

(defn update-state [state]
  (let [game-mode (:gamemode state)
        board (:board state)
        date (:date state)
        old-date (:old-date state)
        database (:database state)]
    (ui/display-board board)
    (database/delete-game database old-date)
    (database/update-game database date board game-mode)
    (assoc state :board (game-mode/next-board game-mode board))))

(defn over? [state]
  (let [board (:board state)]
    (board-state/game-over? board)))

(defn clean-up [state]
  (let [board (:board state)
        date (:date state)
        database (:database state)]
    (ui/display-board board)
    (ui/display-game-over-message board)
    (database/delete-game database date)))