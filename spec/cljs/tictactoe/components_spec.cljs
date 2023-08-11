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

  (it "checks if a board-size counter is valid"
    (should (components/greater-than-two 3))
    (should-not (components/greater-than-two 2))
    (should (components/greater-than-two 4)))

  (it "has a board"
    (should= 12 (count (components/board)))
    (components/change-state :board (repeat 3 (utils/empty-board 3)))
    (should= 36 (count (components/board))))

  (it "resets state"
    (let [init-2d {:state :done :dimension 2 :board-size 3 :board (utils/empty-board 3) :versus-type :pvp :difficulty :easy :character "x" :over? false}
          init-2d-pvc {:state :done :dimension 2 :board-size 3 :board [\_ \_ \_ \_ \_ \_ \_ \_ \x] :versus-type :pvc :difficulty :hard :character "o" :over? false}
          init-3d {:state :done :dimension 3 :board-size 3 :board (repeat 3 (utils/empty-board 3)) :versus-type :pvp :difficulty :easy :character "x" :over? false}]
      (components/reset-state)
      (should= init-2d @components/state)
      (components/change-state :board [\x \_ \_ \_ \_ \_ \_ \_ \_])
      (components/reset-state)
      (should= init-2d @components/state)
      (components/change-state :dimension 3)
      (components/reset-state)
      (should= init-3d @components/state)
      (components/change-state :versus-type :pvp)
      (components/reset-state)
      (should= init-3d @components/state)
      (components/change-state :dimension 2)
      (components/change-state :versus-type :pvc)
      (components/change-state :character "o")
      (components/change-state :difficulty :hard)
      (components/reset-state)
      (should= init-2d-pvc @components/state))))