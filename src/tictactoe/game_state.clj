(ns tictactoe.game-state
  (:require [tictactoe.board-state :as board-state]
            [tictactoe.database :as database]
            [tictactoe.ui :as ui]
            [tictactoe.utils :as utils]
            [tictactoe.game-mode :as game-mode]))

(defn initial-state []
  (let [state (ui/evaluate-menu ui/game-mode-menu)]
    (as-> (assoc state :date (utils/now)) state
          (assoc state :board (:init-board (:game-mode state))))))

(defn update [state]
  (let [game-mode (:game-mode state)
        board (:board state)
        date (:date state)
        old-date (:old-date state)]
    (ui/display-board board)
    (database/delete-game old-date)
    (database/update-game date board game-mode)
    (assoc state :board (game-mode/next-board game-mode board))))

(defn over? [state]
  (let [board (:board state)]
    (board-state/game-over? board)))

(defn clean-up [state]
  (let [board (:board state)
        date (:date state)]
    (ui/display-board board)
    (ui/display-game-over-message board)
    (database/delete-game date)))