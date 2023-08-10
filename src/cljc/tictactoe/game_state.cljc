(ns tictactoe.game-state
  (:require [tictactoe.board-state :as board-state]
            [tictactoe.move :as move]
            [tictactoe.utils :as utils]))

(defmulti run-tictactoe :ui)
(defmulti next-state :state)
(defmulti ui-components :state)
(defmulti db-initialize :database)
(defmulti db-fetch-games :database)
(defmulti db-delete-game :database)
(defmulti db-update-game (fn [state] (:database state)))

(defn db-save-game [state]
  (db-delete-game state (:old-date state))
  (db-update-game state))

(defn over? [state]
  (let [board (:board state)]
    (board-state/game-over? board)))

(defn computer-turn? [state]
  (and (not (empty? (utils/empty-indices (:board state))))
       (= :pvc (:versus-type state))
       (not= (:character state)
             (move/player-to-move (:board state)))))

(defn clean-up [state]
  (let [date (:date state)]
    (db-delete-game state date)))

(defn- update-over [state]
  (if (over? state)
    (do (if (:database state) (clean-up state))
        (assoc state :over? true))
    state))

(defn- update-computer [state]
  (let [board (:board state)
        difficulty (:difficulty state)]
    (if (computer-turn? state)
      (assoc state :board (move/play-move board (move/get-computer-move difficulty board)))
      state)))

(defmethod next-state :done [state input]
  (let [board (:board state)]
    (if-not (nil? input)
      (let [new-state (-> (assoc state :board (move/play-move board (dec input)))
                          (update-computer))]
        (if (:database new-state)
          (do (db-initialize new-state)
              (db-save-game new-state)))
        (update-over new-state))
      state)))