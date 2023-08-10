(ns tictactoe.menu
  (:require [tictactoe.move :as move]
            [tictactoe.utils :as utils]
            [tictactoe.game-state :as gs]
            [tictactoe.file-database]
            [tictactoe.sql-database])
  (:import (java.util Date)))

(defn now []
  (str (Date.)))

(defmethod gs/next-state :default [state input]
  (assoc state :state :database))

(defmethod gs/next-state :database [state input]
  (cond
    (= 1 input) (do (gs/db-initialize {:database :file})
                      (assoc state :database :file :state :load-type :date (now)))
    (= 2 input) (do (gs/db-initialize {:database :sql})
                      (assoc state :database :sql :state :load-type :date (now)))
    :else state))

(defmethod gs/next-state :load-type [state input]
  (cond
    (= 1 input) (assoc state :load-type :new :state :dimension)
    (= 2 input) (assoc state :load-type :load :state :select-game)
    :else state))

(defmethod gs/next-state :dimension [state input]
  (cond
    (= 1 input) (assoc state :dimension 2
                               :state :board-size)
    (= 2 input) (assoc state :dimension 3
                               :board (repeat 3 (utils/empty-board 3))
                               :state :versus-type)
    :else state))

(defmethod gs/next-state :board-size [state input]
  (if (or (nil? input) (< input 3))
    state
    (assoc state :state :versus-type
                 :board-size input
                 :board (utils/empty-board input))))

(defmethod gs/next-state :versus-type [state input]
  (cond
    (= 1 input) (assoc state :versus-type :pvp :state :done)
    (= 2 input) (assoc state :versus-type :pvc :state :difficulty)
    :else state))

(defmethod gs/next-state :difficulty [state input]
  (cond
    (= 1 input) (assoc state :difficulty :easy :state :character)
    (= 2 input) (assoc state :difficulty :medium :state :character)
    (= 3 input) (assoc state :difficulty :hard :state :character)
    :else state))

(defmethod gs/next-state :character [state input]
  (let [empty-board (if (= 3 (:dimension state))
                      (:board state)
                      (utils/empty-board (:board-size state)))]
    (cond
      (= 1 input) (assoc state :character \x
                                 :board empty-board
                                 :state :done)
      (= 2 input) (assoc state :character \o
                                 :board (move/play-move empty-board
                                                        (move/get-computer-move
                                                          (:difficulty state)
                                                          empty-board))
                                 :state :done)
      :else state)))

(defmethod gs/next-state :select-game [state input]
  (if-not (nil? input)
    (let [games (gs/db-fetch-games state)
          game (nth games (dec input) nil)]
      (cond
        (empty? games)
          (assoc state :state :dimension)
        (not (nil? game))
          (assoc (merge state game) :state :done
                                    :old-date (:date game)
                                    :date (now))
        :else
          state))
    state))

(defmethod gs/ui-components :default [_]
  (gs/ui-components {:state :database}))

(defmethod gs/ui-components :database [state]
  {:label "Database"
   :type :menu
   :options ["1. File Database" "2. SQL Database"]})

(defmethod gs/ui-components :load-type [state]
  {:label "Load Type"
   :type :menu
   :options ["1. New Game" "2. Load Game"]})

(defmethod gs/ui-components :dimension [state]
  {:label "Dimension"
   :type :menu
   :options ["1. 2D" "2. 3D (3x3x3)"]})

(defn number-options [options]
  (map #(str (inc %) ". " (nth options %)) (range 0 (count options))))

(defmethod gs/ui-components :select-game [state]
  (let [games (gs/db-fetch-games state)]
    (if (empty? games)
      {:label "Game"
       :type :menu
       :options ["1. New Game"]}
      {:label "Game"
       :type :menu
       :options (number-options (map #(str (:date %) ": " (:board %))
                                     (gs/db-fetch-games state)))})))

(defn greater-than-two? [n]
  (> n 2))

(defmethod gs/ui-components :board-size [state]
  {:label "Enter Board Size"
   :type :counter
   :initial-value 3
   :valid? greater-than-two?})

(defmethod gs/ui-components :versus-type [state]
  {:label "Versus Type"
   :type :menu
   :options ["1. Versus Player" "2. Versus Computer"]})

(defmethod gs/ui-components :difficulty [state]
  {:label "Difficulty"
   :type :menu
   :options ["1. Easy" "2. Medium" "3. Hard"]})

(defmethod gs/ui-components :character [state]
  {:label "Starting Character"
   :type :menu
   :options ["1. x" "2. o"]})

(defmethod gs/ui-components :done [state]
  {:label "Board"
   :type :board
   :dimension (:dimension state)
   :board-size (:board-size state)
   :board (:board state)})