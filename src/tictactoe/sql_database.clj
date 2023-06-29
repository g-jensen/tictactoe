(ns tictactoe.sql-database
  (:require [tictactoe.database :refer :all]
            [clojure.java.jdbc :refer :all]
            [tictactoe.game-mode :as game-mode]))

(defn sql-db [file-name]
  {:classname "org.sqlite.JDBC" :subprotocol "sqlite" :subname file-name})

(defrecord SQLDatabase [file-name]
  Database
  (initialize [this]
    (db-do-commands (sql-db file-name)
                    "CREATE TABLE IF NOT EXISTS GAMES
                    (DATE text,
                     BOARD blob,
                     GAMEMODE blob)"))

  (fetch-all-games [this]
    (map #(assoc % :board (read-string (:board %))
                   :gamemode (read-string (:gamemode %)))
         (query (sql-db file-name) ["select * from games"])))

  (delete-game [this date]
    (execute! (sql-db file-name) ["DELETE FROM games WHERE date = ?" date]))

  (update-game [this date board game-mode]
    (delete-game this date)
    (insert! (sql-db file-name) :games {:date date
                                        :board board
                                        :gamemode (game-mode/to-map game-mode)})))