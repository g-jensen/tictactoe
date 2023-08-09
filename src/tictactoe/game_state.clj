(ns tictactoe.game-state
  (:require [tictactoe.board-state :as board-state]
            [tictactoe.game-mode :as game-mode]
            [tictactoe.move :as move]
            [tictactoe.utils :as utils])
  (:import (tictactoe.game_mode PvCGame)))

(defmulti run-tictactoe :ui)
(defmulti next-state :state)
(defmulti ui-components :state)
(defmulti db-initialize :database)
(defmulti db-fetch-games :database)
(defmulti db-delete-game :database)
(defmulti db-update-game (fn [state _ _ _] (:database state)))
(defmulti next-board :ui)

(defn db-save-game [state]
  (db-delete-game state (:old-date state))
  (db-update-game state (:date state) (:board state) (:gamemode state)))

(defn over? [state]
  (let [board (:board state)]
    (board-state/game-over? board)))

(defn clean-up [state]
  (let [date (:date state)]
    (db-delete-game state date)))

(defn init-gamemode [state]
  (let [size (:board-size state)
        difficulty (:difficulty state)]
    (if (= :pvp (:versus-type state))
      (game-mode/->PvPGame size (:board state))
      (game-mode/->PvCGame size (:board state) difficulty))))

(defn computer-turn? [state]
  (and (not (empty? (utils/empty-indices (:board state))))
       (instance? PvCGame (:gamemode state))
       (not= (:character state)
             (move/player-to-move (:board state)))))