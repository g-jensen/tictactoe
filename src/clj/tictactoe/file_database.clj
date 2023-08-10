(ns clj.tictactoe.file-database
  (:require [clojure.string :as str]
            [clj.tictactoe.game-state :as gs]
            [clj.tictactoe.game-mode :as game-mode]))

(defn delete-date [string date]
  (if-not (empty? string)
    (as-> (str/split-lines string) games
          (map read-string games)
          (remove #(= date (:date %)) games)
          (str/join "\n" games))))

(def file-name "games.txt")

(defmethod gs/db-initialize :file [_]
  (spit file-name "" :append true))

(defmethod gs/db-fetch-games :file [_]
  (let [data (slurp file-name)]
    (if-not (empty? data)
      (map read-string (str/split-lines data)))))

(defmethod gs/db-delete-game :file [_ date]
  (let [data (slurp file-name)]
    (spit file-name (delete-date data date))))

(defmethod gs/db-update-game :file [state date board game-mode]
  (gs/db-delete-game state date)
  (let [data (slurp file-name)]
    (if-not (empty? data)
      (as-> (str/split-lines data) games
            (map read-string games)
            (conj games {:date date :board board :gamemode (game-mode/to-map game-mode)})
            (str/join "\n" games)
            (spit file-name (str games "\n")))
      (spit file-name (str {:date date :board board :gamemode (game-mode/to-map game-mode)} "\n")))))