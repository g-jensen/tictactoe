(ns tictactoe.core
  (:gen-class)
  (:require [clojure.string :as str]
            [tictactoe.menu :as menu]
            [tictactoe.game-state :as game-state]
            [tictactoe.console-game]
            [tictactoe.quil-game]))

(defn -main [& args]
  (let [state {:state :ui}]
    (println (:label (menu/ui-components state)))
    (println (str/join "\n" (:options (menu/ui-components state))))
    (game-state/run-tictactoe (menu/next-state state (read-line)))))
