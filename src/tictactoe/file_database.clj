(ns tictactoe.file-database
  (:require [clojure.string :as str]
            [tictactoe.database :refer :all]
            [tictactoe.game-mode :as game-mode]))

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
              (conj games {:date date :board board :gamemode (game-mode/to-map game-mode)})
              (str/join "\n" games)
              (spit file-name (str games "\n")))
        (spit file-name (str {:date date :board board :gamemode (game-mode/to-map game-mode)} "\n"))))))