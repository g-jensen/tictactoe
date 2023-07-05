(ns tictactoe.game-state)

(defmulti run-tictactoe :ui)

(defmulti next-state :state)

(defmulti ui-components :state)

(defmulti db-initialize :database)

(defmulti db-fetch-games :database)

(defmulti db-delete-game :database)

(defmulti db-update-game (fn [state _ _ _] (:database state)))