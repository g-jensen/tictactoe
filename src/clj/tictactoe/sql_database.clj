(ns tictactoe.sql-database
  (:require [clojure.java.jdbc :refer :all]
            [tictactoe.game-state :as gs]))

(def file-name "games.db")
(def sql-db {:classname "org.sqlite.JDBC" :subprotocol "sqlite" :subname file-name})

(defmethod gs/db-initialize :sql [_]
  (db-do-commands sql-db
                  (str "CREATE TABLE IF NOT EXISTS GAMES"
                       "(DATE text,"
                       "BOARD blob,"
                       "VERSUSTYPE blob,"
                       "DIFFICULTY blob)")))

(defmethod gs/db-fetch-games :sql [_]
  (map #(dissoc (assoc % :board (read-string (:board %))
                         :versus-type (read-string (:versustype %))
                         :difficulty (if-not (empty? (:difficulty %)) (read-string (:difficulty %))))
                :versustype)
       (query sql-db ["select * from games"])))

(defmethod gs/db-delete-game :sql [_ date]
  (execute! sql-db ["DELETE FROM games WHERE date = ?" date]))

(defmethod gs/db-update-game :sql [state]
  (gs/db-delete-game state (:date state))
  (insert! sql-db :games {:date (str (:date state))
                          :board (str (vec (:board state)))
                          :versustype (str (:versus-type state))
                          :difficulty (str (:difficulty state))}))