(ns tictactoe.components-spec
  (:require-macros [speclj.core :refer [describe context it should= should-contain should should-not before]])
  (:require [speclj.core]
            [tictactoe.components :as components]
            [tictactoe.utils :as utils]))

(describe "A Collection of TicTacToe Components"
  (before (swap! components/state (constantly components/initial-state)))

  (it "has an initial state"
    (should= components/initial-state @components/state))

  (it "changes state to value"
    (components/change-state :dimension 3)
    (should= 3 (:dimension @components/state)))

  (it "changes state with function"
    (components/change-state :dimension dec)
    (should= 1 (:dimension @components/state)))

  (it "has a banner component"
    (should= [:div [:h1 "TicTacToe"]] (components/banner)))

  (it "creates a padded label"
    (should= [:label {:style {:padding-right "10px"}} "hi"]
             (components/padded-label "hi"))
    (should= [:label {:style {:padding-right "10px"}} "greg"]
             (components/padded-label "greg")))

  (it "creates a menu"
    (should-contain '([:option {:value "opt1"} opt1]
                      [:option {:value "opt2"} opt2])
             (nth (components/menu :my-menu ["opt1" "opt2"] #()) 2))
    (should-contain '([:option {:value "opt3"} opt3]
                      [:option {:value "opt4"} opt4])
             (nth (components/menu :my-menu1 ["opt3" "opt4"] #()) 2)))

  (it "has a board"
    (should= 12 (count (components/board)))
    (components/change-state :board (repeat 3 (utils/empty-board 3)))
    (should= 36 (count (components/board)))))