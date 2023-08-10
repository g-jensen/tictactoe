(ns tictactoe.components
  (:require [reagent.core :as r]))

;; Form-3 Component
(defn foo []
  (r/create-class {:reagent-render (fn [] [:div "My, world!"])}))

;; Form-1 Component
(defn bar []
  [:div "Hello, world!"])

;; Form-2 Component
(defn baz []
  (fn []
    [:div "Hello, world!"]))

(defn main []
  [:div
   [foo]
   [bar]
   [baz]])