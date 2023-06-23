(ns tictactoe.database
  (:require [clojure.string :as str]
            [tictactoe.game-mode :as game-mode]
            [clojure.java.jdbc :refer :all]))

(defprotocol Database
  (initialize [this])
  (fetch-all-games [this])
  (update-game [this date board game-mode])
  (delete-game [this date]))

(defn delete-date [string date]
  (if-not (empty? string)
    (as-> (str/split-lines string) games
          (map read-string games)
          (remove #(= date (:date %)) games)
          (str/join "\n" games))))

(defrecord FileDatabase [file-name]
  Database
  (initialize [this]
    (spit file-name "" :append true))
  (fetch-all-games [this]
    (let [data (slurp file-name)]
      (if-not (empty? data)
        (map read-string (str/split-lines data)))))
  (delete-game [this date]
    (let [data (slurp file-name)]
      (spit file-name (delete-date data date))))
  (update-game [this date board game-mode]
    (delete-game this date)
    (let [data (slurp file-name)]
      (if-not (empty? data)
        (as-> (str/split-lines data) games
              (map read-string games)
              (conj games {:date date :board board :gamemode (game-mode/to-string game-mode)})
              (str/join "\n" games)
              (spit file-name (str games "\n")))
        (spit file-name (str {:date date :board board :gamemode (game-mode/to-string game-mode)} "\n"))))))

(defn sql-db [file-name]
  {:classname "org.sqlite.JDBC" :subprotocol "sqlite" :subname file-name})

(defrecord SQLDatabase [file-name]
  Database
  (initialize [this]
    (db-do-commands (sql-db file-name)
                    "CREATE TABLE IF NOT EXISTS GAMES
                    (DATE text,
                     BOARD blob,
                     GAMEMODE text)"))
  (fetch-all-games [this]
    (map #(assoc % :board (read-string (:board %)))
         (query (sql-db file-name) ["select * from games"])))
  (delete-game [this date]
    (execute! (sql-db file-name) ["DELETE FROM games WHERE date = ?" date]))
  (update-game [this date board game-mode]
    (delete-game this date)
    (insert! (sql-db file-name) :games {:date date
                                        :board board
                                        :gamemode (game-mode/to-string game-mode)})))