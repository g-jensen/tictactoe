(ns tictactoe.components-spec
  (:require-macros [speclj.core :refer [describe it should=]])
  (:require [speclj.core]
            [tictactoe.components :as components]))

(describe "hello-world component"
  (it "says hello to the entire world"
    (should= [:div "Hello, world!"] (components/bar))))