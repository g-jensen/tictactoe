(ns tictactoe.file-database
  (:require [clojure.string :as str]
            [tictactoe.game-state :as gs]))

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

(defmethod gs/db-update-game :file [state]
  (gs/db-delete-game state (:date state))
  (let [data (slurp file-name)]
    (if-not (empty? data)
      (as-> (str/split-lines data) games
            (map read-string games)
            (conj games state)
            (str/join "\n" games)
            (spit file-name (str games "\n")))
      (spit file-name (str state "\n")))))