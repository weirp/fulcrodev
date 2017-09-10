(ns solutions.putting-together.soln-ex-1
  (:require [om.next :as om :refer-macros [defui]]
            [om.dom :as dom]
            [fulcro.client.core :as fc]
            [fulcro.client.data-fetch :as df]))

(defui ^:once TodoItem
  static fc/InitialAppState
  (initial-state [this {:keys [id label complete]}] {:item/id id :item/label label :item/done complete})
  static om/IQuery
  (query [this] [:item/id :item/label :item/done])
  static om/Ident
  (ident [this props] [:items/by-id (:item/id props)])
  Object
  (render [this]
          (let [{:keys [item/id item/label item/done]} (om/props this)]
            (dom/li nil
                    (dom/input #js {:type "checkbox" :checked (boolean done)})
                    label
                    (dom/button #js {} "X")))))

(def ui-item (om/factory TodoItem))

(defui ^:once ItemList
  static fc/InitialAppState
  (initial-state [this params] {:list/title "My List" :list/items [(fc/initial-state TodoItem {:id 1 :label "A" :complete false})
                                                                   (fc/initial-state TodoItem {:id 2 :label "B" :complete false})
                                                                   (fc/initial-state TodoItem {:id 3 :label "C" :complete true})
                                                                   (fc/initial-state TodoItem {:id 4 :label "D" :complete false})]})
  static om/IQuery
  (query [this] [:list/title {:list/items (om/get-query TodoItem)}])
  static om/Ident
  (ident [this props] [:lists/by-title (:list/title props)])
  Object
  (render [this]
    (let [{:keys [list/title list/items]} (om/props this)]
      (dom/div nil
        (dom/h4 nil title)
        (dom/input #js {}) (dom/button nil "Add")
        (dom/ol nil (map ui-item items))))))

(def ui-item-list (om/factory ItemList))

(defui ^:once TodoList
  static fc/InitialAppState
  (initial-state [this params] {:ui/react-key "A" :item-list (fc/initial-state ItemList nil)})
  static om/IQuery
  (query [this] [:ui/react-key {:item-list (om/get-query ItemList)}])
  Object
  (render [this]
    (let [{:keys [ui/react-key item-list]} (om/props this)]
      (dom/div #js {:key react-key}
        (ui-item-list item-list)))))
