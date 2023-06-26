(ns tictactoe.menu
  (:require [tictactoe.board-state :as board-state]
            [tictactoe.database :as database]
            [tictactoe.game-mode :as game-mode]
            [tictactoe.move :as move]
            [tictactoe.ui]
            [tictactoe.utils :as utils])
  (:import (tictactoe.database FileDatabase SQLDatabase)
           (tictactoe.ui ConsoleUI QuilUI)))

(defn display-options [options]
  (doall (map #(println (str (inc %) ": " (:name (get options %))))
              (range 0 (count options)))))

(defn choose-option [menu]
  (loop []
    (println (:name menu))
    (display-options (:options menu))
    (let [input (read-line)]
      (if (utils/input-valid? input (:options menu))
        (get (:options menu) (dec (Integer/parseInt input)))
        (recur)))))

(defn evaluate-menu [menu]
  (loop [menu menu]
    (let [value (get menu :value)]
      (if-not (nil? value)
        value
        (recur (choose-option menu))))))

(defn character-picker-options [state]
  [{:name "Start as X"
    :options []
    :value (assoc state :gamemode (game-mode/->PvCGame (:size state) (utils/empty-board (:size state)) (:difficulty state)))}
   {:name    "Start as O"
    :options []
    :value (assoc state :gamemode (game-mode/->PvCGame (:size state) (move/play-move (utils/empty-board (:size state)) (move/get-computer-move (:difficulty state) (utils/empty-board (:size state)))) (:difficulty state)))}])

(defn versus-options [state]
  [{:name    "Versus Player"
    :options []
    :value   (assoc state :gamemode (game-mode/->PvPGame (:size state) (utils/empty-board (:size state))))}
   {:name    "Versus Computer"
    :options [{:name    "Easy"
               :options (character-picker-options (assoc state :difficulty :easy))}
              {:name "Medium"
               :options (character-picker-options (assoc state :difficulty :medium))}
              {:name "Hard"
               :options (character-picker-options (assoc state :difficulty :hard))}]}])

(defn board-size-options [state]
  [{:name "3x3 board"
    :options (versus-options (assoc state :size 3))}
   {:name "4x4 board"
    :options (versus-options (assoc state :size 4))}])

(defn load-game-options [state]
  (let [db (:database state)]
    (database/initialize db)
    (vec (for [game (database/fetch-all-games db)]
           {:name (apply str [(:date game) ": " (:board game)])
            :options []
            :value (cond
                     (= (:gamemode game) "PvPGame") (merge state {:gamemode (game-mode/->PvPGame (board-state/board-size (:board game)) (:board game)) :old-date (:date game)})
                     (= (:gamemode game) "PvCGame :easy") (merge state {:gamemode (game-mode/->PvCGame (board-state/board-size (:board game)) (:board game) :easy) :old-date (:date game)})
                     (= (:gamemode game) "PvCGame :medium") (merge state {:gamemode (game-mode/->PvCGame (board-state/board-size (:board game)) (:board game) :medium) :old-date (:date game)})
                     (= (:gamemode game) "PvCGame :hard") (merge state {:gamemode (game-mode/->PvCGame (board-state/board-size (:board game)) (:board game) :hard) :old-date (:date game)}))}))))


(defn load-type-options [state]
  [{:name    "New Game"
    :options (board-size-options state)}
   {:name    "Load Game"
    :options (let [opts (load-game-options state)]
               (if (empty? opts)
                 [{:name    "New Game"
                   :options (board-size-options state)}]
                 opts))}])

(defn database-options [state]
  [{:name    "File Persistence"
    :options (load-type-options (assoc state :database (FileDatabase. "games.txt")))}
   {:name    "SQL Persistence"
    :options (load-type-options (assoc state :database (SQLDatabase. "games.db")))}])

(def game-mode-menu
  {:name    "Choose UI Type"
   :options [{:name    "Console UI"
              :options (database-options {:ui (ConsoleUI.)})}
             {:name    "Quil UI"
              :options (database-options {:ui (QuilUI.)})}]})