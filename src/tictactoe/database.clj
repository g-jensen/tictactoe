(ns tictactoe.database
  (:require [clojure.string :as str]
            [tictactoe.game-mode :as game-mode]))

(def database-file "games.txt")

(defn delete-date [string date]
  (if-not (empty? string)
    (as-> (str/split-lines string) games
          (map read-string games)
          (remove #(= date (:date %)) games)
          (str/join "\n" games))))

(defn delete-game [date]
  (let [data (slurp database-file)]
    (spit database-file (delete-date data date))))

(defn fetch-all-games []
  (let [data (slurp database-file)]
    (if-not (empty? data)
      (map read-string (str/split-lines data)))))

(defn update-game [date board game-mode]
  (delete-game date)
  (let [data (slurp database-file)]
    (if-not (empty? data)
      (as-> (str/split-lines data) games
            (map read-string games)
            (conj games {:date date :board board :game-mode (game-mode/to-string game-mode)})
            (str/join "\n" games)
            (spit database-file (str games "\n")))
      (spit database-file (str {:date date :board board :game-mode (game-mode/to-string game-mode)} "\n")))))