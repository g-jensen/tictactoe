(ns tictactoe.web-game
  (:require [clojure.string :as str]
            [tictactoe.board-state :as board-state]
            [tictactoe.game-state :as gs]
            [tictactoe.utils :as utils])
  (:import (java.net InetSocketAddress)
           (java.util.function Function)
           (org.httpserver HttpMessage HttpServer)))

(defmulti generate-html #(:type (gs/ui-components %)))
(defn get-choice [req]
  (let [body (.getBody req)]
    (if (nil? body)
      nil
      (subs (String. body) 7))))

(defn get-state [req]
  (let [cookie (.get (.getHeaderFields req) "Cookie")
        state (if (nil? cookie) {:state :database} (read-string (subs cookie 6)))
        choice (get-choice req)]
    (cond
      (:over? state) {:state :database}
      (nil? choice) state
      :else (gs/next-state state choice))))


(defn- html-button [value inner-html]
  (str "<button type=\"submit\" name=\"choice\" value=\"" value "\">" inner-html "</button>"))

(defmethod generate-html :menu [state]
  (let [comps (gs/ui-components state)
        {label :label
         opts :options} comps]
    (str label
         "<form action=\"/tictactoe\" method=\"post\">"
         (str/join "" (map-indexed (fn [idx opt] (html-button (inc idx) opt)) opts))
         "</form>")))

(defmethod generate-html :counter [state]
  (let [comps (gs/ui-components state)
        label (:label comps)]
    (str label
         "<form action=\"/tictactoe\" method=\"post\">"
         "<input type=\"number\" id=\"choice\" name=\"choice\" value=\"3\">"
         "<input type=\"submit\" value=\"Submit\">"
         "</form>")))

(defn- html-buttons [board]
  (let [board-size (board-state/board-size board)]
    (if (utils/board-3d? board)
      (->> board
           (flatten)
           (map-indexed (fn [idx opt] (html-button (inc idx) opt)))
           (map-indexed (fn [idx opt] (if (zero? (mod (inc idx) board-size)) (str opt "<br>") opt)))
           (map-indexed (fn [idx opt] (if (zero? (mod (inc idx) (* board-size board-size))) (str opt "<br>") opt))))
      (->> board
           (map-indexed (fn [idx opt] (html-button (inc idx) opt)))
           (map-indexed (fn [idx opt] (if (zero? (mod (inc idx) board-size)) (str opt "<br>") opt)))))))

(defmethod generate-html :board [state]
  (let [comps (gs/ui-components state)
        {label :label
         board :board} comps
        over "<button type=\"submit\" name=\"choice\" value=\"1\">Play Again</button>"]
    (str label
         "<form action=\"/tictactoe\" method=\"post\">"
         (str/join "" (html-buttons board))
         (if (:over? state) over "")
         "</form>")))

(defn handle [req]
  (let [state (get-state req)
        html (generate-html state)]
    (doto (HttpMessage.)
      (.setStartLine HttpMessage/HttpOK)
      (.putHeader "Content-Type" "text/html")
      (.putHeader "Content-Length" (str (count html)))
      (.putHeader "Set-Cookie" (str "state=" state))
      (.setBody (str html)))))

(defn- ->fun [f]
  (reify Function
    (apply [this arg] (f arg))))

(defmethod gs/run-tictactoe :web [state]
  (let [address (InetSocketAddress. "localhost" 8082)]
    (doto (HttpServer. address)
      (.initialize)
      (.onConnection (->fun handle))
      (.run))))