(ns tictactoe.components
  (:require [cljs.reader :as reader]
            [reagent.core :as r]
            [tictactoe.game-state :as gs]
            [tictactoe.move :as move]
            [tictactoe.utils :as utils]))

(def initial-state {:state :done :dimension 2 :board-size 3 :board (utils/empty-board 3) :versus-type :pvp :difficulty :easy :character \x})
(def state (r/atom initial-state))

(defn change-state [key value]
  (if (fn? value)
    (swap! state assoc key (value (key @state)))
    (swap! state assoc key value)))

(defn banner []
  [:div [:h1 "TicTacToe"]])

(defn padded-label [name]
  [:label {:style {:padding-right "10px"}} name])

(defn- option-name [opt]
  (let [val (reader/read-string opt)]
    (if (keyword? val)
      (name val)
      val)))

(defn menu [label options do-fn]
  [:div
   (padded-label (name label))
   [:select {:on-change #(do (change-state label (reader/read-string (.. % -target -value))) (do-fn))}
    (doall (for [opt options] [:option {:value opt} (option-name opt)]))]])

(defn- try-change [label valid? f]
  (if (valid? (f (label @state)))
    (change-state label f)))

(defn counter [label valid? do-fn]
  [:div
   (padded-label (name label))
   (padded-label (label @state))
   [:input {:type "button" :value "-" :on-click #(do (try-change label valid? dec) (do-fn))}]
   [:input {:type "button" :value "+" :on-click #(do (try-change label valid? inc) (do-fn))}]])

(defn- build-tile [idx]
  (let [board (if (utils/board-3d? (:board @state)) (flatten (:board @state)) (:board @state))
        size (:board-size @state)
        real-idx (- idx (quot (inc idx) (inc size)))]
    (if (zero? (mod (inc idx) (inc size)))
      [:br]
      [:input {:type "button" :value (nth board real-idx)
               :on-click #(swap! state (fn [x] (gs/next-state x (inc real-idx))))}])))

(defn board []
  (let [board (:board @state)
        size (:board-size @state)]
    (if (utils/board-3d? board)
      (map build-tile (range 0 36))
      (map build-tile (range 0 (* size (inc size)))))))

(defn greater-than-two [n] (> n 2))

(defn reset-state []
  (change-state :board (utils/empty-board (:board-size @state)))
  (when (= 3 (:dimension @state))
    (change-state :board (repeat 3 (utils/empty-board 3)))
    (change-state :board-size 3))
  (when (= :pvp (:versus-type @state))
    (change-state :difficulty :easy)
    (change-state :character "x"))
  (if (and (= :pvc (:versus-type @state)) (= "o" (:character @state)))
    (change-state :board (move/play-move (:board @state) (move/get-computer-move (:difficulty @state) (:board @state)))))
  (change-state :over? false))

(defn main []
  [:div
   (banner)
   (menu :dimension ["2" "3"] reset-state)
   (if (= 2 (:dimension @state))
     (counter :board-size greater-than-two reset-state))
   (menu :versus-type [":pvp" ":pvc"] reset-state)
   (if (= :pvc (:versus-type @state))
     (menu :difficulty [":easy" ":medium" ":hard"] reset-state))
   (if (= :pvc (:versus-type @state))
     (menu :character ["\"x\"" "\"o\""] reset-state))
   (board)
   (if (:over? @state)
     [:input {:type "button" :value "Play Again" :on-click reset-state}])])