(ns tictactoe.sql-database
  (:require [tictactoe.database :refer :all]
            [clojure.java.jdbc :refer :all]
            [tictactoe.game-mode :as game-mode]
            [tictactoe.game-state :as gs]))

(def file-name "games.db")
(def sql-db {:classname "org.sqlite.JDBC" :subprotocol "sqlite" :subname file-name})

(defmethod gs/db-initialize :sql [_]
  (db-do-commands sql-db
                  "CREATE TABLE IF NOT EXISTS GAMES
                  (DATE text,
                   BOARD blob,
                   GAMEMODE blob)"))

(defmethod gs/db-fetch-games :sql [_]
  (map #(assoc % :board (read-string (:board %))
                 :gamemode (read-string (:gamemode %)))
       (query sql-db ["select * from games"])))

(defmethod gs/db-delete-game :sql [_ date]
  (execute! sql-db ["DELETE FROM games WHERE date = ?" date]))

(defmethod gs/db-update-game :sql [state date board game-mode]
  (gs/db-delete-game state date)
  (insert! sql-db :games {:date date
                                      :board board
                                      :gamemode (game-mode/to-map game-mode)}))