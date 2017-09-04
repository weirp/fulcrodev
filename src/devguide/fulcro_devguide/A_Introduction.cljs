(ns fulcro-devguide.A-Introduction
  (:require-macros [cljs.test :refer [is]])
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [devcards.core :as dc :refer-macros [defcard defcard-doc]]))

(defcard-doc
  "# Introduction

  Welcome to Fulcro!

  This guide will walk you through the various parts of Fulcro in a way that should then allow you to easily develop
  your own full-stack web applications!

  Note that you can navigate to the table of contents any time using the `devcards` link at the top of the page.

  ## What is Fulcro?

  Fulcro is a set of libraries that together comprise a full-stack, feature-complete web development toolkit.

  ## Why Clojure and Clojurescript?

  1. *Dramatically simpler language.*
  +
  Clojure has very little syntax. At first this seems a liability until you realize the
  amount of boilerplate it eliminates. What's more, because the language is written
  as a data structure it means that metaprogramming (augmenting the compiler to do
  something new) is nearly as easy as writing regular code. This means things like
  domain-specific languages are easy to create for your specialized problems. A typical
  Clojure program is significantly shorter than what you're used to in Java or Javascript.
  +
  2. *First-class immutable (persistent) data structures.*
  +
  If you're coming from the non-functional world this seems like a very odd thing at
  first.
  +
  3. *The code itself is written in a data language, which means you can easily transmit it, store it, transform it, etc.*
  4. *Great support for concurrency.*
  5. *One language on both the front and back-end that isn't Javascript + Node.*

  It is likely that you can dive in and start playing with Fulcro without knowing too
  much about Clojure, but you should check out a book like
  \"Clojure for the Brave and True\" to at least get through the basics.


  ## How is Fulcro related to Om (Next)?

  The Fulcro Client and Server libraries serve as an implementation of the network layer, plumbing, and default
  application database format. This means that the vast majority of the important and ground-breaking features of
  Om Next are simply augmented to make them much simpler and easier to get started with.

  Think of the base Fulcro libraries as the glue that ties the overall full-stack story together *for* Om Next.

  Other features (such as the i18n integration, support viewer, datomic helpers, websockets, etc.) are additional
  libraries that further leverage the Fulcro additions to give you an even more complete stack on which to develop
  your next killer app!

  ## About this guide

  This guide is written in Bruce Hauman's excellent Devcards. As such, these documents are live code!

  This file, for example, is in `src/devguide/fulcro_devguide/A_Introduction.cljs`. If you followed the README to start
  up this project, then you're reading this file through your browser and Bruce's other great tool Figwheel. The
  combination of the two bring you documentation that runs and also hot reloads whenever the files are saved.

  If you open this file in an editor, edit it and save, you'll see the browser automatically refresh the view.

  The box below, for example, is generated by a devcard:

  ")

(defcard sample-card (dom/div nil "The following number is calculated: " (+ 3 8)))

(defcard-doc
  "
  Open up the `A_Introduction.cljs`, search for `sample-card`, edit the numbers, save, and watch this page refresh. You
  are encouraged to play with the source code and examples in the guide to verify your understanding as you read.
  Devcards support state as well, and will track it in an atom for you. Thus, you can generate UI that actually responds
  to user interaction:
  ")
(defcard interactive-card
         (fn [state-atom owner]                             ;wrapper function that can accept a state atom
           (dom/div nil "A single top-level element."
                    (dom/span nil (str "value of x: " (:x @state-atom)))
                    (dom/br nil)
                    (dom/button #js {:onClick #(swap! state-atom update-in [:x] inc)} "Click me")))
         {:x 2}                                             ; This is a map of initial state that devcards puts in an atom
         {:inspect-data true})                              ; options....show me the current value of the data

(defcard-doc
  "
  Notice that if you edit the code in the card above and save that it *does not* lose track of state. Figwheel does hot
  code reloading and devcards is therefore able to hold onto the state of the component. Thus, if you make dramatic
  changes to something and the saved state no longer makes sense then you will need to reload the page via the browser
  to clear that state.

  IMPORTANT IF YOU GET STUCK:

  First, if there is no obvious error in the browser try reloading the page.

  If you make a typo or language error Figwheel will usually describe it pretty well in the browser.
  However, it is possible to get the whole thing stuck. Typing `(reset-autobuild)` in the REPL will clean the sources and
  rebuild (and you'll see compile errors there). Correct the errors and everything should start
  working again.

  Try NOT to kill the REPL and restart, as that will cause you a lot of waiting as
  you get compile errors, edit, and restart. (If you do kill the REPL,
  you might even consider using git to undo your changes so that it will restart cleanly).

  Occasionally you might see weird behavior (all of these tools are relatively new). At that point it
  might make sense to kill the REPL, use git to get to a known good source state, and
  clean things with `lein clean` and then restart.

  ## Notes on documentation

  Om wrappers on plain DOM elements take as their second parameter a javascript map (not a cljs one) or nil. As such, you
  usually write your UI like this:

  ```
  (dom/div #js {:onClick (fn [evt] ...) })
  ```

  in some of the examples you'll see this instead:

  ```
  (dom/div (clj->js {:onClick (fn [evt] ...) }))
  ```

  The two are equivalent. The latter will do a recursive transform, which can be handy.

  [Let's start with a Quick Tour.](#!/fulcro_devguide.A_Quick_Tour)

  ")
